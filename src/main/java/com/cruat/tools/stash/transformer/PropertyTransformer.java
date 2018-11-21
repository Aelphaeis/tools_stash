package com.cruat.tools.stash.transformer;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BinaryOperator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cruat.tools.stash.config.Instruction;
import com.cruat.tools.stash.directives.Directive;
import com.cruat.tools.stash.directives.InstructionDirective;
import com.cruat.tools.stash.exceptions.StashException;
import com.cruat.tools.stash.utils.IgnoreKeyCaseMap;

public class PropertyTransformer extends AbstractTransformer {
	private static final Logger logger = LogManager.getLogger();
    PropertiesConfigurationLayout layout;
	PropertiesConfiguration config;

	
	public PropertyTransformer() {
		this(TransformerFactory.CACHE.get(PropertyTransformer.class));
	}
	
	public PropertyTransformer(Set<Directive> directives) {
		super(directives);
    	config = new PropertiesConfiguration();
    	layout = new PropertiesConfigurationLayout(config);
    	config.setDelimiterParsingDisabled(true);
	}

	@Override
	public void load(InputStream stream) throws IOException {
		try {
			layout.load(new InputStreamReader(stream));
		} catch (ConfigurationException e) {
			throw new IOException(e);
		}
	}

	public void transformInternal(Map<String, Instruction> kvp) {
		for (Entry<String, Instruction> entry : kvp.entrySet()) {
			String key = entry.getKey();
			
			Instruction inst = entry.getValue();
			inst = processInstructionDirective(inst);
			
			config.setProperty(key, inst.getValue());
			logger.trace("modifying {} to {}", key, inst.getValue());
		}
	}

	@Override
	public void save(OutputStream stream) throws IOException {
		try {
			layout.save(new PrintWriter(stream));
		} 
		catch (ConfigurationException e) {
			throw new IOException(e);
		}
	}

	private Instruction processInstructionDirective(Instruction inst) {
		List<String> directives = inst.getDirectives();
		Map<String, InstructionDirective> applicable = getDirectives().stream()
				.filter(InstructionDirective.class::isInstance)
				.map(InstructionDirective.class::cast)
				.map(p -> new SimpleEntry<>(p.getName().toLowerCase(), p))
				.filter(p -> directives.contains(p.getKey()))
				.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue,
						throwingMerger(),
						() -> ignoreKeyCaseMap(new HashMap<>())));

		Instruction result = inst;
		for(String dirName : directives) {
			InstructionDirective directive = applicable.get(dirName);
			if(directive != null) {
				result = directive.execute(result);
			}
		}
		return result;
	}

	private static <T> BinaryOperator<T> throwingMerger(){
        return (u,v) -> {
        	String err = String.format("Duplicate key %s", u);
        	throw new IllegalStateException(err);
    	};
	}
	private static <E> Map<String, E> ignoreKeyCaseMap(Map<String, E> m){
		return new IgnoreKeyCaseMap<>(m);
	}
	
	@Override
	public void validateDirectivesInternal(Instruction i)
			throws StashException {
		//No Validation Required.
	}
}
