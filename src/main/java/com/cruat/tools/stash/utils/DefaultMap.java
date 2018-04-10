package com.cruat.tools.stash.utils;

import java.util.HashMap;
import java.util.Map;

public class DefaultMap<K, E> extends DecoratorMap<K, E>{
	
	private E defaultValue;
	
	public DefaultMap(E defaultValue) {
		this(new HashMap<>(), defaultValue);
	}
	
	public DefaultMap(Map<K, E> m, E defaultValue) {
		super(m);
		this.defaultValue = defaultValue;
	}
	
	@Override
	public E get(Object key) {
		return getOrDefault(key, defaultValue);
	}
}
