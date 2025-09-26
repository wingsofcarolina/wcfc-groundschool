package org.wingsofcarolina.gs.email;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.GsConfiguration;
import org.wingsofcarolina.gs.domain.VerificationCode;

public class EmailUtils {

  // This address must be configured in Gmail API settings
  static final String FROM = "webmaster@wingsofcarolina.org";

  // This is the server to which we need to direct the verification
  static String SERVER = null;

  // Root of all file-system stored data
  private static String gs_root;

  // The subject line for the email.
  static final String SUBJECT = "WCFC Ground School Login";
  static final String SUBJECT_CONTACT = "WCFC Ground School Contact Request";

  private ObjectMapper mapper;
  private static GmailService gmailService;

  // The HTML body for the email.
  static final String HTMLBODY =
    "<html><div class=body>" +
    "<img src=https://groundschool.wingsofcarolina.org/WCFC-logo.jpg>" +
    "<div class=title>WCFC Groundschool Login</div>" +
    "<p>This email contains a verification code you can use to log into the WCFC Groundschool server. " +
    "This code is good for roughly 2 hours after which you will need to request another verification code. Once " +
    "your login is verified a token will be stored in your browser and subsequent attempts " +
    "to access the server will NOT require logging in again, as long as you use the same " +
    "system/browser and don't clear browser data. </p>" +
    "<p>Enter the following code into the login verification input :</p>" +
    "<div class=code>CODE</div>" +
    "<p>Thank you for joining the WCFC ground school. Good luck with your training!</p>" +
    "<div class=signature>-- WCFC Groundschool Server Administration</div>" +
    "</div></html>" +
    "<style>p{width:70%;}" +
    ".body{margin-top:30px;margin-left:30px;}" +
    ".title{font-size:1.2em;font-weight:bold;}" +
    ".code{text-decoration: none;margin:30px;font-size:36pt;font-family:Verdana}" +
    ".signature{margin-left:30px;margin-bottom:30px;}</style>";

  // The email body for recipients with non-HTML email clients.
  static final String TEXTBODY =
    "WCFC Ground School Login\n" +
    "This email contains a verification code you can use to log into the WCFC Groundschool server.\n" +
    "This code is good for roughly 2 hours after which you will need to request another verification\n" +
    "code. Once your login is verified a token will be stored in your browser and subsequent attempts\n" +
    "to access the server will NOT require logging in again, as long as you use the same\n" +
    "system/browser and don't clear browser data.\n\n" +
    "        CODE\n\n" +
    "Thank you for joining the WCFC ground school. Good luck with your training!\n\n" +
    "-- WCFC Ground School Server Administration";

  private static final Logger LOG = LoggerFactory.getLogger(EmailUtils.class);

  /**
   * Determines the server URL to use for email links.
   * Priority: 1) provided baseUrl, 2) configured SERVER, 3) fallback based on mode
   */
  private String determineServerUrl(String baseUrl) {
    if (baseUrl != null && !baseUrl.trim().isEmpty()) {
      return baseUrl.trim();
    }

    if (SERVER != null && !SERVER.trim().isEmpty()) {
      return SERVER.trim();
    }

    // Fallback to configuration-based URL determination
    GsConfiguration config = GsConfiguration.instance();
    if (config != null) {
      return config.getGs();
    }

    return null;
  }

  public static void initialize(String gs_root, String server) {
    EmailUtils.gs_root = gs_root;
    EmailUtils.SERVER = server;

    // Initialize Gmail service
    try {
      GsConfiguration config = GsConfiguration.instance();
      String serviceAccountKey = System.getenv("GMAIL_SERVICE_ACCOUNT_KEY");
      String impersonateUser = config.getGmailImpersonateUser();

      if (
        serviceAccountKey != null &&
        !serviceAccountKey.trim().isEmpty() &&
        impersonateUser != null &&
        !impersonateUser.trim().isEmpty()
      ) {
        String gmailApiBaseUrl = config.getGmailApiBaseUrl();
        gmailService =
          new GmailService(serviceAccountKey, impersonateUser, gmailApiBaseUrl);
        LOG.info("Gmail service initialized successfully");
      } else {
        LOG.warn(
          "Gmail service not initialized - missing configuration. " +
          "Set GMAIL_SERVICE_ACCOUNT_KEY and GMAIL_IMPERSONATE_USER environment variables."
        );
      }
    } catch (IOException | GeneralSecurityException e) {
      LOG.error("Failed to initialize Gmail service: {}", e.getMessage(), e);
      gmailService = null;
    }
  }

  public void emailTo(String email, String uuid) {
    emailTo(email, uuid, null);
  }

  public void emailTo(String email, String uuid, String baseUrl) {
    String serverUrl = determineServerUrl(baseUrl);

    if (serverUrl != null && gmailService != null) {
      Integer code = VerificationCode.makeEntry(uuid).getCode();

      String htmlBody = HTMLBODY
        .replace("SERVER", serverUrl)
        .replace("UUID", uuid)
        .replace("EMAIL", email)
        .replace("CODE", code.toString());
      String textBody = TEXTBODY
        .replace("SERVER", serverUrl)
        .replace("UUID", uuid)
        .replace("EMAIL", email)
        .replace("CODE", code.toString());

      try {
        gmailService.sendEmail(email, SUBJECT, textBody, htmlBody);
        LOG.info("Email sent to {} with id {} and code {}", email, uuid, code);
      } catch (MessagingException | IOException ex) {
        LOG.error(
          "The email was not sent to {}. Error message: {}",
          email,
          ex.getMessage(),
          ex
        );
      }
    } else {
      if (serverUrl == null) {
        LOG.warn("Email not sent - SERVER not configured and no base URL provided");
      }
      if (gmailService == null) {
        LOG.warn("Email not sent - Gmail service not initialized");
      }
    }
  }

  // The HTML body for the email.
  static final String HTMLBODY_CONTACT =
    "<html><div class=body>" +
    "<img src=https://groundschool.wingsofcarolina.org/WCFC-logo.jpg>" +
    "<div class=title>WCFC Groundschool Contact Request</div>" +
    "<p>This email contains a message sent via the 'Contact' page from the WCFC Groundschool server. " +
    "<div class=user>" +
    "<div class=label>Name</div><div class=detail>NAME</div>" +
    "</div>" +
    "<div class=user>" +
    "<div class=label>Phone</div><div class=detail>PHONE</div>" +
    "</div>" +
    "<div class=user>" +
    "<div class=label>Email</div><div class=detail><a href=\"mailto:EMAIL\">EMAIL</a></div>" +
    "</div>" +
    "<div class=\"label margin\">Message</div>" +
    "<div class=message>" +
    "MSG" +
    "</div>" +
    "<div class=signature>-- WCFC Groundschool Server Contact Page</div>" +
    "</div></html>" +
    "<style>p{width:70%;}" +
    "a:link {" +
    "  text-decoration: none;" +
    "} " +
    ".body{margin-top:30px;margin-left:30px;}" +
    ".title{font-size:1.2em;font-weight:bold;}" +
    ".code{text-decoration: none;margin:30px;font-size:36pt;font-family:Verdana}" +
    ".signature{margin-left:30px;margin-bottom:30px;}" +
    ".user{font-size:1.0em;margin:2px;}" +
    ".margin{margin-top:10px;margin-bottom:10px;}" +
    ".label{display:inline-block;color:#3248a8;width:75px;pad-right:10px;}" +
    ".detail{display:inline-block;}" +
    ".message {" +
    "    font-family: monospace;" +
    "    font-size:12pt;" +
    "    white-space: pre;" +
    "    margin-bottom:20px;" +
    "}</style>";

  // The email body for recipients with non-HTML email clients.
  static final String TEXTBODY_CONTACT =
    "WCFC Ground School Contact Request\n" +
    "This email contains a message sent via the 'Contact' page from the WCFC Groundschool server.\n" +
    "\n" +
    "Name  : NAME\n" +
    "Phone : PHONE\n" +
    "Email : EMAIL\n" +
    "\n" +
    "Message\n" +
    "\n" +
    "MSG\n" +
    "\n" +
    "-- WCFC Groundschool Server Administration\n";

  public void emailInstructors(String name, String phone, String email, String message) {
    emailInstructors(name, phone, email, message, null);
  }

  public void emailInstructors(
    String name,
    String phone,
    String email,
    String message,
    String baseUrl
  ) {
    String serverUrl = determineServerUrl(baseUrl);

    if (serverUrl != null && gmailService != null) {
      List<String> instructors = null;

      mapper = new ObjectMapper();

      try {
        String json = new String(
          Files.readAllBytes(Paths.get(gs_root + "/instructors.json"))
        );

        instructors = mapper.readValue(json, new TypeReference<List<String>>() {});
      } catch (IOException e) {
        LOG.error("Failed to read instructors.json", e);
        return;
      }

      String htmlBody = HTMLBODY_CONTACT
        .replace("SERVER", serverUrl)
        .replace("NAME", name)
        .replace("EMAIL", email)
        .replace("PHONE", phone)
        .replace("MSG", message);
      String textBody = TEXTBODY_CONTACT
        .replace("SERVER", serverUrl)
        .replace("NAME", name)
        .replace("EMAIL", email)
        .replace("PHONE", phone)
        .replace("MSG", message);

      for (String instructor : instructors) {
        try {
          gmailService.sendEmail(instructor, SUBJECT_CONTACT, textBody, htmlBody);
          LOG.info(
            "Email sent to {} for student {} at address {}",
            instructor,
            name,
            email
          );
        } catch (MessagingException | IOException ex) {
          LOG.error(
            "The email was not sent to {}. Error message: {}",
            instructor,
            ex.getMessage(),
            ex
          );
        }
      }
    } else {
      if (serverUrl == null) {
        LOG.warn("Email not sent - SERVER not configured and no base URL provided");
      }
      if (gmailService == null) {
        LOG.warn("Email not sent - Gmail service not initialized");
      }
    }
  }
}
