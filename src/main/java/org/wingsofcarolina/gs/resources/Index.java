package org.wingsofcarolina.gs.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Index implements Comparable<Index> {

  private Integer lesson = 0;
  private String path;
  private String label;
  private Boolean required;
  private Boolean directory = false;
  private List<Index> children = null;

  public Index() {}

  public Index(String path, String label, Integer lesson, Boolean required) {
    this.path = path;
    if (required == null) {
      this.required = true;
    } else {
      this.required = required;
    }
    this.label = label;
    this.lesson = lesson;
  }

  public Index(String path, String label, Integer lesson) {
    this(path, label, 0, true);
  }

  public Index(String path, String label) {
    this(path, label, 0);
  }

  public Integer getLesson() {
    return lesson;
  }

  public void setLesson(Integer lesson) {
    this.lesson = lesson;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Boolean isRequired() {
    if (required == null) {
      required = true;
    }
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public List<Index> getChildren() {
    return children;
  }

  public void setDirectory() {
    this.directory = true;
  }

  public Boolean isDirectory() {
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

  public int compareTo(Index index) {
    if (getLesson() == null || index.getLesson() == null) {
      return 0;
    }
    return getLesson().compareTo(index.getLesson());
  }

  @Override
  public String toString() {
    return "Index [path=" + path + ", label=" + label + ", lesson=" + lesson + "]";
  }
}
