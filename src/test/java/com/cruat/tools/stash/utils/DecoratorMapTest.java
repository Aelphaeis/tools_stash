package com.cruat.tools.stash.utils;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DecoratorMapTest {

	@Test
	public void decoratorMap_entrySet_empty() {
		Map<String, String> m = new ConcreteMap<>();
		assertEquals(0, m.entrySet().size());
		assertTrue(m.isEmpty());
	}

	public static class ConcreteMap<K, E> extends DecoratorMap<K, E> {
		public ConcreteMap() {
			super(new HashMap<>());
		}
	}

}
