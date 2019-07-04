package stash.transformer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TransformerFactoryTest {

	@Test(expected=IllegalArgumentException.class)
	public void buildTransformer_emptyString_IllegalArgument() {
		TransformerFactory tf = new TransformerFactory();
		tf.buildTransformer("");
	}
	
	@Test
	public void buildTransformer_xmlFile_xmlTransformer() {
		TransformerFactory tf = new TransformerFactory();
		Transformer t = tf.buildTransformer("web.xml");
		assertEquals(XmlTransformer.class, t.getClass());
	}
	
	@Test
	public void buildTransformer_propFile_propTransformer() {
		TransformerFactory tf = new TransformerFactory();
		Transformer t = tf.buildTransformer("settings.properties");
		assertEquals(PropertyTransformer.class, t.getClass());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void buildTransformer_jsonFile_exception() {
		TransformerFactory tf = new TransformerFactory();
		tf.buildTransformer("settings.json");
	}
}
