package org.wingsofcarolina.gs;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.TimeZone;
import org.eclipse.jetty.ee10.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.common.RuntimeExceptionMapper;
import org.wingsofcarolina.gs.email.EmailUtils;
import org.wingsofcarolina.gs.healthcheck.MinimalHealthCheck;
import org.wingsofcarolina.gs.persistence.Persistence;
import org.wingsofcarolina.gs.resources.GsResource;
import org.wingsofcarolina.gs.resources.TestResource;
import org.wingsofcarolina.gs.services.HousekeepingService;
import org.wingsofcarolina.gs.slack.Slack;

public class GsService extends Application<GsConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(GsService.class);

  public static void main(String[] args) throws Exception {
    LOG.info("Starting : WCFC Groundschool Server");
    if (args.length < 2) {
      new GsService().run(new String[] { "server", "configuration.yml" });
    } else {
      new GsService().run(args);
    }
  }

  @Override
  public void initialize(Bootstrap<GsConfiguration> bootstrap) {
    // Enable environment variable substitution
    bootstrap.setConfigurationSourceProvider(
      new SubstitutingSourceProvider(
        bootstrap.getConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor(false)
      )
    );

    // bootstrap.addBundle(new AssetsBundle("/doc", "/doc", "index.html","html"));
    bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
    bootstrap.addBundle(new MultiPartBundle());
  }

  @Override
  public String getName() {
    return "wcfc-groundschool";
  }

  @Override
  public void run(GsConfiguration config, Environment env) throws Exception {
    env.jersey().setUrlPattern("/api/*");

    // Set up Slack communications
    new Slack(config);

    // Let those who care know we started
    Slack.instance().sendString(Slack.Channel.NOTIFY, "Groundschool server started.");

    // Get the startup date/time in GMT
    SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

    // Configure to allow CORS
    configureCors(env);

    // Set up the Persistence singleton
    new Persistence().initialize(config.getMongodb());

    // Make sure the email class knows the right server to reference
    EmailUtils.initialize(config.getGsroot(), config.getGsServer());

    // Set exception mappers
    if (config.getMode().contentEquals("PROD")) {
      env.jersey().register(new RuntimeExceptionMapper());
    }

    // Now set up the API
    env.jersey().register(new GsResource(config));
    //env.jersey().register(new TestResource(config));
    env.healthChecks().register("check", new MinimalHealthCheck());

    // Add shutdown hook to properly shutdown housekeeping service
    Runtime
      .getRuntime()
      .addShutdownHook(
        new Thread(() -> {
          LOG.info("Shutting down housekeeping service...");
          HousekeepingService.getInstance().shutdown();
        })
      );
  }

  private void configureCors(Environment environment) {
    final FilterRegistration.Dynamic cors = environment
      .servlets()
      .addFilter("CORS", CrossOriginFilter.class);

    // Configure CORS parameters
    cors.setInitParameter("allowedOrigins", "*");
    cors.setInitParameter(
      "allowedHeaders",
      "X-Requested-With,Content-Type,Accept,Origin,Authorization"
    );
    cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
    cors.setInitParameter("allowCredentials", "true");

    // Add URL mapping
    cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
  }
}
