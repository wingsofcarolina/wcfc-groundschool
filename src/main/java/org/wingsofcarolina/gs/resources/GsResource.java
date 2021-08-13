package org.wingsofcarolina.gs.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.palantir.roboslack.api.MessageRequest;
import com.palantir.roboslack.api.attachments.Attachment;
import com.palantir.roboslack.api.attachments.Attachment.Builder;
import com.palantir.roboslack.api.attachments.components.Author;
import com.palantir.roboslack.api.attachments.components.Color;
import com.palantir.roboslack.api.attachments.components.Field;
import com.palantir.roboslack.api.attachments.components.Footer;
import com.palantir.roboslack.api.attachments.components.Title;
import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.model.User;
import org.wingsofcarolina.gs.slack.Slack;
import org.wingsofcarolina.gs.slack.SlackAuthService;
import org.wingsofcarolina.gs.GsConfiguration;
import org.wingsofcarolina.gs.authentication.AuthUtils;
import org.wingsofcarolina.gs.common.APIException;

/**
 * @author dwight
 *
 */
@Path("/")	// Note that this is actually accessed as /api due to the setUrPattern() call in OralService
public class GsResource {
	private static final Logger LOG = LoggerFactory.getLogger(GsResource.class);
	
	private static GsConfiguration config;
	private static String versionOverride = null;
	private DateTimeFormatter dateFormatGmt;

    // Slack credentials
    private static final String CLIENT_ID = "REDACTED";
    private static final String CLIENT_SECRET = "REDACTED";
	private SlackAuthService slackAuth;

	private AuthUtils authUtils;
	private boolean authEnabled = false;
	
	private static boolean mockUser = true; // When we are developing and don't want to authenticate with Slack
	
	private static String gs_root;
	
	private ObjectMapper mapper;

	@SuppressWarnings("static-access")
	public GsResource(GsConfiguration config) throws IOException, ListFolderErrorException, DbxException {
		this.config = config;
		
		// See if we have turned auth on
		authEnabled = config.getAuth();
		
		// Get authorization utils object instance
		authUtils = AuthUtils.instance();
		
		// Get the GS document root
		gs_root = config.getGsroot();
		
		// For JSON serialization/deserialization
		mapper = new ObjectMapper();
		
		// Get the startup date/time format in GMT
		dateFormatGmt = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");
		
        // Create Slack authentication API service object
        slackAuth = new SlackAuthService(CLIENT_ID, CLIENT_SECRET);
	}
	
	@GET
	@Path("version")
	@Produces(MediaType.APPLICATION_JSON)
	public Response version() {
		Map<String, String> version = getBuildMetadata();
		if (version == null) {
			return Response.status(404).build();
		} else {
			return Response.ok().entity(version).build();
		}
	}
	
	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response user(@CookieParam("wcfc.gs.token") Cookie cookie) {
        Map<String, Object> reply = new HashMap<String, Object>();

        if (authEnabled) {
	        User user = authUtils.getUserFromCookie(cookie);
	        
	        if (user != null) {
		        reply.put("name", user.getName());
		        reply.put("email", user.getEmail());
		        reply.put("anonymous", false);
		        if (user.getEmail().contains("dfrye@planez.co")) {
		        	reply.put("admin", true);
		        } else {
		        	reply.put("admin", false);
		        }

		        return Response.ok().entity(reply).build();
	        } else {
	        	return Response.status(404).build();
	        }
		} else {
			if ( ! mockUser) {
		        reply.put("name", "Anonymous");
		        reply.put("email", "nobody@wingsofcarolina.org");
		        reply.put("anonymous", true);
			} else {
		        reply.put("name", "Dwight Frye");
		        reply.put("email", "dfrye@planez.co");
		        reply.put("anonymous", false);
		        reply.put("admin", true);
			}
	        return Response.ok().entity(reply).build();
		}
	}
	
	@GET
	@Path("index/{section}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response index(@CookieParam("wcfc.gs.token") Cookie cookie,
			@PathParam("section") String section) throws DbxApiException, DbxException, IOException, CsvException, URISyntaxException {
		User user = null;
		
		if (authEnabled == true && cookie == null) {
			// They have not been validated, so go demand they do it
	        return Response.status(401).build();
		}
		
		// Get the user, so we can re-issue the cookie
		if (authEnabled == false && cookie != null) {
			user = authUtils.getUserFromCookie(cookie);
		} else {
			user = mockUser();
		}
		
		Index index = getSectionIndex(section);
		
        return Response.ok().entity(index).cookie(authUtils.generateCookie(user)).build();
	}
	
	private Index getSectionIndex(String section) {
		Index index = null;
		try {
			String indexFile = gs_root + "/" + section + "/Index.json";
			LOG.debug("Reading index : {}", indexFile);
			index = mapper.readValue(new File(indexFile), Index.class);
			
			// Sort by class number
			List<Index> children = index.getChildren();
			Collections.sort(children);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return index;
	}
	
	private void writeSectionIndex(String section, Index index) {
		try {
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(index);

			try (FileOutputStream fos = new FileOutputStream(gs_root + "/" + section + "/Index.json");
					FileChannel channel = fos.getChannel();
					FileLock lock = channel.lock()) {
				fos.write(json.getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@POST
	@Path("fetch")
	@Produces("application/pdf")
	public Response fetchFile(Map<String, String> request) throws IOException, DbxException {
		
		String name = request.get("name");
		
		File file = new File(gs_root + "/" + name);
		if (file.exists()) {
	        InputStream inputStream = new FileInputStream(file);
	        return Response.ok().type("application/pdf").entity(inputStream).build();
		} else {
			return Response.status(404).build();
		}
	}
	
	@PUT
	@Path("moveUp/{section}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response moveUp(@CookieParam("wcfc.gs.token") Cookie cookie,
			@PathParam("section") String section, @QueryParam("path") String path) {
        User user = authUtils.getUserFromCookie(cookie);

        int i = 0;
        Index index = getSectionIndex(section);
		List<Index> children = index.getChildren();
		Iterator<Index> it = children.iterator();
		while (it.hasNext()) {
			Index item = it.next();
			if (path.equals(item.getPath())) {
				System.out.println("Moving up : " + item);
				children.remove(item);
				
				// Reinsert the item at the previous index
				children.add(i-1, item);
				writeSectionIndex(section, index);
				break;
			}
			i++;
		}
        return Response.ok().build();
	}

	@PUT
	@Path("moveDown/{section}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response moveDown(@CookieParam("wcfc.gs.token") Cookie cookie,
			@PathParam("section") String section, @QueryParam("path") String path) {
        User user = authUtils.getUserFromCookie(cookie);

        int i = 0;
        Index index = getSectionIndex(section);
		List<Index> children = index.getChildren();
		Iterator<Index> it = children.iterator();
		while (it.hasNext()) {
			Index item = it.next();
			if (path.equals(item.getPath())) {
				System.out.println("Moving down : " + item);
				children.remove(item);
				
				// Reinsert the item at the previous index
				children.add(i+1, item);
				writeSectionIndex(section, index);
				break;
			}
			i++;
		}
        return Response.ok().build();
	}
	
	@DELETE
	@Path("delete/{section}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFile(@CookieParam("wcfc.gs.token") Cookie cookie,
			@PathParam("section") String section, @QueryParam("path") String path) {
        User user = authUtils.getUserFromCookie(cookie);

        Index index = getSectionIndex(section);
		List<Index> children = index.getChildren();
		Iterator<Index> it = children.iterator();
		while (it.hasNext()) {
			Index item = it.next();
			if (path.equals(item.getPath())) {
				LOG.info("Removing : {}", item.getPath());
				children.remove(item);
				new File(gs_root + "/" + item.getPath()).delete();
				writeSectionIndex(section, index);
				break;
			}
		}
        return Response.ok().build();
	}
	
	@POST
	@Path("contact")
	@Produces(MediaType.APPLICATION_JSON)
	public Response contact(Map<String, String> request) {
        Slack.instance().sendMessage(Slack.Channel.CONTACT, contactMessage(request));
		return Response.ok().build();
	}
	
	private MessageRequest contactMessage(Map<String, String> request) {
		String name = request.getOrDefault("name", "NONE");
		String phone = request.getOrDefault("phone", "NONE");
		String email = request.getOrDefault("email", "NONE");
		String message = request.getOrDefault("message", "NONE");

		ZoneId zoneId = ZoneId.of("US/Eastern");
		LOG.info("Zone : {}", zoneId);
		ZonedDateTime now = LocalDateTime.now().atZone(zoneId);
		
		Builder ab = Attachment.builder()
				.fallback("Gs : " + message)
				.author(Author.of(name))
				.color(Color.good())
				.title(Title.builder()
				.text(email)
				.link(url("mailto:" + email))
				.build())
				.footer(Footer.builder().text("Generated By WCFC Groundschool Server")
						.icon(url("https://platform.slack-edge.com/img/default_application_icon.png"))
						.timestamp(now.toEpochSecond()).build());
		if ( ! phone.equals("NONE")) {
				ab.addFields(Field.builder().isShort(true).title("Phone").value(phone).build());
		}
		ab.addFields(Field.builder().isShort(false).title("Message").value(message).build());
		
		MessageRequest msg = MessageRequest.builder().username("Groundschool Contact")
				.channel("contact")
				.text("*Groundschool contact sent at : " + dateFormatGmt.format(now) + "*") // + SlackMarkdown.EMOJI.decorate("new"))
				.addAttachments(ab.build())
				.build();

		return msg;
	}
	
	@GET
	@Path("auth")
	@Produces(MediaType.TEXT_HTML)
	@SuppressWarnings("unchecked")
	public Response auth(@QueryParam("code") String code) throws URISyntaxException, APIException {
		LOG.info("Code : {}", code);
		
		Map<String, Object> details = slackAuth.authenticate(code);
		Map<String, Object> user_details = (Map<String, Object>)details.get("authed_user");
		
		String user_id = (String) user_details.get("id");
		String access_token = (String) user_details.get("access_token");
		String team_id = (String) ((Map<String, Object>)details.get("team")).get("id");

		Map<String, Object> identity = slackAuth.identity(access_token);
		Map<String, String>userMap = (Map<String, String>) identity.get("user");
		String name = userMap.get("name");
		String email = userMap.get("email");
		
		User user = new User(name, email, user_id, team_id, access_token);
		LOG.info("Authenticated user : {}", user);
		Slack.instance().sendString(Slack.Channel.NOTIFY, "Authenticated user : " + user);
		
		// User authenticated and identified. Save the info.
		NewCookie cookie = authUtils.generateCookie(user);
		
        return Response.seeOther(new URI("/")).cookie(cookie).build();
	}
	
	@GET
	@Path("freespace")
	@Produces(MediaType.APPLICATION_JSON)
	public Response freespace() {
		File fs = new File("tmp");
		Map<String, Long> myMap = new HashMap<String, Long>() {{
	        put("space", fs.getFreeSpace());
	    }};
	    
	    return Response.ok().entity(myMap).build();
	}
	
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response upload(@FormDataParam("label") String label,
			@FormDataParam("lesson") Integer lesson,
			@FormDataParam("section") String section,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetails)
			throws IOException, CsvException, ParseException {
		
		UUID uuid = UUID.randomUUID();
		String path = section + "/" + uuid.toString() + ".pdf";
	    File targetFile = new File("/tmp/" + uuid.toString() + ".tmp");
	    OutputStream outStream = new FileOutputStream(targetFile);

	    byte[] buffer = new byte[8 * 1024];
	    int bytesRead;
	    while ((bytesRead = uploadedInputStream.read(buffer)) != -1) {
	        outStream.write(buffer, 0, bytesRead);
	    }
	    uploadedInputStream.close();
	    outStream.close();

	    // Check out the type 
	    String fileType = Files.probeContentType(targetFile.toPath());
	    if (fileType.equals("application/pdf")) {
			String newname = gs_root + "/" + path;
			if (targetFile.renameTo(new File(newname))) {
				LOG.info("Creating : {}", newname);
				
				Index index = getSectionIndex(section);
				index.getChildren().add(new Index(path, label, lesson));
				writeSectionIndex(section, index);
				return Response.ok().build();
		    } else {
		    	LOG.error("Renaming of temp upload file failed : {}", path);
				return Response.status(500).build();
		    }
	    } else {
	    	LOG.error("File was not a PDF, rejected");
	    	targetFile.delete();
			return Response.status(400).build();
	    }
	}
	
	@POST
	@Path("update")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public Response update(@FormDataParam("label") String label,
			@FormDataParam("lesson") Integer lesson,
			@FormDataParam("path") String path,
			@FormDataParam("section") String section)
			throws IOException, CsvException, ParseException {
		
		boolean found = false;

		Index index = getSectionIndex(section);
		for (Index child : index.getChildren()) {
			if (child.getPath().equals(path)) {
		    	LOG.error("Modifying entry : {}", path);

				child.setLesson(lesson);
				child.setLabel(label);

				writeSectionIndex(section, index);

				found = true;
			}
		}
		
		if (found) {
			return Response.ok().build();
		} else {
	    	LOG.error("Modify failed, entry not found : {}", path);

			return Response.status(404).build();
		}
	}
	
	@GET
	@Path("mock")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response mock() throws URISyntaxException {

		User user = mockUser();
		
		// User authenticated and identified. Save the info.
		NewCookie cookie = authUtils.generateCookie(user);
		
        return Response.seeOther(new URI("/")).cookie(cookie).build();
	}
	
	private User mockUser() {
		return new User("Dwight Frye", "dfrye@planez.co", "REDACTED", "REDACTED", "REDACTED");
	}
	
    private static URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage(), e);
        }
    }

	public static Map<String, String> getBuildMetadata() {
		Map<String, String> version = new HashMap<String, String>();
		
		if (config.getMode().equals("DEV")) {
            if (versionOverride != null) {
            	version.put("version", versionOverride);
            } else {
            	version.put("version", "DEV");
            }
            version.put("build", "DEV");
            return version;
		} else {
		    Enumeration<URL> resEnum;
		    try {
		        resEnum = GsResource.class.getClassLoader().getResources(JarFile.MANIFEST_NAME);
		        while (resEnum.hasMoreElements()) {
		            URL url = resEnum.nextElement();
		            InputStream is = url.openStream();
		            if (is != null) {
		                Manifest manifest = new Manifest(is);
		                Attributes mainAttribs = manifest.getMainAttributes();
		                version.put("version", mainAttribs.getValue("Git-Build-Version"));
		                version.put("build", mainAttribs.getValue("Git-Commit-Id"));
		                if (version != null) {
		                    return version;
		                }
		            }
		        }
		    } catch (IOException e1) {
		        // Silently ignore wrong manifests on classpath?
		    	LOG.info("IOException during manifest retrieval : {}", e1);
		    }
		    return null; 
		}
	}
}
