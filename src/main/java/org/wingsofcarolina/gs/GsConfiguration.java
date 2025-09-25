package org.wingsofcarolina.gs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;

public class GsConfiguration extends Configuration {

  private static GsConfiguration instance = null;

  @JsonProperty
  String mode;

  @JsonProperty
  String gsServer;

  @JsonProperty
  String gsroot;

  @JsonProperty
  String mongodb;

  @JsonProperty
  Boolean auth;

  @JsonProperty
  Boolean mockAdmin;

  @JsonProperty
  String mockUser;

  @JsonProperty
  String slackNotify;

  @JsonProperty
  String slackContact;

  @JsonProperty
  String gmailImpersonateUser;

  @JsonProperty
  String gmailApiBaseUrl;

  @JsonProperty
  String slackApiBaseUrl;

  @JsonProperty
  String slackClientId;

  @JsonProperty
  String slackClientSecret;

  public GsConfiguration() {
    GsConfiguration.instance = this;
  }

  public static GsConfiguration instance() {
    return instance;
  }

  public String getMongodb() {
    return mongodb;
  }

  public String getGsServer() {
    return gsServer;
  }

  public Boolean getAuth() {
    return auth;
  }

  public Boolean getMockAdmin() {
    return mockAdmin;
  }

  public String getMockUser() {
    if (mockUser.equalsIgnoreCase("none")) {
      return null;
    } else {
      return mockUser;
    }
  }

  public String getGsroot() {
    return gsroot;
  }

  public String getSlackNotify() {
    return slackNotify;
  }

  public String getSlackContact() {
    return slackContact;
  }

  public String getMode() {
    return mode;
  }

  public String getGmailImpersonateUser() {
    return gmailImpersonateUser;
  }

  public String getGmailApiBaseUrl() {
    return gmailApiBaseUrl != null ? gmailApiBaseUrl : "https://www.googleapis.com";
  }

  public String getSlackApiBaseUrl() {
    return slackApiBaseUrl != null ? slackApiBaseUrl : "https://hooks.slack.com";
  }

  public String getSlackClientId() {
    return slackClientId;
  }

  public String getSlackClientSecret() {
    return slackClientSecret;
  }

  public String getGs() {
    if (getMode().equals("DEV")) {
      return "http://localhost:9323";
    } else {
      return "https://groundschool.wingsofcarolina.org";
    }
  }
}
