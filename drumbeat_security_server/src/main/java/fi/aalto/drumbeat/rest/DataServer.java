package fi.aalto.drumbeat.rest;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
 

@Path("data")
public class DataServer{
	
	@GET
	@Path("/setname/{id}.{name}")
    public Response settName(@PathParam("id") String id,@PathParam("name") String name) {
		
		return Response.status(200).entity("Just OK now").build();
    }
	
	@GET
	@Path("")
    public Response getAll() {
		
		return Response.status(200).entity("").build();
    }
	
	
	@POST
	@Path("/checkClaim")
    public Response checkClaim(@PathParam("id") String id,@PathParam("name") String name) {
		
		return Response.status(200).entity("Just OK now").build();
    }
}