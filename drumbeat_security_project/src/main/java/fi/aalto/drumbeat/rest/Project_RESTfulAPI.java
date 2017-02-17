package fi.aalto.drumbeat.rest;


import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;

 

@Path("security")
public class Project_RESTfulAPI extends RESTfulAPI
{
	
	
	@GET
	@Path("/hello")
    public Response hello() {
		
		return Response.status(200).entity("Hello...").build();
    }

	
	@GET
	@Path("/check_user/{path}.{webID}.{timestamp}")
    public Response check_user(@PathParam("path") String path,@PathParam("webID") String webID,@PathParam("timestamp") String timestamp) {
		return Response.status(200).entity(getJSON_LDContent()).build();
    }
	

}