package org.wingsofcarolina.gs;

import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.TimeZone;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.wingsofcarolina.gs.common.RuntimeExceptionMapper;
import org.wingsofcarolina.gs.common.Slack;
import org.wingsofcarolina.gs.healthcheck.MinimalHealthCheck;
import org.wingsofcarolina.gs.resources.GsResource;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundleConfiguration;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class GsService extends Application<GsConfiguration> {
	private static final Logger LOG = LoggerFactory.getLogger(GsService.class);
	
	public static void main(String[] args) throws Exception {
		LOG.info("Starting : WCFC Groundschool Server");
        if (args.length < 2) {
            new GsService().run(new String[]{"server", "configuration.ftl"});
        } else {
            new GsService().run(args);
        }
	}

	@Override
	public void initialize(Bootstrap<GsConfiguration> bootstrap) {
		// bootstrap.addBundle(new AssetsBundle("/doc", "/doc", "index.html","html"));
		bootstrap.addBundle(new TemplateConfigBundle(new TemplateConfigBundleConfiguration()));
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
		
		// Get the startup date/time in GMT
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

		// Configure to allow CORS
		configureCors(env);
		
		// Set exception mappers
		if (config.getMode().contentEquals("PROD")) {
			env.jersey().register(new RuntimeExceptionMapper());
		}
		
        // Set up Slack communications
        Slack slack = new Slack(config);

		// Now set up the API
		env.jersey().register(new GsResource(config));
		env.healthChecks().register("check", new MinimalHealthCheck());
	}
	
	private void configureCors(Environment environment) {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "/");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
