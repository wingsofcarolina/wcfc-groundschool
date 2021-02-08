package org.wingsofcarolina.gs.resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(Cache.class);
	
	private static Map<String, CacheEntry>cache = new HashMap<String, CacheEntry>();

	private static Cache instance = null;
	
	private Cache() {}
	
	public static Cache instance() {
		if (instance == null) {
			Cache.instance  = new Cache();
		}
		return instance;
	}

	public boolean hasEntry(String name) {
		return cache.get(name) != null;
	}
	
	public void put(String name, byte[] bytes) {
		cache.put(name, new CacheEntry(name, bytes));
	}
	
	public byte[] get(String name) throws IOException {
		CacheEntry result = cache.get(name);
		if (result == null) {
			throw new IOException("Cached file not found.");
		}
		return result.getBytes();
	}

	public void clear() {
		cache.clear();
	}
}
