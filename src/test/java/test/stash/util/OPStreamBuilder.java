package test.stash.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;

import stash.config.InstructionFileParser.CustomConfig;

public class OPStreamBuilder {
	StringBuilder builder = new StringBuilder();

	public OPStreamBuilder defaultLocation(String l) {
		builder.append(l);
		builder.append(">");
		return nl();
	}

	public OPStreamBuilder entry(String n, String v) {
		builder.append(n);
		builder.append("=");
		builder.append(v);
		return nl();
	}

	public OPStreamBuilder entry(String l, String n, String v) {
		return entry(l + ">" + n, v);
	}

	public OPStreamBuilder nl() {
		builder.append('\n');
		return this;
	}

	InputStream build() {
		return new ByteArrayInputStream(toString().getBytes());
	}

	public PropertiesConfiguration toOrderedProperties() {
		PropertiesConfiguration config = new CustomConfig();
		PropertiesConfigurationLayout layout = new PropertiesConfigurationLayout(config);
		try {
			layout.load(new InputStreamReader(build()));
			return config;
		} catch (ConfigurationException e) {
			// should never happen
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
