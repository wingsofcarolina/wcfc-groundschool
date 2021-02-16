package org.wingsofcarolina.gs.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.palantir.roboslack.api.MessageRequest;
import com.palantir.roboslack.api.attachments.Attachment;
import com.palantir.roboslack.api.attachments.components.Author;
import com.palantir.roboslack.api.attachments.components.Color;
import com.palantir.roboslack.api.attachments.components.Field;
import com.palantir.roboslack.api.attachments.components.Footer;
import com.palantir.roboslack.api.attachments.components.Title;
import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadBuilder;
import com.dropbox.core.v2.files.ListFolderBuilder;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderGetLatestCursorResult;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.paper.Cursor;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.model.DataModel;
import org.wingsofcarolina.gs.model.DirectoryNode;
import org.wingsofcarolina.gs.model.DocumentNode;
import org.wingsofcarolina.gs.model.Node;
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
	private DbxClientV2 client;
	private DataModel model = null;

	private static final String APPSECRET = "REDACTED";
    private static final String ACCESS_TOKEN = "REDACTED";
    private static final String root = "/WCFC-Groundschool";
    private static final Cache cache = Cache.instance();
    
    // Slack credentials
    private static final String CLIENT_ID = "REDACTED";
    private static final String CLIENT_SECRET = "REDACTED";
	private SlackAuthService slackAuth;

	private AuthUtils authUtils;
	private boolean authEnabled = false;
	
	private ObjectMapper mapper;
	private ReentrantLock lock = new ReentrantLock();
	private int i = 0;

	private com.slack.api.Slack slack;

	@SuppressWarnings("static-access")
	public GsResource(GsConfiguration config) throws IOException, ListFolderErrorException, DbxException {
		this.config = config;
		
		// See if we have turned auth on
		authEnabled = config.getAuth();
		
		// Get authorization utils object instance
		authUtils = AuthUtils.instance();
		
		// For JSON serialization/deserialization
		mapper = new ObjectMapper();
		
		// Get the startup date/time format in GMT
		dateFormatGmt = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");
		
		// Create Dropbox client
        DbxRequestConfig dbconfig = DbxRequestConfig.newBuilder("wcfc/groundschool").build();
        client = new DbxClientV2(dbconfig, ACCESS_TOKEN);
        
        // Create Slack authentication API service object
        slackAuth = new SlackAuthService(CLIENT_ID, CLIENT_SECRET);
        slack = com.slack.api.Slack.getInstance();

        
        // Kick off the initial load of the model
        loadDataModel();
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
		        
		        return Response.ok().entity(reply).build();
	        } else {
	        	return Response.status(404).build();
	        }
		} else {
	        reply.put("name", "Anonymous");
	        reply.put("email", "nobody@wingsofcarolina.org");
	        reply.put("anonymous", true);
	        
	        return Response.ok().entity(reply).build();
		}
	}
	
	@GET
	@Path("index")
	@Produces(MediaType.APPLICATION_JSON)
	public Response index(@CookieParam("wcfc.gs.token") Cookie cookie) throws DbxApiException, DbxException, IOException, CsvException, URISyntaxException {
		if (cookie == null) {
			// They have not been validated, so go demand they do it
	        return Response.status(401).build();
		}
		
		// Get the user, so we can re-issue the cookie
		User user = authUtils.getUserFromCookie(cookie);
		
		if (model == null) {
			loadDataModel();
		}
		
        return Response.ok().entity(model).cookie(authUtils.generateCookie(user)).build();
	}
	
	private void loadDataModel() throws ListFolderErrorException, DbxException, JsonGenerationException, JsonMappingException, IOException {

		ExecutorService executor = Executors.newFixedThreadPool(1);
		
		executor.submit(() -> {
			// Get the lock, because we only want to run this one-at-a-time
			lock.lock();
			LOG.info("Load data lock acquired on thread {}", Thread.currentThread().getName());
			
			// Start with a clear cache
			cache.clear();
	
			try {
				// First, force DropBox to show us the latest view of everything
				ListFolderBuilder listFolderBuilder = client.files().listFolderBuilder(root);
				ListFolderResult result = listFolderBuilder.withRecursive(true).start();
				LOG.info("Initial  : {}", result.toString());
				while (result.getHasMore()) {
					result = client.files().listFolderContinue(result.getCursor());
					LOG.info("Continue : {}", result.toString());
				}

				Index rootIndex = getIndex("/", "index.csv", "root");
				
				// Iterate over all root index children, knowing 
				// they are all directories
				model = new DataModel();
				for (Index idx : rootIndex.getChildren()) {
					Node node = new DirectoryNode(idx.getPath(), idx.getLabel());
					model.addChild(node);
					
					for (Index entry : idx.getChildren()) {
						node.addChild(new DocumentNode(entry.getPath(),
								entry.getLabel(), entry.getLesson(), entry.getLevel()));
					}
				}
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				LOG.info("Load data lock released on thread {}", Thread.currentThread().getName());
				lock.unlock();
			}
		});
	}
	
	private Index getIndex(String path, String file, String label) throws ListFolderErrorException, DbxException {
		byte[] bytes;
		Index index = null;

		bytes = dbFetch(path, file);
		List<String[]> list;
		try {
			list = parseCSV(bytes);
			
			index = new Index(path, label);
			for (String[] entry : list) {
				if (entry.length == 5) { 
					if (entry[4].contentEquals("dir")) {
						Index idx = getIndex(entry[2], "/index.csv", entry[3]);
						idx.setDirectory();
						index.addChild(idx);
					} else {
						Index idx = new Index(path + "/" + entry[2], entry[3]);
						idx.setLesson(Integer.valueOf(entry[0]));
						idx.setLevel(Integer.valueOf(entry[1]));
						idx.setDocument();
						index.addChild(idx);
					}
				} else {
					LOG.error("Malformed line from CSV file, incorrect number of fields");
					Slack.instance().sendString(Slack.Channel.NOTIFY, "Malformed line from CSV file, incorrect number of fields");
				}
			}
		} catch (IOException | CsvException e) {
			LOG.error("There seems to be a serious problem with the CSV file : {}", path + "/" + file);
			Slack.instance().sendString(Slack.Channel.NOTIFY,
					"There seems to be a serious problem with the CSV file : " + path + "/" + file);
		}

		return index;
	}
	
	public byte[] dbFetch(String path, String file) throws ListFolderErrorException, DbxException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String target = root + path + file;

        // Then download the latest version
    	DownloadBuilder metadata = client.files().downloadBuilder(target);
    	try {
			metadata.download(out);
	    	out.close();
		} catch (DbxException | IOException e) {
			LOG.error("Unable to download {} from Dropbox", target);
			Slack.instance().sendString(Slack.Channel.NOTIFY, "Unable to download '" + target + "' from Dropbox; investigate why." );
		}
    	
    	return(out.toByteArray());
	}
	
	public List<String[]> parseCSV(byte[] bytes) throws IOException, CsvException {
		Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
		CSVReader csvReader = new CSVReaderBuilder(reader)
                // Skip the header
                .withSkipLines(1)
                .build();
	    List<String[]> list = csvReader.readAll();
	    reader.close();
	    csvReader.close();
		return list;
	}
	
	@POST
	@Path("fetch")
	@Produces("application/pdf")
	public Response fetchFile(Map<String, String> request) throws IOException, DbxException {
		byte[] bytes;
		
		String name = request.get("name");
		int index = name.lastIndexOf('/');
		String path = name.substring(0, index);
		String file = name.substring(index);
		
		if (cache.hasEntry(name)) {
			bytes = cache.get(name);
		} else {
			LOG.info("Fetching \"{}\" from Dropbox, not found in cache", file);
			bytes = dbFetch(path, file);
			cache.put(name, bytes);
		}
		
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return Response.ok().type("application/pdf").entity(inputStream).build();
	}
	
	@GET
	@Path("refresh")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verify(@DefaultValue("") @QueryParam("challenge") String challenge) throws DbxException, IOException, CsvException {
		LOG.info("Challenge from Dropbox received, responding");
		LOG.info("Challenge : {}", challenge);
		Slack.instance().sendString(Slack.Channel.NOTIFY, "Challenge received from Dropbox, responding");
		return Response.ok().header("X-Content-Type-Options", "nosniff").entity(challenge).build();
	}
	
	@POST
	@Path("refresh")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response refresh(@Context HttpHeaders httpheaders,
			Map<Object, Object> request) throws DbxException, IOException, CsvException {
		
		// Validate the signature if we are in PROD mode
		if (config.getMode().equals("PROD")) {
			String signature = httpheaders.getHeaderString("X-Dropbox-Signature");
			LOG.info("DB HMAC : {}", signature);
			if (signature == null || signature.isEmpty()) {
				Response.status(404).build();
			}
		}
		ZoneId zoneId = ZoneId.of("US/Eastern");
		ZonedDateTime now = LocalDateTime.now().atZone(zoneId);
		LOG.info("Refresh requested at : {}", now);
		LOG.info("Request : {}", request);
		Slack.instance().sendString(Slack.Channel.NOTIFY, "Refresh requested at : " + dateFormatGmt.format(now));

		// Now, rebuild our world.
		loadDataModel();
		
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
		
		MessageRequest msg = MessageRequest.builder().username("Groundschool Contact")
				.channel("contact")
				.text("*Groundschool contact sent at : " + dateFormatGmt.format(now) + "*") // + SlackMarkdown.EMOJI.decorate("new"))
				.addAttachments(Attachment.builder()
						.fallback("Gs : " + message)
						.author(Author.of(name))
						.color(Color.good())
						.title(Title.builder()
						.text(email)
						.link(url("https://groundschool.wingsofcarolina.org"))
						.build())
						.footer(Footer.builder().text("Generated By WCFC Groundschool Server")
								.icon(url("https://platform.slack-edge.com/img/default_application_icon.png"))
								.timestamp(now.toEpochSecond()).build())
						.addFields(Field.builder().isShort(true).title("Phone").value(phone).build())
						.addFields(Field.builder().isShort(true).title("Email").value(email).build())
						.addFields(Field.builder().isShort(false).title("Message").value(message).build())
						.build())
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
		LOG.info("New user : {}", user);
		
		// User authenticated and identified. Save the info.
		NewCookie cookie = authUtils.generateCookie(user);
		
        return Response.seeOther(new URI("/")).cookie(cookie).build();
	}
	
//	@GET
//	@Path("mock")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response mock() throws URISyntaxException {
//
//		User user = new User("Dwight Frye", "dfrye@planez.co", "REDACTED", "REDACTED", "REDACTED");
//		
//		// User authenticated and identified. Save the info.
//		NewCookie cookie = authUtils.generateCookie(user);
//		
//        return Response.seeOther(new URI("/")).cookie(cookie).build();
//	}
	
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
