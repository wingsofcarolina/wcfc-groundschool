package org.wingsofcarolina.gs.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
	private String path;
	private String label;
	private List<Node> children = null;
	
	public Node() {}
	
	public Node(String path, String label) {
		this.path = path;
		this.label = label;
	}

	public abstract boolean isDirectory();

	public String getPath() {
		return path;
	}

	public String getLabel() {
		return label;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void addChild(Node index) {
		if (children == null) {
			this.children = new ArrayList<Node>();
		}
		this.children.add(index);
	}

}
