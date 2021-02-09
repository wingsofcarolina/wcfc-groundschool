package org.wingsofcarolina.gs.model;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
	private List<Node> children = new ArrayList<Node>();
	
	public void addChild(Node node) {
		this.children.add(node);
	}
	
	public List<Node> getChildren() {
		return children;
	}
}
