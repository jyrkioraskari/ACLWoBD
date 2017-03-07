package fi.aalto.drumbeat;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

public class DrumbeatApplication extends ResourceConfig {
	public DrumbeatApplication() {
		packages("fi.aalto.drumbeat.rest");
		register(JspMvcFeature.class);
		property("jersey.config.server.mvc.templateBasePath.jsp", "/WEB-INF/jsp");

		
		
	}

}