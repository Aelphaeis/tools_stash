package stash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stash.config.Configurator;

public class Stash {
	private static final String DEFAULT_LOC = "src/main/resources/StashConfig";
	private static final Logger logger = LogManager.getLogger();
	public static void main(String[] args) throws Exception {
		String location = (args.length > 0)? args[0]: DEFAULT_LOC; 
		logger.info("Processing from {} ", location);
		Configurator.execute(location);
	}
}
