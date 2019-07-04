package stash.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import stash.config.Instruction;
import stash.directives.Directive;
import stash.directives.xml.Add;
import stash.directives.xml.Remove;
import stash.exceptions.NotImplementedException;
import stash.exceptions.StashException;
import stash.utils.Serializer;
import stash.utils.XmlUtils;

public class XmlTransformer extends AbstractTransformer {

	public static final String ADD = "add";
	public static final String REMOVE = "remove";
	private static final Logger logger = LogManager.getLogger();
	private static final String UNK_ERR = "Unknown error occurred";
	Document document;

	public XmlTransformer() {
		this(TransformerFactory.CACHE.get(XmlTransformer.class));
	}
	
	XmlTransformer(Set<Directive> directives) {
		super(directives);
	}

	@Override
	public void load(InputStream stream) throws IOException {
		try {
			DocumentBuilderFactory docFactory = documentFactory();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			document = docBuilder.parse(stream);
		} catch (ParserConfigurationException e) {
			// this is impossible and not recoverable
			logger.fatal(UNK_ERR, e);
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			logger.error("malformed xml loaded", e);
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void transformInternal(Map<String, Instruction> kvp) {
		for (Entry<String, Instruction> entry : kvp.entrySet()) {
			String key = entry.getKey();
			Instruction inst = entry.getValue();
			String value = inst.getValue();

			Optional<Node> t = XmlUtils.findFirst(key, document);
			if (t.isPresent()) {
				Node e = t.get();
				if (handleAddIfPresent(inst)) {
					logger.trace("added node to [{}]", key);
				} else if (handleRemoveIfPresent(inst)) {
					logger.trace("removed node from [{}]", key);
				} else {
					e.setTextContent(value);
					logger.trace(e);
				}
			} else {
				// this is valid but not yet implemented
				// I'd imagine we want to create the node here.
				String err = "Unable to find node with query [{}]";
				logger.error(err, key);
				throw new NotImplementedException();
			}
		}
	}

	@Override
	public void save(OutputStream stream) throws IOException {
		Serializer.serialize(document, new PrintWriter(stream));
	}
	
	
	public Document getDocument() {
		return document;
	}

	boolean handleAddIfPresent(Instruction i) {
		if (i.getDirectives().contains(ADD)) {
			new Add().execute(i, this);
			return true;

		} else {
			return false;
		}
	}


	boolean handleRemoveIfPresent(Instruction i) {
		if (i.getDirectives().contains(REMOVE)) {
			new Remove().execute(i, this);
			return true;

		} else {
			return false;
		}
	}

	private static DocumentBuilderFactory documentFactory() {
		DocumentBuilderFactory dbf;
		try {
			dbf = DocumentBuilderFactory.newInstance();

			dbf.setValidating(false);
			dbf.setNamespaceAware(true);
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
			dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			
			return dbf;
		} catch (ParserConfigurationException e) {
			// this is impossible and not recoverable
			logger.error(UNK_ERR, e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void validateDirectivesInternal(Instruction i) throws StashException {
		List<String> dirs = i.getDirectives();
		if (dirs.contains(REMOVE) && dirs.contains(ADD)) {
			String err = "remove and add directives together is not supported";
			throw new StashException(err);
		}
	}

	@Override
	public Set<Directive> getDirectives() {
		return directives;
	}
}
