package org.wingsofcarolina.gs.domain;

public interface Person {
  public String getName();

  public String getEmail();

  public String getUUID();

  public Boolean isAdmin();

  public Boolean isStudent();

  public static Person getPerson(String uuid) {
    Person person = Admin.getByUUID(uuid);
    if (person == null) {
      person = Student.getByUUID(uuid);
    }
    return person;
  }
}
