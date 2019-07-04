package stash.transformer;

import static stash.utils.Reflector.getClassesForPackage;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stash.directives.Directive;
import stash.utils.DefaultMap;
import stash.utils.Reflector;

public class TransformerFactory {
	public static final Map<Class<? extends Transformer>, Set<Directive>> CACHE;
	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * Sorts directives by the transformers they support, this allows us to
	 * more efficiently resolve directives when creating transformers as
	 * we never expect this to change after compile time.
	 */
	static {
		logger.traceEntry();
		Package directivePkg = Directive.class.getPackage();
		//Get classes that implement Directive with parameterless ctor
		Map<Class<? extends Transformer>, Set<Directive>> cache;
		cache = Collections.unmodifiableMap(getClassesForPackage(directivePkg)
			.stream()
			.filter(Directive.class::isAssignableFrom)
			.filter(Reflector::isInstantiable)
			.map(Reflector::initParamCtor)
			.filter(Objects::nonNull)
			.map(Directive.class::cast)
			.flatMap(d -> d.supports()
				.stream()
				.map(q -> new SimpleEntry<>(d, q)))
			.collect(Collectors
				.groupingBy(e -> e.getValue(), Collectors
					.mapping(e -> e.getKey(), Collectors.toSet()))));
		cache = new DefaultMap<>(cache, new HashSet<>());
		CACHE = Collections.unmodifiableMap(cache);
		
		if (logger.isTraceEnabled()) {
			Set<Directive> directives = CACHE.values().stream()
					.flatMap(p -> p.stream())
					.collect(Collectors.toSet());
			
			for (Class<? extends Transformer> t : CACHE.keySet()) {
				logger.trace("Transformer [{}] directives cached", t.getName());
			}
			for(Directive d : directives) {
				logger.trace("Directive [{}] cached", d.getClass().getName());
			}
		}
		logger.traceExit();
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
