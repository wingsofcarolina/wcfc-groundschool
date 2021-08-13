package org.wingsofcarolina.gs.model;

public class DocumentNode extends Node {
	private Integer lesson = 0;
	
	public DocumentNode() {}
	
	public DocumentNode(String path, String label) {
		super(path, label);
	}

	public DocumentNode(String path, String label, Integer lesson) {
		super(path, label);
		this.lesson = lesson;
	}
	
	@Override
	public boolean isDirectory() {
		return false;
	}

	public Integer getLesson() {
		return lesson;
	}
}
