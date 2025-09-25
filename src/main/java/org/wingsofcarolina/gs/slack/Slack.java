package org.wingsofcarolina.gs.slack;

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.GsConfiguration;

public class Slack {

  private static final Logger LOG = LoggerFactory.getLogger(Slack.class);

  public static enum Channel {
    NOTIFY,
    CONTACT,
  }

  private static Slack instance = null;

  private String notificationUrl;
  private String contactUrl;
  private String slackApiBaseUrl;

  private GsConfiguration config;

  public Slack(GsConfiguration config) {
    this.config = config;
    this.slackApiBaseUrl = config.getSlackApiBaseUrl();

    // Build webhook URLs from the configuration
    if (
      config.getSlackNotify() != null &&
      !config.getSlackNotify().isEmpty() &&
      !"none".equals(config.getSlackNotify())
    ) {
      if (config.getSlackNotify().startsWith("https://")) {
        notificationUrl = config.getSlackNotify();
      } else {
        // Assume it's in the old format (T/B/X) and convert to webhook URL
        String[] tokenParts = config.getSlackNotify().split("/");
        if (tokenParts.length == 3) {
          notificationUrl =
            slackApiBaseUrl +
            "/services/" +
            tokenParts[0] +
            "/" +
            tokenParts[1] +
            "/" +
            tokenParts[2];
        } else {
          throw new RuntimeException("Bad Slack #notification token, shutting down!");
        }
      }
    } else {
      LOG.warn(
        "Slack notification channel is disabled (token set to 'none' or not configured)"
      );
      notificationUrl = null;
    }

    if (
      config.getSlackContact() != null &&
      !config.getSlackContact().isEmpty() &&
      !"none".equals(config.getSlackContact())
    ) {
      if (config.getSlackContact().startsWith("https://")) {
        contactUrl = config.getSlackContact();
      } else {
        // Assume it's in the old format (T/B/X) and convert to webhook URL
        String[] tokenParts = config.getSlackContact().split("/");
        if (tokenParts.length == 3) {
          contactUrl =
            slackApiBaseUrl +
            "/services/" +
            tokenParts[0] +
            "/" +
            tokenParts[1] +
            "/" +
            tokenParts[2];
        } else {
          throw new RuntimeException("Bad Slack #contact token, shutting down!");
        }
      }
    } else {
      LOG.warn(
        "Slack contact channel is disabled (token set to 'none' or not configured)"
      );
      contactUrl = null;
    }

    Slack.instance = this;

    LOG.info("Slack service initialized with API base URL: {}", slackApiBaseUrl);
  }

  public static Slack instance() {
    if (instance == null) {
      throw new RuntimeException(
        "Slack API communications object has not been initialized."
      );
    }
    return Slack.instance;
  }

  public void sendString(Channel channel, String msg) {
    if (msg != null) {
      LOG.info("{}", msg);
      if (config.getMode().equals("PROD")) {
        sendMessage(channel, msg);
      }
    }
  }

  public boolean sendMessage(Channel channel, String msg) {
    if (msg != null) {
      LOG.info("Sending : {}", msg);
      if (config.getMode().equals("PROD")) {
        String url = getWebhookUrl(channel);
        if (url == null) {
          LOG.info("Slack channel {} is disabled, skipping message: {}", channel, msg);
          return false;
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        String json = "{\"text\":\"GROUNDSCHOOL: " + escapeJson(msg) + "\"}";
        HttpEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        try {
          CloseableHttpResponse response = httpclient.execute(httpPost);
          if (response.getCode() != 200) {
            LOG.error(
              "Failed to successfully send message to Slack: {} {}",
              response.getCode(),
              response.getReasonPhrase()
            );
            return false;
          }
          return true;
        } catch (IOException e) {
          LOG.error("Error sending message to Slack", e);
          return false;
        }
      }
    }
    return false;
  }

  // For backward compatibility with the old MessageRequest API
  public boolean sendMessage(Channel channel, Object messageRequest) {
    // For now, just convert to string - this maintains compatibility
    // but loses the rich formatting. Could be enhanced later if needed.
    if (messageRequest != null) {
      return sendMessage(channel, messageRequest.toString());
    }
    return false;
  }

  private String getWebhookUrl(Channel channel) {
    // Select the desired channel.
    switch (channel) {
      case NOTIFY:
        return notificationUrl;
      case CONTACT:
        return contactUrl;
      default:
        return notificationUrl;
    }
  }

  private String escapeJson(String text) {
    if (text == null) return "";
    return text
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\n", "\\n")
      .replace("\r", "\\r")
      .replace("\t", "\\t");
  }
}
