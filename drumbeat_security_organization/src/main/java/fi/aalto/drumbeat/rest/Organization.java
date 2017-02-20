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
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.security.OrganizationManager;
import fi.aalto.drumbeat.webid.WebIDCertificate;
import fi.aalto.drumbeat.webid.WebIDProfile;

@Path("/security")
public class Organization extends RESTfulAPI {

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
			return Response.status(500).entity("No queries").build();
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		

		RDFNode time_stamp=query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, rdf.Response());
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp);

		return Response.status(200).entity(writeModel(output_model)).build();
	}

	@POST
	@Path("/check_webid")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response checkWebID(@Context UriInfo uriInfo,String msg) {
		setBaseURI(uriInfo);
		Model input_model = parseInput(msg);
		Resource query=getQuery(input_model);
		if(query==null)
			return Response.status(500).entity("No queries").build();
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		

		RDFNode time_stamp=query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, rdf.Response());
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp);

		return Response.status(200).entity(writeModel(output_model)).build();
	}
	
	
	@POST
	@Path("/get_webid")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response getWebID(@Context UriInfo uriInfo,String msg) {
		setBaseURI(uriInfo);
		if(!this.organization.isPresent())
			return Response.status(500).entity("Initialization errors").build();
		Model input_model = parseInput(msg);
		Resource query=getQuery(input_model);
		if(query==null)
			return Response.status(500).entity("No queries").build();
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		

		RDFNode time_stamp=query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		RDFNode name=query.getProperty(RDFConstants.property_hasName).getObject();
		RDFNode public_key=query.getProperty(RDFConstants.property_hasPublicKey).getObject();
		WebIDCertificate wc=organization.get().getWebID(name.asLiteral().getLexicalForm(),public_key.asLiteral().getLexicalForm());
		
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, rdf.Response());
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp);

		response.addProperty(RDFConstants.property_hasWebID, output_model.getResource(wc.getWebid_uri().toString()));
		
		return Response.status(200).entity(writeModel(output_model)).build();
	}
	
	Optional<OrganizationManager> organization=  Optional.empty();
	
	@Override
	protected void setBaseURI(UriInfo uriInfo) {		
		super.setBaseURI(uriInfo);
		this.organization = Optional.of(new OrganizationManager(this.getBase_url()));
	}
}