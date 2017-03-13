package fi.aalto.cs.drumbeat.rest;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.glassfish.jersey.server.mvc.Viewable;

import fi.aalto.cs.drumbeat.controllers.DataProtectionController;
import fi.aalto.cs.drumbeat.controllers.DrumbeatSecurityController;
import fi.aalto.cs.drumbeat.vo.DrumbeatSecurityQuery;
import fi.aalto.cs.drumbeat.vo.DrumbeatSecurityResponce;
import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.rest.RESTfulAPI;

@Path("/")
public class DrumbeatSecurityAPI extends RESTfulAPI {
	
	@GET
	@Produces({"text/html"})
	public  Viewable   getIndexJSP(@Context SecurityContext sc, @Context HttpServletRequest request, @Context HttpServletResponse response) {
			return new Viewable("/index.jsp", null);
	}

	
	@Path("/hello")
	@GET
	public String getHello() {
		
		return "Hello OK!";
	}
	

	@POST
	@Path("/hello")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response postHello(@Context UriInfo uriInfo, String msg) {
		setBaseURI(uriInfo);
		if (!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors")
					.build();
		Model input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();

		Model output_model = ModelFactory.createDefaultModel();

		RDFNode time_stamp = query.getProperty(RDFConstants.Message.hasTimeStamp).getObject();

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Message.SecurityResponse);
		response.addLiteral(RDFConstants.Message.hasTimeStamp, time_stamp.asLiteral().toString());
		response.addLiteral(RDFConstants.Message.hasMessage, "base was: " + getBase_url());

		response.addProperty(RDFConstants.Message.hasPermissionStatus, RDFConstants.Message.accepted);
		return Response.status(200).entity(writeModel(output_model)).build();
	}

	@Path("/list")
	@Produces({"text/html"})
	@GET
	
	public  Viewable   getListOfUsers(@Context SecurityContext sc, @Context HttpServletRequest request, @Context HttpServletResponse response) {
			return new Viewable("/list.jsp", null);
	}


	//TODO mitä tapahtuu, jos haetaan GETillä?
	@POST
	@Consumes("application/ld+json")	
	@Produces("application/ld+json")
	public Response checkPath(@Context UriInfo uriInfo, String msg) {
		setBaseURI(uriInfo);
		if (!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors")
					.build();
		Model input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();

		Model output_model = ModelFactory.createDefaultModel();

		RDFNode time_stamp = query.getProperty(RDFConstants.Message.hasTimeStamp).getObject();
		RDFNode webid_url = query.getProperty(RDFConstants.Message.hasWebID).getObject();
		RDFNode path = query.getProperty(RDFConstants.Authorization.hasRulePath).getObject();
		
		boolean result = organization.get().checkRDFPath(webid_url.toString(), path.asResource());

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Message.SecurityResponse);
		response.addLiteral(RDFConstants.Message.hasTimeStamp, time_stamp.asLiteral().toString());

		if(result)
			response.addProperty(RDFConstants.Message.hasPermissionStatus, RDFConstants.Message.accepted);
		else
			response.addProperty(RDFConstants.Message.hasPermissionStatus, RDFConstants.Message.denied);
		return Response.status(200).entity(writeModel(output_model)).build();

	}



	@POST
	@Path("/getWebIDProfile")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response getWebIDProfile(@Context UriInfo uriInfo, String msg) {
		setBaseURI(uriInfo);

		if (!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors")
					.build();
		Model input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();

		Model output_model = ModelFactory.createDefaultModel();

		RDFNode time_stamp = query.getProperty(RDFConstants.Message.hasTimeStamp).getObject();
		RDFNode webid_url = query.getProperty(RDFConstants.Message.hasWebID).getObject();
		Resource wp = organization.get().getWebIDProfile(webid_url.toString());
		if (wp == null)
			return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("No user").build();
		
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Message.SecurityResponse);

		/*RDFNode public_key=wp.getProperty(RDFConstants.property_hasPublicKey).getObject();
		response.addLiteral(RDFConstants.property_hasPublicKey, public_key.asLiteral().toString());*/

		response.addLiteral(RDFConstants.Message.hasTimeStamp, time_stamp.asLiteral().toString());
		return Response.status(200).entity(writeModel(output_model)).build();

	}
	// @formatter:off
/*
 * 
	@POST
	@Path("/registerWebID")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response registerWebID(@Context UriInfo uriInfo, String msg) {
		setBaseURI(uriInfo);

		if (!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors")
					.build();
		Model input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();

		Model output_model = ModelFactory.createDefaultModel();

		RDFNode time_stamp = query.getProperty(RDFConstants.Messages.hasTimeStamp).getObject();
		RDFNode webid = query.getProperty(RDFConstants.Messages.hasWebID).getObject();
		
		//TODO Exponent+modulus
		
		RDFNode public_key = query.getProperty(RDFConstants.property_hasPublicKey).getObject();
		Resource wc = organization.get().registerWebID(webid.toString(),
				public_key.asLiteral().getLexicalForm());


		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Messages.SecurityResponse);
		response.addLiteral(RDFConstants.Messages.hasTimeStamp, time_stamp.asLiteral().toString());

		response.addProperty(RDFConstants.Messages.hasWebID, output_model.getResource(wc.toString()));

		return Response.status(200).entity(writeModel(output_model)).build();
	}
	*/
	// @formatter:on

	Optional<DrumbeatSecurityController> organization = Optional.empty();

	@Override
	public void setBaseURI(UriInfo uriInfo) {
		super.setBaseURI(uriInfo);
		if (!this.organization.isPresent())
			this.organization = Optional.of(DrumbeatSecurityController.getOrganizationManager(getBase_url()));
	}
}