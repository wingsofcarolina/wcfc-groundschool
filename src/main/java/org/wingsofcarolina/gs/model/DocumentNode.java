package org.wingsofcarolina.gs.model;

public class DocumentNode extends Node {
	private Integer lesson = 0;
	private Integer level = 0;
	
	public DocumentNode(String path, String label) {
		super(path, label);
	}

	public DocumentNode(String path, String label, Integer lesson, Integer level) {
		super(path, label);
		this.lesson = lesson;
		this.level = level;
	}
	
	@Override
	public boolean isDirectory() {
		return false;
	}

	public Integer getLesson() {
		return lesson;
	}

	public Integer getLevel() {
		return level;
	}
}
