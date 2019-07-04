package stash.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import stash.config.Instruction;
import stash.config.InstructionBuilder;
import stash.transformer.PropertyTransformer;

public class PropertyTransformerTest {

	private static final String TEST_FILE = "/TestFolder/test.properties";
	private static final String TEST_FILE_LOC = "src/test/resources";

	PropertyTransformer t;
	@Before
	public void setup() {
		t =	 new PropertyTransformer();
	}
	
	@Test
	public void transform_unwrap_unwrappedCorrectly() throws IOException {
		String input = "value,value2,value3";
		String[] valArr = input.split(",");
		String value = Arrays.toString(valArr);
		
		Map<String, Instruction> m = new HashMap<>();
		PropertyTransformer t = new PropertyTransformer();
		
		InstructionBuilder builder = new InstructionBuilder();
		builder.name("name").value(value).directive("unwrap");
		m.put("name", builder.build());
		
		t.transform(m);
		String result = String.valueOf(t.config.getProperty("name"));
		assertEquals("value, value2, value3", result);
	}
	
	@Test
	public void load_validFile_success() throws IOException {
		t.load(new File(TEST_FILE_LOC + TEST_FILE));
		assertEquals("value1", t.config.getProperty("default_property_1"));
		assertEquals("value2", t.config.getProperty("default_property_2"));
	}
	
	@Test
	public void transform_newValues_success() throws IOException {
		t.load(new File(TEST_FILE_LOC + TEST_FILE));
		
		Map<String, Instruction> addable = new HashMap<>();
		
		InstructionBuilder builder = new InstructionBuilder();
		builder.value("value3").name("new_property_1");
		addable.put("new_property_1", builder.build());
		
		t.transform(addable);
		
		assertEquals("value1", t.config.getProperty("default_property_1"));
		assertEquals("value2", t.config.getProperty("default_property_2"));
		assertEquals("value3", t.config.getProperty("new_property_1"));
	}
	
	@Test
	public void transform_replace_success() throws IOException {
		t.load(new File(TEST_FILE_LOC + TEST_FILE));
		
		Map<String, Instruction> addable = new HashMap<>();
		InstructionBuilder builder = new InstructionBuilder();
		builder.name("default_property_1").value("value3");
		addable.put("default_property_1", builder.build());
		
		t.transform(addable);
		
		assertEquals("value3", t.config.getProperty("default_property_1"));
		assertEquals("value2", t.config.getProperty("default_property_2"));
	}
	
	@Test
	public void transform_save_success() throws IOException {
		final String expected = "key=value";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		InputStream is = new ByteArrayInputStream(expected.getBytes());
		
		t.load(is);
		assertEquals("value", t.config.getProperty("key"));
		t.save(os);
		
		String result = new String(os.toByteArray());
		assertTrue(result.contains(expected));
	}
}
