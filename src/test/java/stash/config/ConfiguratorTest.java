package stash.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import test.stash.util.ResourceFolder;

public class ConfiguratorTest {

	private static final String LOCATION = "location";
	private static final String TEST_FILE_LOC = "src/test/resources";
	private static final String VALID_FILE = "/valid.properties";
	private static final String TEST_FOLDER = "/TestFolder";
	
	@Rule
	public ResourceFolder fold = new ResourceFolder();
	
	@Test
	public void getInstructions_testDir_getEverything() throws IOException{
		Configurator conf = new Configurator();
		Instructions inst = conf.getInstructions(fold.getResourceFolder());
		assertNotNull(inst.get(LOCATION));
		assertEquals("value1", inst.get(LOCATION).get("default_property_1").getValue());
		assertEquals("value2", inst.get(LOCATION).get("default_property_2").getValue());
	}
	
	@Test
	public void getInstructions_nonExistant_getEverything() throws IOException{
		Configurator conf = new Configurator();
		Instructions inst = conf.getInstructions(TEST_FILE_LOC + "/missing" + TEST_FOLDER);
		assertTrue(inst.isEmpty());
	}
	

	@Test
	public void listFileTree_multiLvDir_success() {
		File file = fold.getResourceFolder();
		assertEquals(2, Configurator.listFileTree(file).size());
	}

	@Test
	public void listFileTree_null_empty() {
		assertEquals(0, Configurator.listFileTree(null).size());
	}

	@Test
	public void listFileTree_invalidFile_empty() {
		File file = new File(TEST_FILE_LOC + "invalidFolder * ");
		assertEquals(0, Configurator.listFileTree(file).size());
	}
	
	@Test
	public void listFileTree_file_empty() {
		File file = new File(TEST_FILE_LOC + VALID_FILE);
		assertEquals(1, Configurator.listFileTree(file).size());
	}
	
	@Test
	public void getConfDirectory_noArgs_success() {
		Configurator conf = new Configurator();
		File file = conf.getConfDirectory();
		assertNotNull(file);
		assertTrue(file.isDirectory());
	}
}
