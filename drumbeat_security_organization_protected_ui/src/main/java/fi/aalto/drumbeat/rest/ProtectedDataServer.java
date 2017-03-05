package fi.aalto.drumbeat.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.sun.jersey.api.view.Viewable;

@Path("/data")
public class ProtectedDataServer {

	@Path("/hello")
	@GET
	public Viewable  getHello( @Context HttpServletRequest request, @Context HttpServletResponse response) {
		String message = "Hello World";
		request.setAttribute("message", "value");
		return new Viewable("/jsps/hello.jsp", null);
	}
	
	@Path("/test2")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String isSecure2(@Context SecurityContext sc) {
		return "{\"name\":\"" + sc.getUserPrincipal().getName() + " \"}";
	}

}