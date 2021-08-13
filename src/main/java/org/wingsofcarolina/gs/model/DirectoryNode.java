package org.wingsofcarolina.gs.model;

public class DirectoryNode extends Node {

	public DirectoryNode() {}
	
	public DirectoryNode(String path, String label) {
		super(path, label);
	}

	@Override
	public boolean isDirectory() {
		return true;
	}
}
