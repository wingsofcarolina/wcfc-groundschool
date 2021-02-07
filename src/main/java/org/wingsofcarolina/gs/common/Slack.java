package org.wingsofcarolina.gs.common;

import java.util.List;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.palantir.roboslack.api.MessageRequest;
import com.palantir.roboslack.api.attachments.Attachment;
import com.palantir.roboslack.api.attachments.Attachment.Builder;
import com.palantir.roboslack.api.attachments.components.Color;
import com.palantir.roboslack.api.markdown.SlackMarkdown;
import com.palantir.roboslack.webhook.api.model.WebHookToken;
import com.palantir.roboslack.webhook.api.model.response.ResponseCode;
import com.palantir.roboslack.webhook.SlackWebHookService;

import org.wingsofcarolina.gs.GsConfiguration;

public class Slack {
	private static final Logger LOG = LoggerFactory.getLogger(Slack.class);
	
	private static Slack instance = null;

	// WCFC #quiz channel : REDACTED
	// Gs.co #notification channel : REDACTED
	// Gs.co #contact channel : TAYTPJJF5/B01LH95DHRT/lo9pphg7Rs80fmCHatAGmPMR
	WebHookToken token;

	private GsConfiguration config;
	
	public Slack(GsConfiguration config) {
		String[] tokenParts = config.getSlackTarget().split("/");
		if (tokenParts.length == 3) {
		token = WebHookToken.builder()
                .partT(tokenParts[0])
                .partB(tokenParts[1])
                .partX(tokenParts[2])
                .build();
		Slack.instance = this;
		this.config = config;
		} else {
			throw new RuntimeException("Bad Slack token, shutting down!");
		}
	}
	
	public static Slack instance() {
		if (instance == null) {
			throw new RuntimeException("Slack API communications object has not been initialized.");
		}
		return Slack.instance;
	}
	
	public void sendString(String msg) {
		if (msg != null) {
			LOG.debug("{}", msg);
			if (config.getMode().equals("PROD") ) {
				MessageRequest message = MessageRequest.builder()
		                .username("roboslack")
		                // SlackMarkdown string decoration is handled automatically in fields that require it,
		                // so this is valid:
		                .iconEmoji(SlackMarkdown.EMOJI.decorate("smile"))
		                .text(msg)
		                .build();
				ResponseCode response = SlackWebHookService.with(token)
	                    .sendMessage(message);
				if (response.name().equals("ok")) {
					LOG.error("Failed to send slack message : {}", response);
				}
			}
		}
	}

	public boolean sendMessage(MessageRequest msg) {
		if (msg != null) {
			LOG.info("Sending : {}", msg);
			if (config.getMode().equals("PROD") ) {
				ResponseCode response = SlackWebHookService.with(token).sendMessage(msg);
				if (response.name().equals("ok")) {
					LOG.error("Failed to send slack message : {}", response);
				} else {
					return true;
				}
			}
		}
		return false;
	}
}
