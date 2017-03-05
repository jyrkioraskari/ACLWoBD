package fi.aalto.drumbeat.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/data")
public class ProtectedDataServer {

	@Path("/hello")
	@GET
	public String getHello() {
		
		return "Protected Hello OK!";
	}
	
	@Path("/test2")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String isSecure2(@Context SecurityContext sc) {
		return "{\"name\":\"" + sc.getUserPrincipal().getName() + " \"}";
	}

}