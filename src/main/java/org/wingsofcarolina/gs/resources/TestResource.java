package org.wingsofcarolina.gs.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.GsConfiguration;
import org.wingsofcarolina.gs.domain.Admin;
import org.wingsofcarolina.gs.domain.Role;
import org.wingsofcarolina.gs.domain.Student;
import org.wingsofcarolina.gs.email.EmailLogin;
import org.wingsofcarolina.gs.email.VerificationCodeCache;

/**
 * @author dwight
 *
 */
@Path("/test")	// Note that this is actually accessed as /api due to the setUrPattern() call in GsService
public class TestResource {
	private static final Logger LOG = LoggerFactory.getLogger(TestResource.class);
	
	private static GsConfiguration config;

	@SuppressWarnings("static-access")
	public TestResource(GsConfiguration config) throws IOException, ListFolderErrorException, DbxException {
		this.config = config;
	}
	
	@GET
	@Path("cleanCache")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cleanCache() {
		VerificationCodeCache.instance().cleanCache();
		return Response.ok().build();
	}
	
	@GET
	@Path("makeAdmins")
	@Produces(MediaType.APPLICATION_JSON)
	public Response makeAdmins() {
		List<Role> roles = new ArrayList<Role>();
		
		roles.add(Role.ADMIN);
		
		new Admin("dfrye@planez.co", "Dwight Frye", roles).save();
		new Admin("dwight@openweave.org", "Dwight Frye", roles).save();
		new Admin("george.scheer@gmail.com", "George Scheer", roles).save();
		new Admin("cfi@wingsofcarolina.org", "George Scheer", roles).save();
		new Admin("sue.davis@wingsofcarolina.org", "Sue Davis", roles).save();
		
		return Response.ok().build();
	}
}
