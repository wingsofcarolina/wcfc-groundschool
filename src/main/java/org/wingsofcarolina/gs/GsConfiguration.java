package org.wingsofcarolina.gs;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class GsConfiguration extends Configuration {
	private static GsConfiguration instance = null;

	@JsonProperty String mode;
	@JsonProperty String slackTarget;

	public GsConfiguration() {
		GsConfiguration.instance = this;
	}

	public static GsConfiguration instance() {
		return instance;
	}

    public String getSlackTarget() {
        return slackTarget;
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
