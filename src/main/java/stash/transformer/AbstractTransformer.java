package stash.transformer;

import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stash.config.Instruction;
import stash.directives.Directive;
import stash.directives.InstructionDirective;
import stash.exceptions.AggregationException;
import stash.exceptions.StashException;
import stash.exceptions.StashRuntimeException;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractTransformer implements Transformer {
	Set<Directive> directives;
	
	AbstractTransformer(){
		this(new HashSet<>());
	}
	
	AbstractTransformer(Set<Directive> directives) {
		this.directives = Collections.unmodifiableSet(directives);
	}
	
	protected boolean supports(String name) {
		return getDirectives()
			.stream()
			.anyMatch(p -> p.getName().equalsIgnoreCase(name));
	}
	
	protected Optional<Directive> getDirective(String name) {
		return getDirectives()
			.stream()
			.filter(p -> p.getName().equals(name))
			.findFirst();
	}
	
	@Override
	public void load(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			load(is);
		}
	}

	@Override
	public void save(File file) throws IOException {
		try (OutputStream os = new FileOutputStream(file)) {
			save(os);
		} 
	}
	
	@Override
	public Set<Directive> getDirectives() {
		return directives;
	}
	
	
	@Override
	public final void transform(Map<String, Instruction> kvp) {
		for (Entry<String, Instruction> entry : kvp.entrySet()) {
			Instruction inst = entry.getValue();
			try {
				validateDirectives(inst);
				entry.setValue(processInstructionDirective(inst));
			}
			catch(StashException e) {
				throw new StashRuntimeException(e);
			}
		}
		transformInternal(kvp);
	}
	
	private Instruction processInstructionDirective(Instruction inst) {
		List<String> dirs = inst.getDirectives();
		Map<String, InstructionDirective> applicable = getDirectives().stream()
				.filter(InstructionDirective.class::isInstance)
				.map(InstructionDirective.class::cast)
				.map(p -> new SimpleEntry<>(p.getName().toLowerCase(), p))
				.filter(p -> dirs.contains(p.getKey()))
				.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

		Instruction result = inst;
		for(String dirName : dirs) {
			InstructionDirective directive = applicable.get(dirName);
			if(directive != null) {
				result = directive.execute(result);
			}
		}
		return result;
	}
	
	public abstract void transformInternal(Map<String, Instruction> kvp);
	

	/**
	 * Validates the directives to ensure that they exist in the list of 
	 * supported directives. Throws a StashException if one directive
	 * is unsupported and throws an aggregation exception if multiple are
	 * not supported.
	 * 
	 * @param instructionDirectives
	 * @throws StashException
	 */
	public final void validateDirectives(Instruction i) 
			throws StashException {
		List<StashException> exs = new ArrayList<>();
		if(!i.getDirectives().isEmpty()) {
			for(String directive : i.getDirectives()) {
				if(!supports(directive)) {
					String err = "[%s] directive is not supported";
					exs.add(new StashException(String.format(err, directive)));
				}
			}
		}
		try {
			validateDirectivesInternal(i);
		}
		catch(AggregationException e) {
			exs.addAll(e.getExceptions(StashException.class));
		}
		catch(StashException e) {
			exs.add(e);
		}
		if(!exs.isEmpty()) {
			String err = "Multiple errors processing directives";
			throw (exs.size() > 1)? new AggregationException(err, exs) 
					: exs.get(0);
		}
	}
	
	public abstract void validateDirectivesInternal(Instruction i) 
			throws StashException;
}
