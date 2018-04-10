package com.cruat.tools.stash.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cruat.tools.stash.transformer.Transformer;
import com.cruat.tools.stash.transformer.TransformerFactory;

public class Configurator {

	private static final String CONF_DIR = "src/main/resources/StashConfig";

	private static final Logger logger = LogManager.getLogger();
	
	public Instructions getInstructions(String instructionFile)
			throws IOException {
		return getInstructions(new File(instructionFile));
	}
	

	public Instructions getInstructions(File instructionFile)
			throws IOException {
		Instructions inst = new Instructions();
		if (!instructionFile.exists()) {
			String err = "Instructions were not found at ({})";
			logger.warn(err, instructionFile.getAbsolutePath());
		}

		for (File file : listFileTree(instructionFile)) {
			logger.info("processing file ({})", file.getAbsolutePath());
			InstructionFileParser p = new InstructionFileParser(file);
			Instructions i = p.parseInstructions();

			for (Entry<String, HashMap<String, Instruction>> m : i.entrySet()) {
				String k = m.getKey();
				HashMap<String, Instruction> v = m.getValue();
				inst.computeIfAbsent(k, t -> new HashMap<>()).putAll(v);
			}
		}
		return inst;
	}

	public static Set<File> listFileTree(File dir) {
		return listFileTree(dir, true);
	}

	public static Set<File> listFileTree(File dir, boolean recursive) {
		Set<File> fileTree = new HashSet<>();
		if (dir == null || !dir.exists()) {
			return fileTree;
		}
		File[] children = dir.listFiles();
		if (children == null) {
			fileTree.add(dir);
		} 
		else {
			for (File entry : children) {
				if (entry.isFile())
					fileTree.add(entry);
				else if(recursive)
					fileTree.addAll(listFileTree(entry, recursive));
			}
		}
		return fileTree;
	}
	
	/**
	 * 
	 * @param configLocation
	 * @throws IOException
	 */
	public static void execute(String configLocation) throws IOException {
		Configurator conf = new Configurator();
		Instructions s = conf.getInstructions(new File(configLocation));

		TransformerFactory factory = new TransformerFactory();
		for (Entry<String, HashMap<String, Instruction>> entry : s.entrySet()) {
			String fileLocation = entry.getKey();
			Transformer transformer = factory.buildTransformer(fileLocation);

			File outputFile = new File(fileLocation);

			transformer.load(outputFile);
			transformer.transform(entry.getValue());
			transformer.save(outputFile);
		}
	}
	
	public File getConfDirectory() {
		return new File(CONF_DIR);
	}
}
