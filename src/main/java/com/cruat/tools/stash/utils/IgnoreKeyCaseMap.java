package com.cruat.tools.stash.utils;

import java.util.Map;

public class IgnoreKeyCaseMap<E> extends DecoratorMap<String, E> {

	public IgnoreKeyCaseMap(Map<String, E> wrappable) {
		super(wrappable);
	}
	
	@Override
	public boolean containsKey(Object key) {
		return internal.containsKey(String.class.cast(key).toLowerCase());
	}
}
