package com.cruat.tools.stash.directives;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.Collection;

import com.cruat.tools.stash.transformer.Transformer;

public interface Directive {
	default String getName() {
		return this.getClass().getSimpleName();
	}
	
	default Collection<Class<? extends Transformer>> supports(){
		Supports supports = getClass().getAnnotation(Supports.class);
		if(supports == null) {
			return unmodifiableList(emptyList());
		}
		else {
			return unmodifiableList(asList(supports.value()));
		}
	}
}
