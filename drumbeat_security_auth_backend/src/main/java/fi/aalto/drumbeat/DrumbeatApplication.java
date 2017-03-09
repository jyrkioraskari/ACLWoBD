package fi.aalto.drumbeat;

import org.glassfish.jersey.server.ResourceConfig;

public class DrumbeatApplication extends ResourceConfig {
	public DrumbeatApplication() {
		packages("fi.aalto.drumbeat.rest");
		
	}

}