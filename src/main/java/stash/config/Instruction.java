package stash.config;

import java.util.List;

public class Instruction {
	
	public static final String FILE_DELIMITERS = "<>";
	
	//TODO make immutable
	private String value;
	//TODO make immutable
	private List<String> directives;
	
	private final String location;
	//TODO make immutable
	private String name;
	
	private final String key;
	
	public Instruction(String key, String val) {
		this(key, val, "");
	}

	public Instruction(String key, String val, String defaultLoc) {
		value = val;
		this.key = key;
		name = InstructionFileParser.resolveName(key);
		directives = InstructionFileParser.resolveDirectives(key);
		location = InstructionFileParser.resolveDestination(key, defaultLoc);
	}

	
	public void validate() {
		InstructionFileParser.validateEntry(location, name, value);
	}
	
	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	public List<String> getDirectives() {
		return directives;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDirectives(List<String> directives) {
		this.directives = directives;
	}

	public String getKey() {
		return key;
	}
}


