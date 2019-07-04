package stash.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

import stash.config.Instruction;
import stash.config.InstructionFileParser;
import stash.config.Instructions;
import stash.exceptions.StashRuntimeException;
import test.com.cruat.tools.stash.util.OPStreamBuilder;

public class InstructionFileParserTest {
	
	@Test
	public void process_directive_splitArray() {
		OPStreamBuilder builder = new OPStreamBuilder();
		builder.entry("location<unwrap>name", "value,value2,value3");
		PropertiesConfiguration pc = builder.toOrderedProperties();
	
		Instructions insts = InstructionFileParser.process(pc);
		Instruction inst = insts.get("location").get("name");
		String result = String.valueOf(inst.getValue());
		assertEquals("[value, value2, value3]", result);
		assertTrue(inst.getDirectives().contains("unwrap"));
	}

	@Test(expected = StashRuntimeException.class)
	public void process_noLocationForProperty_exception() {
		OPStreamBuilder builder = new OPStreamBuilder().entry("PropertyName",
				"value");

		InstructionFileParser.process(builder.toOrderedProperties());
	}

	@Test(expected = StashRuntimeException.class)
	public void process_missingPropName_exception() {
		OPStreamBuilder builder = new OPStreamBuilder().entry("Location>",
				"value");

		InstructionFileParser.process(builder.toOrderedProperties());
	}

	@Test
	public void process_setDefaultLocation_success()
			throws ConfigurationException {
		OPStreamBuilder builder = new OPStreamBuilder()
				.defaultLocation("Location");

		PropertiesConfiguration props = builder.toOrderedProperties();
		Instructions results = InstructionFileParser.process(props);
		assertTrue(results.isEmpty());

		builder.entry("location", "name", "value");
		props = builder.toOrderedProperties();
		results = InstructionFileParser.process(props);
		assertTrue(results.containsKey("location"));
		assertEquals("value", results.get("location").get("name").getValue());
	}

	@Test
	public void process_explicit_success() {
		OPStreamBuilder builder = new OPStreamBuilder()
				.defaultLocation("Location").entry("boop", "name", "value");

		PropertiesConfiguration props = builder.toOrderedProperties();
		Instructions results = InstructionFileParser.process(props);

		assertTrue(results.containsKey("boop"));
		assertFalse(results.containsKey("location"));
		assertEquals("value", results.get("boop").get("name").getValue());
	}

}
