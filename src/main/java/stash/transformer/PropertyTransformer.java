package stash.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.PropertiesConfigurationLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stash.config.Instruction;
import stash.directives.Directive;
import stash.exceptions.StashException;

public class PropertyTransformer extends AbstractTransformer {
	private static final Logger logger = LogManager.getLogger();
    PropertiesConfigurationLayout layout;
	PropertiesConfiguration config;

	
	public PropertyTransformer() {
		this(new TransformerFactory().getDirectives(PropertyTransformer.class));
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

	@Override
	public void transformInternal(Map<String, Instruction> kvp) {
		logger.traceEntry(null, kvp);
		for (Entry<String, Instruction> entry : kvp.entrySet()) {
			String key = entry.getKey();
			Instruction inst = entry.getValue();
			config.setProperty(key, inst.getValue());
			logger.trace("modifying {} to {}", key, inst.getValue());
		}
		logger.traceExit();
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


	
	@Override
	public void validateDirectivesInternal(Instruction i)
			throws StashException {
		//No Validation Required.
	}
}
