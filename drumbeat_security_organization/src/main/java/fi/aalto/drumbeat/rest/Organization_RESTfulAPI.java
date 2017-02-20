package fi.aalto.drumbeat.rest;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

@Path("/security")
public class Organization_RESTfulAPI extends RESTfulAPI {

	@Path("/hello")
	@GET
	public String getHello() {
		return "OK!";
	}

	@POST
	@Path("/check_user")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response check_user(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null,  "JSON-LD");

		StringWriter writer = new StringWriter();
		
		Model json_output_model = ModelFactory.createDefaultModel();

		Resource r1 = json_output_model.getResource("http://test.fi/yksi");
		Resource rtype = json_output_model.getResource("http://test.fi/type");
		r1.addProperty(RDF.type, rtype);
		json_output_model.write(writer, "JSON-LD");
		writer.flush();
		return Response.status(200).entity(writer.toString()).build();
	}

}