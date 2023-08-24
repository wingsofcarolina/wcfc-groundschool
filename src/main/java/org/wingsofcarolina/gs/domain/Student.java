package org.wingsofcarolina.gs.domain;

import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import org.wingsofcarolina.gs.domain.dao.StudentDAO;
import org.wingsofcarolina.gs.persistence.Persistence;

@Entity("Students")
public class Student implements Person {
	private static StudentDAO dao = new StudentDAO();
	
	@Id
	@JsonIgnore
	private ObjectId id;
	
	private Long studentId;
	private String uuid;
	private String section;
	private String name;
	private String email;
	
	@Transient
	private String token;
	
	public static String ID_KEY = "students";

	public Student() {
	}
	
	public Student(String section, String name, String email) {
		this.section = section;
		this.name = name;
		this.email = email;
		this.uuid = UUID.randomUUID().toString();
		this.studentId = Persistence.instance().getID(ID_KEY, 1000);
	}
	
	public Boolean isAdmin() {
		return false;
	}
	
	public long getId() {
		return studentId;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getUUID() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "Student [section=" + section + ", name=" + name + ", email=" + email + "]";
	}

	/*
	 * Database Management Functionality
	 */
	public static long count() {
		return dao.count();
	}
	
	public static void drop() {
		dao.drop();
	}
	
	@SuppressWarnings("unchecked")
	public static List<Student> getAll() {
		return (List<Student>) dao.getAll();
	}

	public static Student getByID(long id) {
		return (Student) dao.getByID(id);
	}
	
	public static Student getByUUID(String uuid) {
		return dao.getByUUID(uuid);
	}

	public static Student getByEmail(String email) {
		return dao.getByEmail(email);
	}
	
	public void save() {
		dao.save(this);
	}
	
	public void delete() {
		dao.delete(this);
	}
}
