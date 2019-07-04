package stash.transformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import stash.config.Instruction;
import stash.config.InstructionBuilder;
import stash.exceptions.AggregationException;
import stash.exceptions.StashException;

public class AbstractTransformerTest {

	private static final String TEST_FILE_LOC = "src/test/resources/TestFolder";
	
	AbstractTransformer at = new AbstractTransformer() {
		
		@Override
		public void save(OutputStream stream) throws IOException {
			
		}
		
		@Override
		public void load(InputStream stream) throws IOException {
		}

		@Override
		public void transformInternal(Map<String, Instruction> kvp) {
			
		}

		@Override
		public void validateDirectivesInternal(Instruction i) {
		}
	};
	
	@Test(expected=StashException.class)
	public void validateDirectives_unsupportedDirective_exception() 
			throws StashException {
		InstructionBuilder builder = new InstructionBuilder();
		builder.value("value").directive("unsupported");
		Instruction i = builder.build();
		at.validateDirectives(i);
	}
	
	@Test(expected=AggregationException.class)
	public void validateDirectives_multiUnsupportedDirective_exception() 
			throws StashException {
		List<String> dirs = Arrays.asList("unsupported1", "unsupported2");
		InstructionBuilder builder = new InstructionBuilder();
		builder.value("value").directive(dirs);
		Instruction i = builder.build();
		at.validateDirectives(i);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void load_missingFile_exception() throws IOException {
		at.load(new File(TEST_FILE_LOC + "/idontexist.exe"));
	}
	
	@Test
	public void load_file_noException() throws IOException {
		at.load(new File(TEST_FILE_LOC + "/test.properties"));
	}
}
