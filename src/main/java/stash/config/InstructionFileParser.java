package com.cruat.tools.stash.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.commons.configuration.PropertiesConfiguration.IOFactory;
import org.apache.commons.configuration.PropertiesConfiguration.PropertiesReader;
import org.apache.commons.configuration.PropertiesConfiguration.PropertiesWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cruat.tools.stash.exceptions.StashRuntimeException;

public class InstructionFileParser {
	public static final String ASSIGNMENT_DELIMITERS = "=:";
	public static final String INVALID_FILE_CHARS = "\"*<>?|";
	public static final String DIRECTIVE_DELIMITERS = "<";
	public static final String FILE_DELIMITERS = "<>";
	private static final Logger logger = LogManager.getLogger();
	final PropertiesConfigurationLayout layout;
	final PropertiesConfiguration config;
	
	final File inFile;

	public InstructionFileParser(File file) {
		inFile = file;
		config =  new CustomConfig();
		layout = new PropertiesConfigurationLayout(config);
	}

	public Instructions parseInstructions() throws IOException {
		try (InputStream is = new FileInputStream(inFile)) {
			layout.load(new InputStreamReader(is));
		} catch (ConfigurationException e) {
			String err = "unknown error occurred";
			throw new IOException(err, e);
		}
		return process(config);
	}

	static Instructions process(PropertiesConfiguration props) {
		Instructions inst = new Instructions();
		String defaultDestination = "";
		
		Iterator<String> it = props.getKeys();
		while(it.hasNext()) {
			
			String key = it.next();
			String name = resolveName(key);
			String value =  props.getProperty(key).toString();
			String destination = resolveDestination(key, defaultDestination);
			validateEntry(destination, name, value);
			
			Instruction p = new Instruction(key, value, defaultDestination);
			if(p.getName().isEmpty()) {
				defaultDestination = destination;
			}
			else {
				inst.computeIfAbsent(p.getLocation(), k -> new HashMap<>())
				.put(p.getName(), p);
			}
		}
		return inst;
	}

	static List<String> resolveDirectives(String key) {
		int index = resolveDestinationIndex(key);
		if(index == -1) {
			return new ArrayList<>();
		}
		
		int start = resolveDestinationIndex(key) + 1;
		int end = resolveDestinationIndex(key.substring(start)) + start ;
		
		
		if(end + 1 == start) {
			//format like  location> or location>name or location>name=value
			return new ArrayList<>();
		}
		
		if(start == -1 || end == -1) {
			return new ArrayList<>();
		}
		else {
			String [] directives = key.substring(start, end).split(" ");
			return Arrays.asList(directives).stream().
					map(String::toLowerCase).
					collect(Collectors.toList());
		}
	}
	

	/**
	 * Check to see that its possible to resolve a destination and that if a
	 * default destination is being set there is no value associated with it.
	 * 
	 * @param destination
	 * @param key
	 * @param value
	 */
	static void validateEntry(String destination, String key, String value) {
		if (destination.isEmpty()) {
			String err = "property missing file specifier";
			logger.error(err + "({}:{})", key, value);
			throw new StashRuntimeException(err);
		}

		if (key.isEmpty() && !value.isEmpty()) {
			String err = "value has no property name";
			throw new StashRuntimeException(err);
		}
	}

	/**
	 * Resolves the destination, if it cannot be resolved used the default
	 * 
	 * @param key
	 * @param defaultDestination
	 * @return
	 */
	static String resolveDestination(String key, String defaultDestination) {
		String destination = resolveDestination(key);
		return destination.isEmpty() ? defaultDestination : destination;
	}

	/**
	 * Resolves the destination, if it cannot be resolved return empty string
	 * 
	 * @param key
	 * @return
	 */
	static String resolveDestination(String key) {
		int index = resolveDestinationIndex(key);
		return index == -1 ? "" : key.substring(0, index);
	}
	
	static int resolveDestinationIndex(String key) {
		int index = -1;
		for (char c : FILE_DELIMITERS.toCharArray()) {
			index = key.indexOf(c);
			if (index != -1) {
				break;
			}
		}
		return index;
	}

	/**
	 * Resolves the real name of a key.
	 * 
	 * @param key
	 * @return
	 */
	static String resolveName(String key) {
		int index = -1;
		List<Integer> indexes = new ArrayList<>();
		for (char c : FILE_DELIMITERS.toCharArray()) {
			index = key.indexOf(c);
			if (index != -1) {
				indexes.add(key.lastIndexOf(c));
			}
		}
		indexes.sort(Comparator.reverseOrder());
		return indexes.isEmpty() ? key : key.substring(indexes.get(0) + 1);
	}
	
	public static class CustomConfig extends PropertiesConfiguration {
		public CustomConfig() {
			super();
			setIOFactory(new CustomIOFactory());
		}
	}

	static class CustomIOFactory implements IOFactory {
		@Override
		public PropertiesReader createPropertiesReader(Reader in,
				char delimiter) {
			return new PropertiesReader(in, delimiter) {
				@Override
				protected void parseProperty(String line) {

					Properties p = new Properties();
					InputStream is = new ByteArrayInputStream(line.getBytes());
					try {
						p.load(is);
					} catch (IOException e) {
						// should never happen
						throw new IllegalStateException(e);
					}

					String sep = "=";
					String name = "";
					String value = "";

					for (Entry<Object, Object> e : p.entrySet()) {
						name = e.getKey().toString();
						value = e.getValue().toString();
					}

					initPropertyName(name);
					initPropertyValue(value);
					initPropertySeparator(sep);
				}
			};

		}

		@Override
		public PropertiesWriter createPropertiesWriter(Writer out,
				char delimiter) {
			return new PropertiesWriter(out, delimiter);
		}
	}
}
