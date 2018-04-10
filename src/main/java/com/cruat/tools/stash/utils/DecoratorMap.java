package com.cruat.tools.stash.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class DecoratorMap<K, E> implements Map<K, E> {

	private Map<K, E> internal;
	
	DecoratorMap(Map<K, E> wrappable) {
		this.internal = wrappable;
	}
	
	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return internal.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return internal.containsKey(value);
	}

	@Override
	public E get(Object key) {
		return internal.get(key);
	}

	@Override
	public E put(K key, E value) {
		return internal.put(key, value);
	}

	@Override
	public E remove(Object key) {
		return internal.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends E> m) {
		internal.putAll(m);
	}

	@Override
	public void clear() {
		internal.clear();
	}

	@Override
	public Set<K> keySet() {
		return internal.keySet();
	}

	@Override
	public Collection<E> values() {
		return internal.values();
	}

	@Override
	public Set<Entry<K, E>> entrySet() {
		return entrySet();
	}
}
