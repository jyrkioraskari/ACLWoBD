package fi.aalto.drumbeat.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/server")
public class SecurityServer extends RESTfulAPI {

	@Path("/hello")
	@GET
	public String getHello() {
		
		return "Hello OK!";
	}
	
	@POST
	@Path("/hello")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTrackInJSON(ServiceQuery query) {

		String result = "ServiceQuery : " + query;
		return Response.status(201).entity(result).build();

	}
	
	
	@Override
	public void setBaseURI(UriInfo uriInfo) {
		super.setBaseURI(uriInfo);
	}
}