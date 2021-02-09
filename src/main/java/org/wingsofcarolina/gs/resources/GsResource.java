package org.wingsofcarolina.gs.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
import com.dropbox.core.v2.files.ListFolderResult;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.common.Slack;
import org.wingsofcarolina.gs.GsConfiguration;

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
	private Object rootIndex = null;
	
    private static final String ACCESS_TOKEN = "REDACTED";
    private static final String root = "/WCFC-Groundschool";
    private static final Cache cache = Cache.instance();

	@SuppressWarnings("static-access")
	public GsResource(GsConfiguration config) throws IOException {
		this.config = config;
		
		// Get the startup date/time format in GMT
		dateFormatGmt = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");
		
		// Create Dropbox client
        DbxRequestConfig dbconfig = DbxRequestConfig.newBuilder("wcfc/groundschool").build();
        client = new DbxClientV2(dbconfig, ACCESS_TOKEN);
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
	@Path("index")
	@Produces(MediaType.APPLICATION_JSON)
	public Response test() throws DbxApiException, DbxException, IOException, CsvException {
        // First, get the root index
		if (rootIndex == null) {
			rootIndex = getIndex("/", "index.csv", "root");
		}
    	
        return Response.ok().entity(rootIndex).build();
	}
	
	private Index getIndex(String path, String file, String label) throws DbxException, IOException, CsvException {
		byte[] bytes;

		bytes = dbFetch(path, file);
    	List<String[]> list = parseCSV(bytes);
    	
    	Index index = new Index(path, label);
    	for (String[] entry : list) {
    		if (entry[2].contentEquals("dir")) {
    			index.addChild(getIndex(entry[0], "/index.csv", entry[1]));
    		} else {
    			index.addChild(new Index(path + "/" + entry[0], entry[1]));
    		}
    	}
    	
    	return index;
	}
	
	public byte[] dbFetch(String path, String file) throws DbxException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String target = root + path + file;
    	DownloadBuilder metadata = client.files().downloadBuilder(target);
    	metadata.download(out);
    	out.close();
    	return out.toByteArray();
	}
	
	public List<String[]> parseCSV(byte[] bytes) throws IOException, CsvException {
		Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
	    CSVReader csvReader = new CSVReader(reader);
	    List<String[]> list;;
	    list = csvReader.readAll();
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
	public Response refresh(Map<Object, Object> request) throws DbxException, IOException, CsvException {
		ZoneId zoneId = ZoneId.of("US/Eastern");
		ZonedDateTime now = LocalDateTime.now().atZone(zoneId);
		LOG.info("Refresh requested at : {}", now);
		LOG.info("Request : {}", request);
		Slack.instance().sendString(Slack.Channel.NOTIFY, "Refresh requested at : " + dateFormatGmt.format(now));

		ListFolderBuilder listFolderBuilder = client.files().listFolderBuilder(root);
		ListFolderResult result = listFolderBuilder.withRecursive(true).start();
		LOG.info(result.toString());

		cache.clear();
		rootIndex = getIndex("/", "index.csv", "root");
		
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
