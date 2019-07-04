package stash.transformer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class DefaultTransformerTest {
	private static final String RES_DIR = "src/test/resources/";
	private static final String TEST_FILE = "valid.log";
	

	private DefaultTransformer t;
	
	@Before
	public void setup() {
		t = new DefaultTransformer();
	}
	
	@Test
	public void load_ValidFile_contentLoaded() throws IOException {
		t.load(new File(RES_DIR + TEST_FILE));
		assertEquals("DefaultTransformer Test File", t.getContent());
	}
}
