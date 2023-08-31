package org.wingsofcarolina.gs.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class EmailLogin {

	// This address must be verified with Amazon SES.
	static final String FROM = "webmaster@wingsofcarolina.org";
	
	// This is the server to which we need to direct the verification
	static String SERVER = null;

	// The subject line for the email.
	static final String SUBJECT = "WCFC Groundschool Login";

	// The HTML body for the email.
	static final String HTMLBODY = "<html><div class=body>"
			+ "<img src=https://groundschool.wingsofcarolina.org/WCFC-logo.jpg>"
			+ "<div class=title>WCFC Groundschool Login</div>"
			+ "<p>This email contains a URL you can use to log into the WCFC groundschool server. "
			+ "This URL is good for roughly 2 hours after which you will need to request another login. Once "
			+ "your login is verified a token will be stored in your browser and subsequent attempts "
			+ "to access the server will NOT require logging in again, as long as you use the same "
			+ "system/browser and don't clear browser data. </p>"
			+ "<p>This URL can be used only once. Invoking this URL will clear the verification code from\n"
			+ "the system's cache. If you need another URL you'll have to re-submit your email address\n"
			+ "and receive another verification email.</p>"			
			+ "<div class=link><a href=SERVER/api/verify/UUID/CODE>Log into the WCFC Groundschool server for EMAIL</a></div>"
			+ "<p>Thank you for joining the WCFC groundschool. Good luck with your training!</p>"
			+ "<div class=signature>-- WCFC Groundschool Server Administration</div>"
			+ "</div></html>"
			+ "<style>p{width:70%;}"
			+ ".body{margin-top:30px;margin-left:30px;}"
			+ ".title{font-size:1.2em;font-weight:bold;}"
			+ ".link{text-decoration: none;margin-left:30px;}"
			+ ".signature{margin-left:30px;}</style>";

	// The email body for recipients with non-HTML email clients.
	static final String TEXTBODY = "WCFC Groundschool Login\n"
			+ "This email contains a URL you can use to log into the WCFC groundschool server.\n"
			+ "This URL is good for roughly 2 hours after which you will need to request another login.\n"
			+ "Once your login is verified a token will be stored in your browser and subsequent attempts\n"
			+ "to access the server will NOT require logging in again, as long as you use the same\n"
			+ "system/browser and don't clear browser data.\n\n"
			+ "This URL can be used only once. Invoking this URL will clear the verification code from\n"
			+ "the system's cache. If you need another URL you'll have to re-submit your email address\n"
			+ "and receive another verification email.\n"
			+ "\nSERVER/api/verify/UUID/CODE\n\n"
			+ "Thank you for joining the WCFC groundschool. Good luck with your training!\n"
			+ "-- WCFC Groundschool Server Administration";

	private static final Logger LOG = LoggerFactory.getLogger(EmailLogin.class);
	
	public static void initialize(String server) {
		EmailLogin.SERVER = server;
	}
	
	public void emailTo(String email, String uuid) {
		if (SERVER != null) {
			Integer code = VerificationCodeCache.instance().getVerificationCode(uuid);
			
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
}