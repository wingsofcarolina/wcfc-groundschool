package org.wingsofcarolina.gs.resources;

import java.util.Date;

public class CacheEntry {
	private String name;
	private byte[] bytes;
	private Date timestamp = new Date();
	
	public CacheEntry(String name, byte[] bytes) {
		this.name = name;
		this.bytes = bytes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "CacheEntry [name=" + name + ", bytes=" + bytes.length + ", timestamp=" + timestamp + "]";
	}
}
