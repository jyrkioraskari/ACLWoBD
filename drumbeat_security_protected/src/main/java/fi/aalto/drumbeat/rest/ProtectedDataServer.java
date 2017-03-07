package fi.aalto.drumbeat.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.sun.jersey.api.view.Viewable;

@Path("/")
public class ProtectedDataServer {

	@Path("/musiikkitalo")
	@GET
	public Viewable  getMusiikkitalo(@Context SecurityContext sc, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		request.setAttribute("name", sc.getUserPrincipal().getName() );
		
		if(sc.isUserInRole("CREATE"))
			request.setAttribute("CreatePermission", "TRUE");
		else
			request.setAttribute("CreatePermission", "FALSE");
		
		
		if(sc.isUserInRole("READ"))
			request.setAttribute("ReadPermission", "TRUE");
		else
			request.setAttribute("ReadPermission", "FALSE");
		
		
		if(sc.isUserInRole("UPDATE"))
			request.setAttribute("UpdatePermission", "TRUE");
		else
			request.setAttribute("UpdatePermission", "FALSE");

		
		if(sc.isUserInRole("DELETE"))
			request.setAttribute("DeletePermission", "TRUE");
		else
			request.setAttribute("DeletePermission", "FALSE");

		
		return new Viewable("/hello.jsp", null);
	}
	
	
	@Path("/sanomatalo")
	@GET
	public Viewable  getSanomatalo(@Context SecurityContext sc, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		request.setAttribute("name", sc.getUserPrincipal().getName() );
		
		if(sc.isUserInRole("CREATE"))
			request.setAttribute("CreatePermission", "TRUE");
		else
			request.setAttribute("CreatePermission", "FALSE");
		
		
		if(sc.isUserInRole("READ"))
			request.setAttribute("ReadPermission", "TRUE");
		else
			request.setAttribute("ReadPermission", "FALSE");
		
		
		if(sc.isUserInRole("UPDATE"))
			request.setAttribute("UpdatePermission", "TRUE");
		else
			request.setAttribute("UpdatePermission", "FALSE");

		
		if(sc.isUserInRole("DELETE"))
			request.setAttribute("DeletePermission", "TRUE");
		else
			request.setAttribute("DeletePermission", "FALSE");

		
		return new Viewable("/hello.jsp", null);
	}
}