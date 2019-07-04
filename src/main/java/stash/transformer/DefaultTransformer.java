package stash.transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import stash.config.Instruction;
import stash.exceptions.StashException;

public class DefaultTransformer extends AbstractTransformer {

	@Override
	public void load(InputStream stream) throws IOException {
		try(BufferedReader reader = newBufferedReader(stream)){
			
		}
	}
	
	private BufferedReader newBufferedReader(InputStream is) {
		Reader temp = new InputStreamReader(is, StandardCharsets.UTF_8);
		return new BufferedReader(temp);
	}

	@Override
	public void save(OutputStream stream) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transformInternal(Map<String, Instruction> kvp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateDirectivesInternal(Instruction i)
			throws StashException {
		// TODO Auto-generated method stub
	}
}
