package org.wingsofcarolina.gs.resources;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Index {
	private Integer lesson = 0;
	private Integer level = 0;
	private String path;
	private String label;
	private boolean directory = false;
	private List<Index> children = null;
	
	public Index(String path, String label) {
		this.path = path;
		this.label = label;
	}

	public Integer getLesson() {
		return lesson;
	}

	public void setLesson(Integer lesson) {
		this.lesson = lesson;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public void setDirectory() {
		this.directory = true;
	}

	public boolean isDirectory() {
		return this.directory == true;
	}
	public void setDocument() {
		this.directory = false;
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
