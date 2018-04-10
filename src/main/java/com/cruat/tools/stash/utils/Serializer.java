package com.cruat.tools.stash.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class Serializer {
	private static final Logger logger = LogManager.getLogger();

	private Serializer() {
		// we don't want anyone instantiating this class.
	}

	/**
	 * Given an object returns an xml representation of the object
	 * 
	 * @param obj
	 *            object to serialize to xml
	 * @return
	 * @throws JAXBException
	 *             if the object cannot be serialized
	 */
	public static <T> String serialize(T obj) throws JAXBException {
		StringWriter stringWriter = new StringWriter();
		serialize(obj, stringWriter);
		return stringWriter.toString();
	}

	/**
	 * Given an document returns a string representation of the object
	 * 
	 * @param obj
	 *            object to serialize to xml
	 * @return
	 * @throws JAXBException
	 *             if the object cannot be serialized
	 */
	public static String serialize(Node obj) {
		StringWriter stringWriter = new StringWriter();
		serialize(obj, stringWriter);
		return stringWriter.toString();
	}

	/**
	 * Given an object and a writer will write the xml object to the writer.
	 * 
	 * @param obj
	 * @param writer
	 * @throws JAXBException
	 *             if the object cannot be serialized.
	 */
	public static <T> void serialize(T obj, Writer writer)
			throws JAXBException {
		/*
		 * Casting Class<? extends Object> to Class<? extends T> is impossible
		 * to guarantee, hence we receive an unchecked warning. We suppress this
		 * because we are in control of both the source and result of the
		 * assignment operation.
		 */
		@SuppressWarnings("unchecked")
		Class<T> objClass = (Class<T>) obj.getClass();
		JAXBContext context = JAXBContext.newInstance(objClass);
		QName qName = new QName(obj.getClass().getSimpleName());
		JAXBElement<T> element = new JAXBElement<>(qName, objClass, obj);

		Marshaller m = context.createMarshaller();

		// This makes things readable.
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(element, writer);
	}

	/**
	 * Given a document and a writer, will write the contents of the document to
	 * the writter.
	 * 
	 * @param document
	 * @param writer
	 */
	public static void serialize(Node document, Writer writer) {
		final String indent = "{http://xml.apache.org/xslt}indent-amount";
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(writer);
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(indent, "2");
			t.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// this is impossible and not recoverable
			logger.error("Unable to configure tranformer", e);
			throw new IllegalStateException(e);
		} catch (TransformerException e) {
			String err = "Unrecoverable error occurred during transformation";
			logger.error(err, e);
			throw new IllegalArgumentException(err);
		}
	}

	/**
	 * Deserializes a string to specified object.
	 * 
	 * @param xml
	 *            string to deserialize
	 * @param clazzs
	 *            class we will deserialize to
	 * @return
	 * @throws JAXBException
	 *             if Object cannot be deserialized
	 */
	public static <T> T deserialize(String xml, Class<T> clazz)
			throws JAXBException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
	        dbf.setValidating(false);
	        dbf.setNamespaceAware(true);
	        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
	        dbf.setFeature("http://xml.org/sax/features/validation", false);
	        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
	        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false );
			
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			InputSource xmlSource = new InputSource(new StringReader(xml));
			Document doc = dBuilder.parse(xmlSource);

			JAXBContext context = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			JAXBElement<T> element = unmarshaller.unmarshal(doc, clazz);
			return element.getValue();
		} catch (ParserConfigurationException e) {
			// This should not occur ever
			logger.error(e.getMessage(), e);
			throw new IllegalStateException("Unable to configure parser", e);
		} catch (IOException e) {
			// This should not occur ever
			logger.error(e.getMessage(), e);
			throw new IllegalStateException("Unable to read document", e);
		} catch (SAXException e) {
			// The Xml input is not formatted properly.
			throw new IllegalArgumentException("Illegal Xml input", e);
		}
	}

	/**
	 * Given a input stream deserializes the xml into a document
	 * 
	 * @param is
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document deserialize(InputStream is)
			throws SAXException, IOException {
		DocumentBuilderFactory docFactory;
		try {
			docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			return docBuilder.parse(is);
		} catch (ParserConfigurationException e) {
			// this is impossible and not recoverable
			logger.error("Unknown error occurred", e);
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Given a string of xml, deserializes the xml into a document
	 * 
	 * @param xml
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document deserialize(String xml)
			throws SAXException, IOException {
		return deserialize(new ByteArrayInputStream(xml.getBytes()));
	}
}
