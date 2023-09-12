package org.wingsofcarolina.gs.domain;

public interface Person {

	public String getName();
	public String getEmail();
	public String getUUID();
	public Boolean isAdmin();
	public Boolean isStudent();
	
	public static Person getPerson(String uuid) {
		
		Person person = Student.getByUUID(uuid);
		if (person == null) {
			person = Admin.getByUUID(uuid);
		}
		return person;
	}
}
