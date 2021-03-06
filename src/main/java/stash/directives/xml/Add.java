package stash.directives.xml;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import stash.config.Instruction;
import stash.directives.Supports;
import stash.directives.TransformDirective;
import stash.exceptions.NotImplementedException;
import stash.exceptions.StashRuntimeException;
import stash.transformer.Transformer;
import stash.transformer.XmlTransformer;
import stash.utils.Serializer;
import stash.utils.XmlUtils;

@Supports({ XmlTransformer.class })
public class Add implements TransformDirective {

	private static final Logger logger = LogManager.getLogger();

	@Override
	public void execute(Instruction i, Transformer t) {
		XmlTransformer transformer = resolveTransformer(t);
		String xpath = i.getName();
		Document document = transformer.getDocument();
		Optional<Node> node = XmlUtils.findFirst(xpath, document);
		
		if(!node.isPresent()) {
			String err = "Unable to find node with query [{}]";
			logger.error(err, xpath);
			throw new NotImplementedException();
		}
		
		Node e = node.get();
		String value = i.getValue();
		value = String.format("<wrapper>%s</wrapper>", value);
		byte[] valueBytes = value.getBytes();
		ByteArrayInputStream is = new ByteArrayInputStream(valueBytes);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document tempDoc = builder.parse(is);
			NodeList list = tempDoc.getDocumentElement().getChildNodes();
			
			for (int c = 0; c < list.getLength(); c++) {
				Node n = list.item(c);
				if (!XmlUtils.isNodeExisting(e, n)) {
					Node imported = document.importNode(n, true);
					e.appendChild(imported);
				}
			}

		} catch (Exception ex) {
			String err = "error adding {} to {}";
			logger.error(err, i.getValue(), Serializer.serialize(e));
			throw new StashRuntimeException(ex);
		}
	}
	
	private static XmlTransformer resolveTransformer(Transformer t) {
		if(!(t instanceof XmlTransformer)){
			String err = "This transformation only works for %s";
			err = String.format(err, XmlTransformer.class.getName());
			throw new IllegalArgumentException(err);
		}
		else {
			return XmlTransformer.class.cast(t);
		}
	}
}
