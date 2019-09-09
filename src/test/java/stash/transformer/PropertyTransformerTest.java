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
import org.junit.Rule;
import org.junit.Test;

import stash.config.Instruction;
import stash.config.InstructionBuilder;
import test.stash.util.ResourceFolder;

public class PropertyTransformerTest {

	private static final String VALUE3 = "value3";
	private static final String VALUE2 = "value2";
	private static final String NEW_PROPERTY_1 = "new_property_1";
	private static final String DEFAULT_PROPERTY_2 = "default_property_2";
	private static final String DEFAULT_PROPERTY_1 = "default_property_1";
	private static final String TEST_FILE = "test.properties";

	private PropertyTransformer t;
	
	@Rule
	public ResourceFolder folder = new ResourceFolder();
	
	@Before
	public void setup() {
		t =	 new PropertyTransformer();
	}
	
	@Test
	public void transform_unwrap_unwrappedCorrectly()  {
		String input = "value,value2,value3";
		String[] valArr = input.split(",");
		String value = Arrays.toString(valArr);
		
		Map<String, Instruction> m = new HashMap<>();
		
		InstructionBuilder builder = new InstructionBuilder();
		builder.name("name").value(value).directive("unwrap");
		m.put("name", builder.build());
		
		t.transform(m);
		String result = String.valueOf(t.config.getProperty("name"));
		assertEquals("value, value2, value3", result);
	}
	
	@Test
	public void load_validFile_success() throws IOException {
		t.load(new File(folder.getResourceFolder(), TEST_FILE));
		assertEquals("value1", t.config.getProperty(DEFAULT_PROPERTY_1));
		assertEquals(VALUE2, t.config.getProperty(DEFAULT_PROPERTY_2));
	}
	
	@Test
	public void transform_newValues_success() throws IOException {
		t.load(new File(folder.getResourceFolder(), TEST_FILE));
		Map<String, Instruction> addable = new HashMap<>();
		
		InstructionBuilder builder = new InstructionBuilder();
		builder.value(VALUE3).name(NEW_PROPERTY_1);
		addable.put(NEW_PROPERTY_1, builder.build());
		
		t.transform(addable);
		
		assertEquals("value1", t.config.getProperty(DEFAULT_PROPERTY_1));
		assertEquals(VALUE2, t.config.getProperty(DEFAULT_PROPERTY_2));
		assertEquals(VALUE3, t.config.getProperty(NEW_PROPERTY_1));
	}
	
	@Test
	public void transform_replace_success() throws IOException {
		t.load(new File(folder.getResourceFolder(), TEST_FILE));
		
		Map<String, Instruction> addable = new HashMap<>();
		InstructionBuilder builder = new InstructionBuilder();
		builder.name(DEFAULT_PROPERTY_1).value(VALUE3);
		addable.put(DEFAULT_PROPERTY_1, builder.build());
		
		t.transform(addable);
		
		assertEquals(VALUE3, t.config.getProperty(DEFAULT_PROPERTY_1));
		assertEquals(VALUE2, t.config.getProperty(DEFAULT_PROPERTY_2));
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
