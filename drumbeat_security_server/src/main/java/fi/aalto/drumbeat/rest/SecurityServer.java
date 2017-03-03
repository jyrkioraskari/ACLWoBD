package fi.aalto.drumbeat.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;

@Path("/server")
public class SecurityServer extends RESTfulAPI {

	@Path("/hello")
	@GET
	public String getHello() {
		
		return "Hello OK!";
	}
	

	@Override
	public void setBaseURI(UriInfo uriInfo) {
		super.setBaseURI(uriInfo);
	}
}