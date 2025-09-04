package org.wingsofcarolina.gs.domain.dao;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.domain.Admin;
import org.wingsofcarolina.gs.domain.Student;
import org.wingsofcarolina.gs.persistence.Persistence;

public class StudentDAO extends SuperDAO {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(StudentDAO.class);

  public StudentDAO() {
    super(Student.class);
  }

  public Student getByEmail(String email) {
    Datastore ds = Persistence.instance().datastore();
    Query<Student> query = ds.find(Student.class);
    List<Student> users = query.filter(Filters.eq("email", email)).iterator().toList();
    if (users.size() > 0) {
      return users.get(0);
    } else {
      return null;
    }
  }

  public Student getByUUID(String uuid) {
    Datastore ds = Persistence.instance().datastore();
    Query<Student> query = ds.find(Student.class);
    List<Student> users = query.filter(Filters.eq("uuid", uuid)).iterator().toList();
    if (users.size() > 0) {
      return users.get(0);
    } else {
      return null;
    }
  }

  public List<Student> getAllForSection(String section) {
    Datastore ds = Persistence.instance().datastore();
    Query<Student> query = ds.find(Student.class);
    List<Student> users = query
      .filter(Filters.eq("section", section))
      .iterator()
      .toList();
    return users;
  }
}
