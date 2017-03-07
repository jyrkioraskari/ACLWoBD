package fi.aalto.drumbeat.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import fi.aalto.drumbeat.DataServer;

@Path("/server")
public class SecurityServer extends RESTfulAPI {

	@Path("/hello")
	@GET
	public String getHello() {
		
		return "Hello OK!";
	}
	
	@POST
	@Path("/query")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceResponce createTrackInJSON(ServiceQuery query) {
		DataServer ds=DataServer.getDataServer(query.requestURL);
		String roles = ds.connect(query.alt_name, query.requestURL).stream()
			     .collect(Collectors.joining(","));
		ServiceResponce response=new ServiceResponce();
		response.setRoles(roles);
		response.setStatus("OK");
		return response;
	}
	
	
	@Override
	public void setBaseURI(UriInfo uriInfo) {
		super.setBaseURI(uriInfo);
	}
}