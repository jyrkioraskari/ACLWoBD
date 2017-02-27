package fi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/data")
public class ProtectedDataServer {

	@Path("/hello")
	@GET
	public String getHello() {
		
		return "Protected Hello OK!";
	}
	

}