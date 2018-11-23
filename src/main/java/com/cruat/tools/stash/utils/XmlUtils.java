package com.cruat.tools.stash.utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
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

    private XmlUtils () {
    	
    }
}
