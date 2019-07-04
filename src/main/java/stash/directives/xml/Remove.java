package stash.directives.xml;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import stash.config.Instruction;
import stash.directives.Supports;
import stash.directives.TransformDirective;
import stash.exceptions.NotImplementedException;
import stash.transformer.Transformer;
import stash.transformer.XmlTransformer;
import stash.utils.XmlUtils;

@Supports({ XmlTransformer.class })
public class Remove implements TransformDirective{
	
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
		e.getParentNode().removeChild(e);
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
