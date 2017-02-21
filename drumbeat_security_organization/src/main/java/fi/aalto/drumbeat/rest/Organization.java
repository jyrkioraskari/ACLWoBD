package fi.aalto.drumbeat.rest;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
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
	@Path("/checkPath")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response checkPath(@Context UriInfo uriInfo,String msg) {	
		setBaseURI(uriInfo);
		if(!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors").build();
		Model input_model = parseInput(msg);
		Resource query=getQuery(input_model);
		if(query==null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		

		RDFNode time_stamp=query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		RDFNode webid_url=query.getProperty(RDFConstants.property_hasWebID).getObject();
		RDFNode path = query.getProperty(RDFConstants.property_hasRulePath).getObject();
		boolean  result=organization.get().checkRDFPath(webid_url.toString(),path.asResource());
		
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Response);
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp);

		Literal result_code = output_model.createTypedLiteral(new Boolean(result));			
		response.addLiteral(RDFConstants.property_status, result_code);
		return Response.status(200).entity(writeModel(output_model)).build();

	}

	@POST
	@Path("/getWebIDProfile")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response getWebIDProfile(@Context UriInfo uriInfo,String msg) {
		setBaseURI(uriInfo);
		if(!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors").build();
		Model input_model = parseInput(msg);
		Resource query=getQuery(input_model);
		if(query==null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		

		RDFNode time_stamp=query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		RDFNode webid_url=query.getProperty(RDFConstants.property_hasWebID).getObject();
		WebIDProfile  wp=organization.get().getWebIDProfile(webid_url.toString());
		if(wp==null)
			return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("No user").build();
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Response);
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp);
		response.addLiteral(RDFConstants.property_hasPublicKey, wp.getPublic_key());
		response.addLiteral(RDFConstants.property_hasName, wp.getName());

		return Response.status(200).entity(writeModel(output_model)).build();
	
	}
	
	
	@POST
	@Path("/getWebID")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response getWebID(@Context UriInfo uriInfo,String msg) {
		setBaseURI(uriInfo);
		if(!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors").build();
		Model input_model = parseInput(msg);
		Resource query=getQuery(input_model);
		if(query==null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFConstants rdf = new RDFConstants(output_model);
		

		RDFNode time_stamp=query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		RDFNode name=query.getProperty(RDFConstants.property_hasName).getObject();
		RDFNode public_key=query.getProperty(RDFConstants.property_hasPublicKey).getObject();
		WebIDCertificate wc=organization.get().getWebID(name.asLiteral().getLexicalForm(),public_key.asLiteral().getLexicalForm());
		
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Response);
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