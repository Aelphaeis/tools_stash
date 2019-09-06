package stash.transformer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stash.config.Instruction;
import stash.exceptions.StashException;

public class DefaultTransformer extends AbstractTransformer {
	
	private static final Logger logger = LogManager.getLogger();
	private static final String NL = System.lineSeparator();
	
	private String content;
	
	public DefaultTransformer() {
		super(new TransformerFactory().getDirectives(DefaultTransformer.class));
	}

	@Override
	public void load(InputStream stream) throws IOException {
		List<String> lines = new ArrayList<>();
		try(BufferedReader r = newBufferedReader(stream)){
			for(String l = r.readLine(); l != null; l = r.readLine()) {
				lines.add(l);
			}
			content = lines.stream().collect(Collectors.joining(NL));
		}
	}

	@Override
	public void save(OutputStream stream) throws IOException {
		try(BufferedWriter w = newBufferedWriter(stream)){
			w.write(content);
		}
	}

	@Override
	public void transformInternal(Map<String, Instruction> kvp) {
		logger.traceEntry(null, kvp);
		throw new UnsupportedOperationException();
	}

	@Override
	public void validateDirectivesInternal(Instruction i)
			throws StashException {
		logger.traceEntry(null, i);
		throw new UnsupportedOperationException();
	}
	
	private BufferedReader newBufferedReader(InputStream is) {
		Reader temp = new InputStreamReader(is, StandardCharsets.UTF_8);
		return new BufferedReader(temp);
	}
	
	private BufferedWriter newBufferedWriter(OutputStream os) {
		Writer temp = new OutputStreamWriter(os);
		return new BufferedWriter(temp);
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
}
