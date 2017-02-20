package fi.aalto.drumbeat.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.RDFConstants;

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
	public Response checkUser(String msg) {		
		Model input_model = parseInput(msg);
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		
		ResIterator iter = input_model.listSubjectsWithProperty(rdf.property.hasTimeStamp());
		Resource query = null;
		if (iter.hasNext())
			query = iter.next();
		else
			return Response.status(500).entity("no queries").build();
		RDFNode ts=query.getProperty(rdf.property.hasTimeStamp()).getObject();
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, rdf.Response());
		response.addLiteral(rdf.property.hasTimeStamp(), ts);

		return Response.status(200).entity(writeModel(output_model)).build();
	}

	@POST
	@Path("/check_webid")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response checkWebID(String msg) {
		Model input_model = parseInput(msg);
		Model output_model = ModelFactory.createDefaultModel();

		Resource r1 = output_model.getResource("http://test.fi/yksi");
		Resource rtype = output_model.getResource("http://test.fi/type");
		r1.addProperty(RDF.type, rtype);

		return Response.status(200).entity(writeModel(output_model)).build();
	}
	
	
	@POST
	@Path("/get_webid")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response getWebID(String msg) {
		Model input_model = parseInput(msg);
		Model output_model = ModelFactory.createDefaultModel();

		Resource r1 = output_model.getResource("http://test.fi/yksi");
		Resource rtype = output_model.getResource("http://test.fi/type");
		r1.addProperty(RDF.type, rtype);

		return Response.status(200).entity(writeModel(output_model)).build();
	}
}