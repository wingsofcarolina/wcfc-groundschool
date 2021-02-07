package org.wingsofcarolina.gs.resources;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Index {
	private String path;
	private String label;
	private List<Index> children = null;
	
	public Index(String path, String label) {
		this.path = path;
		this.label = label;
	}

	public String getPath() {
		return path;
	}

	public String getLabel() {
		return label;
	}

	public List<Index> getChildren() {
		return children;
	}
	
	public void addChild(Index index) {
		if (children == null) {
			this.children = new ArrayList<Index>();
		}
		this.children.add(index);
	}

	@Override
	public String toString() {
		return "Index [path=" + path + ", label=" + label + ", children=" + children + "]";
	}
}
