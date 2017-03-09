package fi.aalto.drumbeat;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;

public class DrumbeatApplication extends ResourceConfig {
	public DrumbeatApplication() {
		packages("fi.aalto.drumbeat.rest");
		/*
		property("com.sun.jersey.config.property.JSPTemplatesBasePath", "/WEB-INF/jsp");
		property("com.sun.jersey.config.property.WebPageContentRegex", "/(resources|(WEB-INF/jsp))/.*");
		register( JacksonFeature.class );
		packages(this.getClass().getPackage().getName());
		*/

        register(MvcFeature.class);
	}

	
}