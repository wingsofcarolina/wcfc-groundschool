package org.wingsofcarolina.gs.resources;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.GsConfiguration;
import org.wingsofcarolina.gs.domain.Admin;
import org.wingsofcarolina.gs.domain.Role;
import org.wingsofcarolina.gs.domain.Student;

/**
 * @author dwight
 *
 */
@Path("/test") // Note that this is actually accessed as /api due to the setUrPattern() call in GsService
public class TestResource {

  private static final Logger LOG = LoggerFactory.getLogger(TestResource.class);

  private static GsConfiguration config;

  @SuppressWarnings("static-access")
  public TestResource(GsConfiguration config)
    throws IOException, ListFolderErrorException, DbxException {
    this.config = config;
  }

  @GET
  @Path("makeAdmins")
  @Produces(MediaType.APPLICATION_JSON)
  public Response makeAdmins() {
    Admin admin;

    List<Role> roles = new ArrayList<Role>();
    roles.add(Role.ADMIN);
    admin = new Admin("dfrye@planez.co", "Dwight Frye", roles);
    admin.save();
    new Admin("dwight@openweave.org", admin).save();
    admin = new Admin("george.scheer@gmail.com", "George Scheer", roles);
    admin.save();
    new Admin("cfi@wingsofcarolina.org", admin).save();
    admin = new Admin("sue.davis@wingsofcarolina.org", "Sue Davis", roles);
    admin.save();
    new Admin("bookkeeper@wingsofcarolina.org", admin).save();

    roles.clear();
    roles.add(Role.USER);
    new Admin("airplanehunter@hotmail.com", "John Hunter", roles).save();
    new Admin("dwightrfrye@gmail.com", "Herbie Frye", roles).save();

    return Response.ok().build();
  }
}
