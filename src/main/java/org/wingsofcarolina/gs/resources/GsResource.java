package org.wingsofcarolina.gs.resources;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.GsConfiguration;
import org.wingsofcarolina.gs.authentication.AuthUtils;
import org.wingsofcarolina.gs.common.APIException;
import org.wingsofcarolina.gs.domain.Admin;
import org.wingsofcarolina.gs.domain.Person;
import org.wingsofcarolina.gs.domain.Role;
import org.wingsofcarolina.gs.domain.Student;
import org.wingsofcarolina.gs.domain.VerificationCode;
import org.wingsofcarolina.gs.email.EmailUtils;
import org.wingsofcarolina.gs.model.User;
import org.wingsofcarolina.gs.services.HousekeepingService;
import org.wingsofcarolina.gs.slack.Slack;
import org.wingsofcarolina.gs.slack.SlackAuthService;

/**
 * @author dwight
 *
 */
@Path("/") // Note that this is actually accessed as /api due to the setUrPattern() call in GsService
public class GsResource {

  private static final Logger LOG = LoggerFactory.getLogger(GsResource.class);

  private static GsConfiguration config;
  private static String versionOverride = null;
  private DateTimeFormatter dateFormatGmt;

  private SlackAuthService slackAuth;

  private AuthUtils authUtils;
  private boolean authEnabled = false;
  private boolean mockAdmin = false;
  private String mockUser = null;

  private static String gs_root;

  private ObjectMapper mapper;

  @Context
  private UriInfo uriInfo;

  /**
   * Extracts the base URL from the current request context
   */
  private String getBaseUrl() {
    if (uriInfo != null) {
      URI baseUri = uriInfo.getBaseUri();
      String scheme = baseUri.getScheme();
      String host = baseUri.getHost();
      int port = baseUri.getPort();

      StringBuilder baseUrl = new StringBuilder();
      baseUrl.append(scheme).append("://").append(host);

      // Only include port if it's not the default port for the scheme
      if (
        (port != -1) &&
        !(
          (scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)
        )
      ) {
        baseUrl.append(":").append(port);
      }

      return baseUrl.toString();
    }
    return null;
  }

  @SuppressWarnings("static-access")
  public GsResource(GsConfiguration config)
    throws IOException, ListFolderErrorException, DbxException {
    this.config = config;

    // See if we have turned auth on
    authEnabled = config.getAuth();
    mockAdmin = config.getMockAdmin();
    mockUser = config.getMockUser();

    // Get authorization utils object instance
    authUtils = AuthUtils.instance();

    // Get the GS document root
    gs_root = config.getGsroot();

    // For JSON serialization/deserialization
    mapper = new ObjectMapper();

    // Get the startup date/time format in GMT
    dateFormatGmt = DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss z");

    // Create Slack authentication API service object (only if credentials are configured)
    if (
      config.getSlackClientId() != null &&
      !config.getSlackClientId().equals("none") &&
      config.getSlackClientSecret() != null &&
      !config.getSlackClientSecret().equals("none")
    ) {
      slackAuth =
        new SlackAuthService(config.getSlackClientId(), config.getSlackClientSecret());
    } else {
      LOG.warn(
        "Slack OAuth credentials not configured - Slack authentication will be disabled"
      );
      slackAuth = null;
    }
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
    // Trigger housekeeping if needed (runs asynchronously)
    HousekeepingService.getInstance().triggerHousekeepingIfNeeded();

    Map<String, Object> reply = new HashMap<String, Object>();

    if (authEnabled) {
      User user = authUtils.getUserFromCookie(cookie);

      if (user != null) {
        reply.put("name", user.getName());
        reply.put("email", user.getEmail());
        reply.put("admin", user.isAdmin());
        reply.put("anonymous", false);

        return Response.ok().entity(reply).build();
      } else {
        return Response.status(404).build();
      }
    } else {
      if (mockUser != null) {
        String[] fields = mockUser.split(":");
        reply.put("name", fields[0]);
        reply.put("email", fields[1]);
      } else {
        reply.put("name", "Anonymous");
        reply.put("email", "nobody@wingsofcarolina.org");
      }
      if (mockAdmin) {
        reply.put("anonymous", false);
        reply.put("admin", true);
      } else {
        reply.put("anonymous", true);
        reply.put("admin", false);
      }

      return Response.ok().entity(reply).build();
    }
  }

  @GET
  @Path("index/{section}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response index(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @PathParam("section") String section
  ) throws DbxApiException, DbxException, IOException, CsvException, URISyntaxException {
    User user = null;

    if (authEnabled == true && cookie == null) {
      // They have not been validated, so go demand they do it
      return Response.status(401).build();
    }

    // Get the user, so we can re-issue the cookie
    if (authEnabled == false && cookie != null) {
      user = authUtils.getUserFromCookie(cookie);
    }

    Index index = getSectionIndex(section);

    if (user != null) {
      NewCookie newCookie = authUtils.generateCookie(user);
      return Response
        .ok()
        .entity(index)
        .header("Set-Cookie", AuthUtils.sameSite(newCookie))
        .build();
    } else {
      return Response.ok().entity(index).build();
    }
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

      try (
        FileOutputStream fos = new FileOutputStream(
          gs_root + "/" + section + "/Index.json"
        );
        FileChannel channel = fos.getChannel();
        FileLock lock = channel.lock()
      ) {
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
  public Response fetchFile(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    Map<String, String> request
  ) throws IOException, DbxException {
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
  public Response moveUp(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @PathParam("section") String section,
    @QueryParam("path") String path
  ) {
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
        children.add(i - 1, item);
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
  public Response moveDown(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @PathParam("section") String section,
    @QueryParam("path") String path
  ) {
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
        children.add(i + 1, item);
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
  public Response deleteFile(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @PathParam("section") String section,
    @QueryParam("path") String path
  ) {
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

  @GET
  @Path("auth")
  @Produces(MediaType.TEXT_HTML)
  @SuppressWarnings("unchecked")
  public Response slackAuth(@QueryParam("code") String code)
    throws URISyntaxException, APIException {
    NewCookie cookie = null;
    User user = null;
    String redirect = "/";

    LOG.info("Code : {}", code);

    // Check if Slack OAuth is configured
    if (slackAuth == null) {
      LOG.warn("Slack OAuth not configured - authentication disabled");
      return Response
        .status(Response.Status.SERVICE_UNAVAILABLE)
        .entity("Slack authentication is not configured")
        .build();
    }

    Map<String, Object> details = slackAuth.authenticate(code);
    Map<String, Object> user_details = (Map<String, Object>) details.get("authed_user");

    String user_id = (String) user_details.get("id");
    String access_token = (String) user_details.get("access_token");
    String team_id = (String) ((Map<String, Object>) details.get("team")).get("id");

    Map<String, Object> identity = slackAuth.identity(access_token);
    Map<String, String> userMap = (Map<String, String>) identity.get("user");
    String name = userMap.get("name");
    String email = userMap.get("email");

    Student student = Student.getByEmail(email);
    if (student != null) {
      LOG.info("Authenticated student : {}", student);
      Slack
        .instance()
        .sendString(Slack.Channel.NOTIFY, "Authenticated student : " + student);
      cookie = authUtils.generateCookie(new User(student));
      redirect += student.getSection().toLowerCase();
      user = new User(student);
    } else {
      Admin admin = Admin.getByEmail(email);
      if (admin != null) {
        LOG.info("Authenticated admin user : {}", admin);
        Slack
          .instance()
          .sendString(Slack.Channel.NOTIFY, "Authenticated admin user : " + admin);
        cookie = authUtils.generateCookie(new User(admin));
        user = new User(admin);
      }
    }

    Slack.instance().sendString(Slack.Channel.NOTIFY, "Authenticated user : " + user);

    // User authenticated and identified. Save the info.
    if (cookie != null) {
      return Response
        .seeOther(new URI(redirect))
        .header("Set-Cookie", AuthUtils.sameSite(cookie))
        .build();
    } else {
      return Response
        .seeOther(new URI("/failure"))
        .header("Set-Cookie", AuthUtils.instance().removeCookie())
        .build();
    }
  }

  @GET
  @Path("email/{email}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response email(@PathParam("email") String email) {
    String baseUrl = getBaseUrl();
    Admin admin = Admin.getByEmail(email.toLowerCase());
    if (admin != null) {
      new EmailUtils().emailTo(email, admin.getUUID(), baseUrl);
      return Response.ok().build();
    } else {
      Student student = Student.getByEmail(email.toLowerCase());
      if (student != null) {
        new EmailUtils().emailTo(email, student.getUUID(), baseUrl);
        return Response.ok().build();
      }
    }
    LOG.info("Authentication for {}, person not found", email);
    return Response.status(404).build();
  }

  @GET
  @Path("verify/{code}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response verify(@PathParam("code") Integer code) {
    String redirect = "/";

    VerificationCode vc = VerificationCode.getByCode(code);
    if (vc != null) {
      Person person = Person.getPerson(vc.getUUID());
      LOG.info(
        "Authenticated user {}, code {}, admin == {}",
        person.getName(),
        vc.getCode(),
        person.isAdmin()
      );
      NewCookie newcookie = authUtils.generateCookie(new User(person));
      if (person.isStudent()) {
        Student student = (Student) person;
        redirect += student.getSection().toLowerCase();
      }

      // Remove used verification codes
      vc.delete();

      // User authenticated and identified. Save the info.
      Map<String, String> target = new HashMap<String, String>();
      target.put("target", redirect);
      NewCookie cookie = authUtils.generateCookie(person);
      return Response
        .ok()
        .entity(target)
        .header("Set-Cookie", AuthUtils.sameSite(cookie))
        .build();
    }

    return Response.status(404).build();
  }

  @GET
  @Path("logout")
  @Produces(MediaType.APPLICATION_JSON)
  public Response logout(@CookieParam("wcfc.gs.token") Cookie cookie)
    throws URISyntaxException {
    User user = AuthUtils.instance().getUserFromCookie(cookie);
    if (user != null) {
      LOG.info("User {} / {} logged out.", user.getName(), user.getEmail());
    }
    return Response
      .seeOther(new URI("/"))
      .header("Set-Cookie", AuthUtils.instance().removeCookie())
      .build();
  }

  @GET
  @Path("freespace")
  @Produces(MediaType.APPLICATION_JSON)
  public Response freespace() {
    File fs = new File("tmp");
    @SuppressWarnings("serial")
    Map<String, Long> myMap = new HashMap<String, Long>() {
      {
        put("space", fs.getFreeSpace());
      }
    };

    return Response.ok().entity(myMap).build();
  }

  @POST
  @Path("upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public Response upload(
    @FormDataParam("label") String label,
    @FormDataParam("lesson") Integer lesson,
    @FormDataParam("required") Boolean required,
    @FormDataParam("section") String section,
    @FormDataParam("file") InputStream uploadedInputStream,
    @FormDataParam("file") FormDataContentDisposition fileDetails
  ) throws IOException, CsvException, ParseException {
    LOG.info("Starting upload of file ....");
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
    String fileType = getFileTypeByTika(targetFile);
    LOG.info("File type : {}", fileType);
    if (fileType.equals("application/pdf")) {
      String newname = gs_root + "/" + path;
      File newfile = new File(newname);
      FileUtils.moveFile(targetFile, newfile);
      if (newfile.exists()) {
        LOG.info("Creating : {}", newname);

        Index index = getSectionIndex(section);
        index.getChildren().add(new Index(path, label, lesson, required));
        writeSectionIndex(section, index);
        return Response.ok().build();
      } else {
        LOG.error("Renaming of temp upload file failed : {}", path);
        return Response.status(500).build();
      }
    } else {
      LOG.info("File was not a PDF, rejected --- type '{}'", fileType);
      targetFile.delete();
      return Response.status(400).build();
    }
  }

  @POST
  @Path("addAdmin")
  @Produces(MediaType.APPLICATION_JSON)
  public Response addAdmin(Map<String, String> request) {
    String name = request.get("name");
    String email = request.get("email");

    List<Role> roles = new ArrayList<Role>();
    roles.add(Role.ADMIN);

    Admin admin = Admin.getByEmail(email);
    if (admin == null) {
      admin = new Admin(email, name, roles);
      admin.save();
      LOG.info("Admin {}/{} added to system", name, email);
    } else {
      LOG.info("Admin {} already exists", email);
    }

    return Response.ok().build();
  }

  @GET
  @Path("students/{section}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response students(@PathParam("section") String section) {
    List<Student> students = Student.getAllForSection(section.toUpperCase());

    return Response.ok().entity(students).build();
  }

  @DELETE
  @Path("student/{email}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteStudent(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @PathParam("email") String email
  ) {
    User user = AuthUtils.instance().getUserFromCookie(cookie);
    if (user != null && user.isAdmin() == true) {
      Student student = Student.getByEmail(email);
      if (student != null) {
        student.delete();
        return Response.ok().build();
      } else {
        return Response.status(404).build();
      }
    } else {
      return Response.status(401).build();
    }
  }

  @DELETE
  @Path("students/{section}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteSection(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @PathParam("section") String section
  ) {
    User user = AuthUtils.instance().getUserFromCookie(cookie);
    if (user != null && user.isAdmin() == true) {
      List<Student> students = Student.getAllForSection(section.toUpperCase());
      for (Integer i = 0; i < students.size(); i++) {
        students.get(i).delete();
      }
      return Response.ok().build();
    } else {
      return Response.status(401).build();
    }
  }

  @POST
  @Path("addStudent")
  @Produces(MediaType.APPLICATION_JSON)
  public Response addStudent(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    Map<String, String> request
  ) {
    User user = AuthUtils.instance().getUserFromCookie(cookie);
    if (user != null && user.isAdmin() == true) {
      String section = request.get("section");
      String name = request.get("name").toLowerCase();
      String email = request.get("email").toLowerCase();

      Admin admin = Admin.getByEmail(email);
      if (admin == null) {
        Student student = Student.getByEmail(email);
        if (student == null) {
          student = new Student(section, name, email);
          student.save();
          LOG.info("Student {} added to {} section", email, section);
        } else {
          if (!student.getSection().equals(section)) {
            student.setSection(section);
            student.save();
            LOG.info("Student {} updated to be in {} section", email, section);
          } else {
            LOG.info("Student {} already exists", email);
          }
        }
      } else {
        Map<String, String> reply = Map.of(
          "message",
          "Email already registered as an admin. Don't do that again George."
        );
        return Response.status(409).entity(reply).build();
      }

      return Response.ok().build();
    } else {
      return Response.status(401).build();
    }
  }

  @POST
  @Path("uploadStudents")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public Response uploadStudents(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @FormDataParam("section") String section,
    @FormDataParam("file") InputStream uploadedInputStream,
    @FormDataParam("file") FormDataContentDisposition fileDetails
  ) throws IOException, CsvException, ParseException {
    User user = AuthUtils.instance().getUserFromCookie(cookie);
    if (user != null && user.isAdmin() == true) {
      // Normalize the class/section name
      section = section.toUpperCase();

      LOG.info("Starting upload of student file for {}....", section);
      UUID uuid = UUID.randomUUID();
      File targetFile = new File("/tmp/" + uuid.toString() + ".tmp");
      OutputStream outStream = new FileOutputStream(targetFile);

      byte[] buffer = new byte[8 * 1024];
      int bytesRead;
      while ((bytesRead = uploadedInputStream.read(buffer)) != -1) {
        outStream.write(buffer, 0, bytesRead);
      }
      uploadedInputStream.close();
      outStream.close();

      CSVParser parser = new CSVParserBuilder()
        .withSeparator(',')
        .withIgnoreQuotations(false)
        .build();

      CSVReader csvReader = new CSVReaderBuilder(new FileReader(targetFile))
        .withCSVParser(parser)
        .withSkipLines(2)
        .build();
      List<String[]> entries = csvReader.readAll();

      // Create the students
      Iterator<String[]> it = entries.iterator();
      while (it.hasNext()) {
        String[] line = it.next();

        String email = line[15];
        Student student = Student.getByEmail(email);

        if (student == null) {
          // Build the student name from first/middle/last fields
          StringBuilder sb = new StringBuilder();
          sb.append(line[7]).append(" ");
          if (!line[8].isEmpty()) sb.append(line[8]).append(" ");
          sb.append(line[9]);
          String name = sb.toString().trim();

          student = new Student(section, name, email.toLowerCase());
          student.save();
          LOG.info("Created student : {}", student);
        } else {
          if (!student.getSection().equals(section)) {
            student.setSection(section);
            student.save();
            LOG.info("Updated student : {}", student);
          } else {
            LOG.info("Student {} already exists", email);
          }
        }
      }

      return Response.ok().build();
    } else {
      return Response.status(401).build();
    }
  }

  public static String getFileTypeByTika(File file) {
    final Tika tika = new Tika();
    String fileTypeDefault = "";
    try {
      fileTypeDefault = tika.detect(file);
    } catch (IOException e) {
      LOG.error("Error while detecting file type from File");
      LOG.error("*Error message: {}", e.getMessage());
      e.printStackTrace();
    }
    return fileTypeDefault;
  }

  @POST
  @Path("update")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public Response update(
    @CookieParam("wcfc.gs.token") Cookie cookie,
    @FormDataParam("label") String label,
    @FormDataParam("lesson") Integer lesson,
    @FormDataParam("path") String path,
    @FormDataParam("required") Boolean required,
    @FormDataParam("section") String section
  ) throws IOException, CsvException, ParseException {
    User user = AuthUtils.instance().getUserFromCookie(cookie);
    if (user != null && user.isAdmin() == true) {
      boolean found = false;

      Index index = getSectionIndex(section);
      for (Index child : index.getChildren()) {
        if (child.getPath().equals(path)) {
          LOG.error("Modifying entry : {}", path);

          child.setLesson(lesson);
          child.setLabel(label);
          child.setRequired(required);

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
    } else {
      return Response.status(401).build();
    }
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
      try {
        InputStream gitPropsStream =
          GsResource.class.getClassLoader().getResourceAsStream("git.properties");
        if (gitPropsStream != null) {
          Properties gitProps = new Properties();
          gitProps.load(gitPropsStream);

          version.put("git.build.version", gitProps.getProperty("git.build.version"));
          version.put("git.commit.id", gitProps.getProperty("git.commit.id"));
          version.put("git.branch", gitProps.getProperty("git.branch"));
          version.put("git.build.time", gitProps.getProperty("git.build.time"));
          version.put(
            "git.commit.user.name",
            gitProps.getProperty("git.commit.user.name")
          );
          version.put(
            "git.commit.id.describe",
            gitProps.getProperty("git.commit.id.describe")
          );

          return version;
        }
      } catch (IOException e1) {
        LOG.info("IOException during git.properties retrieval : {}", e1);
      }
      return null;
    }
  }
}
