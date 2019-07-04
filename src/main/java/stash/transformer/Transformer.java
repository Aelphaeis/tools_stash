package stash.transformer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import stash.config.Instruction;
import stash.directives.Directive;

/**
 * This object is intended to template the process of modifying a file.
 * 
 * @author morain
 */
public interface Transformer {
	/**
	 * Loads original content of the file.
	 * 
	 * {@link #load(File)} should not be called twice. If Load is called twice
	 * there is no definition for how this should work.
	 * 
	 * @param file
	 * @throws IOException
	 */
	void load(File file) throws IOException;

	/**
	 * @see {@link #load(File)}
	 * @param stream
	 * @throws IOException
	 */
	void load(InputStream stream) throws IOException;

	/**
	 * Changes the content of the original file<br/>
	 * {@link #load(File)} should be called before this method.
	 * 
	 * @param kvp
	 */
	void transform(Map<String, Instruction> kvp);
	/**
	 * Saves the content.
	 * 
	 * @param file
	 * @throws IOException
	 */
	void save(File file) throws IOException;

	/**
	 * @see {@link #save(File)}
	 * @param stream
	 * @throws IOException
	 */
	void save(OutputStream stream) throws IOException;
	
	
	/**
	 * Get supported directives
	 * @return
	 */
	Set<Directive> getDirectives();
}
