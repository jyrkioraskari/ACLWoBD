package fi.aalto.drumbeat;

import org.glassfish.jersey.server.ResourceConfig;

public class DrumbeatApplication extends ResourceConfig {
	public DrumbeatApplication() {
		packages("fi.aalto.drumbeat.rest");
		property("com.sun.jersey.config.property.JSPTemplatesBasePath", "/WEB-INF/jsp");
		property("com.sun.jersey.config.property.WebPageContentRegex", "/(resources|(WEB-INF/jsp))/.*");
	}

}