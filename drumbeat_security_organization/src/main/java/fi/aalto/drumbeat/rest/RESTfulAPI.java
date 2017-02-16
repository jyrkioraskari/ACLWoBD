package fi.aalto.drumbeat.rest;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

 

@Path("security")
public class RESTfulAPI{
	
	@GET
	@Path("/check_user/{path}.{webID}.{timestamp}")
    public Response check_user(@PathParam("path") String path,@PathParam("webID") String webID,@PathParam("timestamp") String timestamp) {
		return Response.status(200).entity("Just OK now").build();
    }
	

}