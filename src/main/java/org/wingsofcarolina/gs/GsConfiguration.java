package org.wingsofcarolina.gs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.knowm.dropwizard.sundial.SundialConfiguration;

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

  public GsConfiguration() {
    GsConfiguration.instance = this;
  }

  @Valid
  @NotNull
  public SundialConfiguration sundialConfiguration = new SundialConfiguration();

  @JsonProperty("sundial")
  public SundialConfiguration getSundialConfiguration() {
    return sundialConfiguration;
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

  public String getGs() {
    if (getMode().equals("DEV")) {
      return "http://localhost:9323";
    } else {
      return "https://groundschool.wingsofcarolina.org";
    }
  }
}
