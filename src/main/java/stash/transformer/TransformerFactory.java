package com.cruat.tools.stash.transformer;

import java.util.AbstractMap.SimpleEntry;

import static com.cruat.tools.stash.utils.Reflector.getClassesForPackage;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cruat.tools.stash.directives.Directive;
import com.cruat.tools.stash.utils.DefaultMap;
import com.cruat.tools.stash.utils.Reflector;

public class TransformerFactory {
	public static final Map<Class<? extends Transformer>, Set<Directive>> CACHE;
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * Sorts directives by the transformers they support, this allows us to
	 * more efficiently resolve directives when creating transformers as
	 * we never expect this to change after compile time.
	 */
	static {
		Package directivePkg = Directive.class.getPackage();
		//Get classes that implement Directive with parameterless ctor
		Map<Class<? extends Transformer>, Set<Directive>> cache;
		cache = Collections.unmodifiableMap(getClassesForPackage(directivePkg)
			.stream()
			.filter(Directive.class::isAssignableFrom)
			.peek(p-> logger.info("Found directive {}", p.getName()))
			.filter(Reflector::isInstantiable)
			.map(Reflector::initParamCtor)
			.filter(Objects::nonNull)
			.peek(p-> logger.info("Instansiated {}", p))
			.map(Directive.class::cast)
			.flatMap(d -> d.supports()
				.stream()
				.map(q -> new SimpleEntry<>(d, q)))
			.collect(Collectors
				.groupingBy(e -> e.getValue(), Collectors
					.mapping(e -> e.getKey(), Collectors.toSet()))));
		cache = new DefaultMap<>(cache, new HashSet<>());
		CACHE = Collections.unmodifiableMap(cache);
	}
	
	public Transformer buildTransformer(String filePath) {
		int index = filePath.lastIndexOf('.');
		if (index == -1) {
			String err = "Missing file type.";
			throw new IllegalArgumentException(err);
		}
		String type = filePath.substring(index);
		
		if (type.equalsIgnoreCase(".xml")) {
			return new XmlTransformer();
		} else if (type.equalsIgnoreCase(".properties")) {
			return new PropertyTransformer();
		} else {
			String err = "Unknown file type.";
			throw new IllegalArgumentException(err);
		}
	}
}
