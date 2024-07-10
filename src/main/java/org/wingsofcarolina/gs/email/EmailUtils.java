package org.wingsofcarolina.gs.email;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.domain.VerificationCode;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmailUtils {

	// This address must be verified with Amazon SES.
	static final String FROM = "webmaster@wingsofcarolina.org";
	
	// This is the server to which we need to direct the verification
	static String SERVER = null;

	// Root of all file-system stored data
	private static String gs_root;

	// The subject line for the email.
	static final String SUBJECT = "WCFC Ground School Login";
	static final String SUBJECT_CONTACT = "WCFC Ground School Contact Request";
	
	private ObjectMapper mapper;

	// The HTML body for the email.
	static final String HTMLBODY = "<html><div class=body>"
			+ "<img src=https://groundschool.wingsofcarolina.org/WCFC-logo.jpg>"
			+ "<div class=title>WCFC Groundschool Login</div>"
			+ "<p>This email contains a verification code you can use to log into the WCFC Groundschool server. "
			+ "This code is good for roughly 2 hours after which you will need to request another verification code. Once "
			+ "your login is verified a token will be stored in your browser and subsequent attempts "
			+ "to access the server will NOT require logging in again, as long as you use the same "
			+ "system/browser and don't clear browser data. </p>"
			+ "<p>Enter the following code into the login verification input :</p>"
			+ "<div class=code>CODE</div>"
			+ "<p>Thank you for joining the WCFC ground school. Good luck with your training!</p>"
			+ "<div class=signature>-- WCFC Groundschool Server Administration</div>"
			+ "</div></html>"
			+ "<style>p{width:70%;}"
			+ ".body{margin-top:30px;margin-left:30px;}"
			+ ".title{font-size:1.2em;font-weight:bold;}"
			+ ".code{text-decoration: none;margin:30px;font-size:36pt;font-family:Verdana}"
			+ ".signature{margin-left:30px;margin-bottom:30px;}</style>";

	// The email body for recipients with non-HTML email clients.
	static final String TEXTBODY = "WCFC Ground School Login\n"
			+ "This email contains a verification code you can use to log into the WCFC Groundschool server.\n"
			+ "This code is good for roughly 2 hours after which you will need to request another verification\n"
			+ "code. Once your login is verified a token will be stored in your browser and subsequent attempts\n"
			+ "to access the server will NOT require logging in again, as long as you use the same\n"
			+ "system/browser and don't clear browser data.\n\n"
			+ "        CODE\n\n"
			+ "Thank you for joining the WCFC ground school. Good luck with your training!\n\n"
			+ "-- WCFC Ground School Server Administration";

	private static final Logger LOG = LoggerFactory.getLogger(EmailUtils.class);
	
	public static void initialize(String gs_root, String server) {
		EmailUtils.gs_root = gs_root;
		EmailUtils.SERVER = server;
	}
	
	public void emailTo(String email, String uuid) {
		if (SERVER != null) {
			Integer code = VerificationCode.makeEntry(uuid).getCode();
			
			String htmlBody = HTMLBODY.replace("SERVER", SERVER).replace("UUID", uuid).replace("EMAIL", email).replace("CODE", code.toString());
			String textBody = TEXTBODY.replace("SERVER", SERVER).replace("UUID", uuid).replace("EMAIL", email).replace("CODE", code.toString());
	
			try {
				AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
						// Replace US_WEST_2 with the AWS Region you're using for
						// Amazon SES.
						.withRegion(Regions.US_EAST_1).build();
				SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(email))
						.withMessage(new Message()
								.withBody(new Body()
										.withHtml(new Content().withCharset("UTF-8").withData(htmlBody))
										.withText(new Content().withCharset("UTF-8").withData(textBody)))
								.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
						.withSource(FROM);
				client.sendEmail(request);
				LOG.info("Email sent to {} with id {} and code {}", email, uuid, code);
			} catch (Exception ex) {
				LOG.info("The email was not sent. Error message: {}", ex.getMessage());
			}
		}
	}
	
	// The HTML body for the email.
	static final String HTMLBODY_CONTACT = "<html><div class=body>"
			+ "<img src=https://groundschool.wingsofcarolina.org/WCFC-logo.jpg>"
			+ "<div class=title>WCFC Groundschool Contact Request</div>"
			+ "<p>This email contains a message sent via the 'Contact' page from the WCFC Groundschool server. "
			+ "<div class=user>"
			+ "<div class=label>Name</div><div class=detail>NAME</div>"
			+ "</div>"
			+ "<div class=user>"
			+ "<div class=label>Phone</div><div class=detail>PHONE</div>"
			+ "</div>"
			+ "<div class=user>"
			+ "<div class=label>Email</div><div class=detail><a href=\"mailto:EMAIL\">EMAIL</a></div>"
			+ "</div>"
			+ "<div class=\"label margin\">Message</div>"
			+ "<div class=message>"
			+ "MSG"
			+ "</div>"
			+ "<div class=signature>-- WCFC Groundschool Server Contact Page</div>"
			+ "</div></html>"
			+ "<style>p{width:70%;}"
			+ "a:link {"
			+ "  text-decoration: none;"
			+ "} "
			+ ".body{margin-top:30px;margin-left:30px;}"
			+ ".title{font-size:1.2em;font-weight:bold;}"
			+ ".code{text-decoration: none;margin:30px;font-size:36pt;font-family:Verdana}"
			+ ".signature{margin-left:30px;margin-bottom:30px;}"
			+ ".user{font-size:1.0em;margin:2px;}"
			+ ".margin{margin-top:10px;margin-bottom:10px;}"
			+ ".label{display:inline-block;color:#3248a8;width:75px;pad-right:10px;}"
			+ ".detail{display:inline-block;}"
			+ ".message {"
			+ "    font-family: monospace;"
			+ "    font-size:12pt;"
			+ "    white-space: pre;"
			+ "    margin-bottom:20px;"
			+ "}</style>";

	// The email body for recipients with non-HTML email clients.
	static final String TEXTBODY_CONTACT = "WCFC Ground School Contact Request\n"
			+ "This email contains a message sent via the 'Contact' page from the WCFC Groundschool server.\n"
			+ "\n"
			+ "Name  : NAME\n"
			+ "Phone : PHONE\n"
			+ "Email : EMAIL\n"
			+ "\n"
			+ "Message\n"
			+ "\n"
			+ "MSG\n"
			+ "\n"
			+ "-- WCFC Groundschool Server Administration\n";

	public void emailInstructors(String name, String phone, String email, String message) {
		if (SERVER != null) {
			List<String>  instructors = null;
			
			mapper = new ObjectMapper();
			
			try {
				String json = new String(Files.readAllBytes(Paths.get(gs_root + "/instructors.json")));

				instructors = mapper.readValue(json, new TypeReference<List<String>>(){});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String htmlBody = HTMLBODY_CONTACT.replace("SERVER", SERVER).replace("NAME", name).replace("EMAIL", email).replace("PHONE", phone).replace("MSG", message);
			String textBody = TEXTBODY_CONTACT.replace("SERVER", SERVER).replace("NAME", name).replace("EMAIL", email).replace("PHONE", phone).replace("MSG", message);
	
			for (String instructor : instructors) {
				try {
					AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
							// Replace US_WEST_2 with the AWS Region you're using for
							// Amazon SES.
							.withRegion(Regions.US_EAST_1).build();
					SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(instructor))
							.withMessage(new Message()
									.withBody(new Body()
											.withHtml(new Content().withCharset("UTF-8").withData(htmlBody))
											.withText(new Content().withCharset("UTF-8").withData(textBody)))
									.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT_CONTACT)))
							.withSource(FROM);
					client.sendEmail(request);
					LOG.info("Email sent to {} for student {} at address {}", instructor, name, email);
				} catch (Exception ex) {
					LOG.info("The email was not sent. Error message: {}", ex.getMessage());
				}
			}
		}
	}
	
}