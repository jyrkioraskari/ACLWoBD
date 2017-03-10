package fi.aalto.drumbeat;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

public class DrumbeatApplication extends ResourceConfig {
	public DrumbeatApplication() {
		packages("fi.aalto.drumbeat.rest");
		register(LoggingFeature.class);
        register(JspMvcFeature.class);
        property("jersey.config.server.mvc.templateBasePath", "/WEB-INF/jsp");

        register(MvcFeature.class);
	}

	
}