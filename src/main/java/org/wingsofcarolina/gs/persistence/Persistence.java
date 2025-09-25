package org.wingsofcarolina.gs.persistence;

import com.mongodb.client.MongoClients;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.UpdateResult;
import dev.morphia.Datastore;
// import co.planez.padawan.domain.dao.*;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.domain.*;

public class Persistence {

  private static final Logger LOG = LoggerFactory.getLogger(Persistence.class);

  private Datastore datastore;

  private static Persistence instance = null;

  public Persistence initialize(String mongodb) {
    if (instance == null) {
      LOG.info("Connecting to MongoDB with '{}'", mongodb);
      datastore =
        Morphia.createDatastore(MongoClients.create(mongodb), "wcfc-groundschool");

      // Map individual entity classes instead of using deprecated mapPackage
      // The map() method is deprecated, but entity mapping is now handled automatically
      // by Morphia when entities are first accessed. No explicit mapping is required.
      // If explicit mapping is needed, use datastore.getMapper().map(Class) for each class individually

      // Note: Index creation is now handled automatically by Morphia based on annotations
      // or can be done manually using the MongoDB driver if needed

      // Create DAOs
      // daoStore.put(User.class, new UserDAO(datastore));

      // Make this a singleton
      instance = this;
    }
    return this;
  }

  public static Persistence instance() {
    return instance;
  }

  public Datastore datastore() {
    return datastore;
  }

  public AutoIncrement setID(final String key, final long setvalue) {
    AutoIncrement inc = null;
    Datastore ds = Persistence.instance().datastore();
    Query<AutoIncrement> query = ds.find(AutoIncrement.class);
    List<AutoIncrement> autoIncrement = query
      .filter(Filters.eq("id", key))
      .iterator()
      .toList();

    if (autoIncrement == null || autoIncrement.size() == 0) {
      inc = new AutoIncrement(key, setvalue);
      datastore.save(inc);
    } else {
      if (autoIncrement != null && autoIncrement.get(0) != null) {
        inc = autoIncrement.get(0);
        inc.setValue(setvalue);
        datastore.save(inc);
      }
    }
    return inc;
  }

  public long getID(final String key, final long minimumValue) {
    AutoIncrement autoIncrement = null;
    Datastore ds = Persistence.instance().datastore();
    Query<AutoIncrement> query = ds
      .find(AutoIncrement.class)
      .filter(Filters.eq("key", key));

    // Use the MongoDB driver directly for atomic findAndModify operation
    // since the Morphia modify method is deprecated
    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
      .returnDocument(ReturnDocument.AFTER)
      .upsert(false);

    autoIncrement =
      ds
        .getCollection(AutoIncrement.class)
        .findOneAndUpdate(
          com.mongodb.client.model.Filters.eq("key", key),
          com.mongodb.client.model.Updates.inc("value", 1),
          options
        );

    // If none is found, we need to create one for the given key.
    if (autoIncrement == null) {
      autoIncrement = new AutoIncrement(key, minimumValue);
      datastore.save(autoIncrement);
    }
    return autoIncrement.getValue();
  }
}
