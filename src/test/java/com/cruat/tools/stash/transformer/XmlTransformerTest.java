package com.cruat.tools.stash.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import com.cruat.tools.stash.config.Instruction;
import com.cruat.tools.stash.config.InstructionBuilder;
import com.cruat.tools.stash.exceptions.NotImplementedException;
import com.cruat.tools.stash.exceptions.StashRuntimeException;
import com.cruat.tools.stash.transformer.XmlTransformer;
import com.cruat.tools.stash.utils.XmlUtils;

public class XmlTransformerTest {

	XmlTransformer xmlt;
	XPath xp = XPathFactory.newInstance().newXPath();

	@Before
	public void setup() throws IOException {
		xmlt = new XmlTransformer();
		String xml = buildTempXml();
		byte[] bArr = xml.getBytes();
		InputStream is = new ByteArrayInputStream(bArr);
		xmlt.load(is);
	}

	@Test
	public void load_validXml_documentNotNull() throws IOException {
		String xml = buildTempXml();
		byte[] bArr = xml.getBytes();
		InputStream is = new ByteArrayInputStream(bArr);
		xmlt.load(is);
		assertNotNull(xmlt.document);
	}

	@Test(expected = IllegalArgumentException.class)
	public void load_validXml_exception() throws IOException {
		String xml = "invalidxmlprefix" + buildTempXml();
		byte[] bArr = xml.getBytes();
		InputStream is = new ByteArrayInputStream(bArr);
		xmlt.load(is);
	}

	@Test
	public void transform_changeProp_propChanged() throws IOException, JAXBException {
		final String expected = "false";
		// input required for test
		Map<String, Instruction> input = new HashMap<>();
		String query = "web-app/session-config/cookie-config/secure";

		InstructionBuilder builder = new InstructionBuilder();
		builder.name(query).value("false");
		
		input.put(query, builder.build());
		
		
		// run the actual method under test
		xmlt.transform(input);
		// evaluate that success
		Optional<Node> e = XmlUtils.findFirst(query, xmlt.document);
		assertTrue(e.isPresent());
		assertEquals(expected, e.get().getTextContent());
	}
	
	@Test(expected=NotImplementedException.class)
	public void transformInternal_nonExistantNode_NotImplemented()
			throws IOException {
		Map<String, Instruction> input = new HashMap<>();
		String query = "non/existant/node";
		input.put(query, new Instruction("", null, null));
		xmlt.transformInternal(input);
	}
	
	@Test
	public void transform_changeAttr_propChanged() throws IOException{
		final String expected = "false";
		
		// input required for test
		Map<String, Instruction> input = new HashMap<>();
		String query = "web-app/session-config/cookie-config/@attr";
		
		InstructionBuilder builder = new InstructionBuilder();
		builder.name(query).value("false");
		
		
		input.put(query, builder.build());
		
		// run the actual method under test
		xmlt.transform(input);
		// evaluate that success
		Optional<Node> e = XmlUtils.findFirst(query, xmlt.document);
		assertTrue(e.isPresent());
		assertEquals(expected, e.get().getTextContent());
	}

	@Test
	public void transform_addProp_propAdded() throws IOException, JAXBException {
		final String expected = "<boop>beep</boop>";
		
		//Add directive
		List<String> directives = new ArrayList<>();
		directives.add("add");
		
		// input required for test
		Map<String, Instruction> input = new HashMap<>();
		String query = "web-app/session-config/cookie-config";
		InstructionBuilder builder = new InstructionBuilder();
		builder.name(query).directive("add").value(expected);
		input.put(query, builder.build());
		
		// run the actual method under test
		xmlt.transform(input);
		// evaluate that success
		query += "/boop";
		Optional<Node> e = XmlUtils.findFirst(query, xmlt.document);
		assertTrue(e.isPresent());
		assertEquals("beep", e.get().getTextContent());
	}
	
	@Test
	public void transform_removeProp_propRemoved() 
			throws IOException, JAXBException {
		//Add directive
		List<String> directives = new ArrayList<>();
		directives.add("remove");
		
		// input required for test
		Map<String, Instruction> input = new HashMap<>();
		String query = "web-app/session-config/cookie-config";
		
		InstructionBuilder builder = new InstructionBuilder();
		builder.name(query).directive("remove");
		
		input.put(query, builder.build());
		
		// run the actual method under test
		xmlt.transform(input);
		// evaluate that success
		Optional<Node> e = XmlUtils.findFirst(query, xmlt.document);
		assertFalse(e.isPresent());
	}
	
	@Test(expected=StashRuntimeException.class)
	public void transform_addRemove_exception() throws Exception {
		//Add directive
		List<String> directives = new ArrayList<>();
		directives.add("remove");
		directives.add("add");
		
		// input required for test
		Map<String, Instruction> input = new HashMap<>();
		String query = "web-app/session-config/cookie-config";
		InstructionBuilder builder = new InstructionBuilder();
		builder.name(query).directive(directives);
		System.out.println(builder);
		input.put(query, builder.build());
		
		// run the actual method under test
		xmlt.transform(input);
	}
	
	
	String buildTempXml() {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		builder.append("<web-app>\n");
		builder.append("  <session-config>\n");
		builder.append("  	<cookie-config attr=\"testValue\">\n");
		builder.append("  	  <secure>true</secure>\n");
		builder.append("  	</cookie-config>\n");
		builder.append("  </session-config>\n");
		builder.append("</web-app>");
		return builder.toString();
	}
}
