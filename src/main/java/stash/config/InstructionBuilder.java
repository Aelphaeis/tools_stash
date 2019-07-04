package stash.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstructionBuilder {
	
	String name;
	String value;
	String location;
	List<String> directives;
	
	public InstructionBuilder() {
		directives = new ArrayList<>();
	}
	
	public InstructionBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public InstructionBuilder value(String val) {
		this.value = val;
		return this;
	}
	
	public InstructionBuilder location(String location) {
		this.location = location;
		return this;
	}
	
	public InstructionBuilder directive(String directive) {
		directives.add(directive);
		return this;
	}
	
	public InstructionBuilder directive(Collection<String> directives) {
		this.directives.addAll(directives);
		return this;
	}
	
	public Instruction build () {
		return new Instruction(target(), value, "");
	}
	public String target() {		
		StringBuilder builder = new StringBuilder();
		if(location != null) {
			builder.append(location);
		}
		if(!directives.isEmpty()) {
			builder.append("<");
			Stream<String> stream = directives.stream();
			builder.append(stream.collect(Collectors.joining(" ")));
		}
		builder.append(">");
		if(name!= null) {
			builder.append(name);
		}
		return builder.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(target());
		if(value != null) {
			builder.append("=");
			builder.append(value);
		}
		return builder.toString();
	}
}
