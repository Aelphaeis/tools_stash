package stash.transformer;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
	private static final Logger logger = LogManager.getLogger();
	private static final Map<Class<? extends Transformer>, Set<Directive>> CACHE = buildCache();


	
	public <T extends Transformer> Set<Directive> getDirectives(Class<T> c){
		return CACHE.get(c);
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
			return new DefaultTransformer();
		}
	}
	
	
	/**
	 * Sorts directives by the transformers they support, this allows us to
	 * more efficiently resolve directives when creating transformers as
	 * we never expect this to change after compile time.
	 */

	private static DefaultMap<Class<? extends Transformer>, Set<Directive>> buildCache() {
		logger.traceEntry();
		Package directivePkg = Directive.class.getPackage();
		//Get classes that implement Directive with parameterless ctor
		Map<Class<? extends Transformer>, Set<Directive>> cache;
		List<Class<?>> pClasses = Reflector.getClassesForPackage(directivePkg);
		cache = Collections.unmodifiableMap(pClasses.stream()
			.filter(Directive.class::isAssignableFrom)
			.filter(Reflector::isInstantiable)
			.map(Reflector::initParamCtor)
			.filter(Objects::nonNull)
			.map(Directive.class::cast)
			.flatMap(d -> d.supports()
				.stream()
				.map(q -> new SimpleEntry<>(d, q)))
			.collect(Collectors
				.groupingBy(AbstractMap.SimpleEntry::getValue, Collectors
					.mapping(AbstractMap.SimpleEntry::getKey, Collectors
							.toSet()))));
		cache = Collections.unmodifiableMap(cache);
		return logger.traceExit(new DefaultMap<>(cache, new HashSet<>()));
	}
}
