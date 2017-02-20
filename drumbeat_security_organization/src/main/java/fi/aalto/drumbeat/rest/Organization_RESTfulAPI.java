package fi.aalto.drumbeat.rest;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.security.Organization;

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
	public Response checkUser(@Context UriInfo uriInfo,String msg) {	
		setBaseURI(uriInfo);
		Model input_model = parseInput(msg);
		Resource query=getQuery(input_model);
		if(query==null)
			return Response.status(500).entity("no queries").build();
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		

		RDFNode ts=query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, rdf.Response());
		response.addLiteral(RDFConstants.property_hasTimeStamp, ts);

		return Response.status(200).entity(writeModel(output_model)).build();
	}

	@POST
	@Path("/check_webid")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response checkWebID(@Context UriInfo uriInfo,String msg) {
		setBaseURI(uriInfo);
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
	public Response getWebID(@Context UriInfo uriInfo,String msg) {
		setBaseURI(uriInfo);
		Model input_model = parseInput(msg);
		Model output_model = ModelFactory.createDefaultModel();

		
		
		return Response.status(200).entity(writeModel(output_model)).build();
	}
	
	Optional<Organization> organization=  Optional.empty();
	
	@Override
	public void setBaseURI(UriInfo uriInfo) {		
		super.setBaseURI(uriInfo);
		this.organization = Optional.of(new Organization(this.getBase_url()));
	}
}