package com.cruat.tools.stash.utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {
	
	/**
	 * https://www.w3schools.com/xml/xpath_intro.asp
	 * 
	 * Given an X path and a document returns first node to match XPath.
	 * 
	 * @param x
	 * @param d
	 * @return
	 */
	public static Optional<Node> findFirst(String x, Document d) {
		List<Node> nodes = find(x, d, XPathConstants.NODESET);
		Node n = nodes.isEmpty() ? null : nodes.get(0);
		return Optional.ofNullable(n);
	}

	public static List<Node> find(String x, Document d) {
		return find(x, d, XPathConstants.NODESET);
	}

	public static List<Node> find(String x, Document d, QName q) {
		XPath xp = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList) xp.evaluate(x, d, q);
		} catch (XPathExpressionException e) {
			String err = "Illegal XPath Expression [%s]";
			err = String.format(err, x);
			throw new IllegalArgumentException(err, e);
		}

		List<Node> results = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			results.add(nodes.item(i));
		}
		return results;
	}
	

	public static boolean isNodeExisting(Node target, Node searchable) {
		String name = searchable.getNodeName();
		NodeList list = target.getChildNodes();
		NamedNodeMap attrs = searchable.getAttributes();

		// check names
		List<Node> possibleDuplicates = new ArrayList<>();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (name.equals(child.getNodeName())) {
				possibleDuplicates.add(child);
			}
		}
		Iterator<Node> it = possibleDuplicates.iterator();
		while (it.hasNext()) {
			Node child = it.next();
			if (!areAttributesEqual(attrs, child.getAttributes())
					|| !isTextValueEqual(searchable, child)) {
				it.remove();
			}
		}
		return !possibleDuplicates.isEmpty();
	}

	public static boolean areAttributesEqual(NamedNodeMap a, NamedNodeMap b) {
		if (a == b) {
			return true;
		}

		if (a == null || a.getLength() != b.getLength()) {
			return false;
		}

		for (int i = 0; i < b.getLength(); i++) {
			Node childAttr = b.item(i);
			Node attr = a.getNamedItem(childAttr.getNodeName());
			if (!attr.getNodeValue().equals(childAttr.getNodeValue())) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isTextValueEqual(Node nodeA, Node nodeB) {
		String a = nodeA.getTextContent();
		String b = nodeB.getTextContent();
		return a == b || (a == null || a.equals(b));
	}

    private XmlUtils () {
    	
    }
}
