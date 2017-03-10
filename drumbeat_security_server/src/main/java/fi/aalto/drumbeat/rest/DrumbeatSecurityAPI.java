package fi.aalto.drumbeat.rest;

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

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.controllers.DataProtectionController;
import fi.aalto.drumbeat.controllers.DrumbeatSecurityController;
import fi.aalto.drumbeat.vo.DrumbeatSecurityQuery;
import fi.aalto.drumbeat.vo.DrumbeatSecurityResponce;

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

		RDFNode time_stamp = query.getProperty(RDFConstants.property_hasTimeStamp).getObject();

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Response);
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp.asLiteral().toString());
		response.addLiteral(RDFConstants.property_information, "base was: " + getBase_url());

		response.addLiteral(RDFConstants.property_status, "HELLO");
		return Response.status(200).entity(writeModel(output_model)).build();
	}

	@Path("/list")
	@Produces({"text/html"})
	@GET
	
	public  Viewable   getListOfUsers(@Context SecurityContext sc, @Context HttpServletRequest request, @Context HttpServletResponse response) {
			return new Viewable("/list.jsp", null);
	}


	
	//TODD Is a local tcp connection any risk? Can HTTPS help locally?
	@POST
	@Path("/authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public DrumbeatSecurityResponce createTrackInJSON(DrumbeatSecurityQuery query) {
		DataProtectionController ds=DataProtectionController.getDataServer(query.requestURL);
		String roles = ds.autenticate(query.alt_name, query.requestURL).stream()
			     .collect(Collectors.joining(","));
		DrumbeatSecurityResponce response=new DrumbeatSecurityResponce();
		response.setRoles(roles);
		response.setStatus("OK");
		return response;
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

		RDFNode time_stamp = query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		RDFNode webid_url = query.getProperty(RDFConstants.property_hasWebID).getObject();
		RDFNode path = query.getProperty(RDFConstants.property_hasRulePath).getObject();
		
		boolean result = organization.get().checkRDFPath(webid_url.toString(), path.asResource());

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Response);
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp.asLiteral().toString());

		Literal result_code = output_model.createTypedLiteral(new Boolean(result));
		response.addLiteral(RDFConstants.property_status, result_code);
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

		RDFNode time_stamp = query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		RDFNode webid_url = query.getProperty(RDFConstants.property_hasWebID).getObject();
		Resource wp = organization.get().getWebIDProfile(webid_url.toString());
		if (wp == null)
			return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("No user").build();
		
		RDFNode public_key=wp.getProperty(RDFConstants.property_hasPublicKey).getObject();
		
		
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Response);
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp.asLiteral().toString());
		response.addLiteral(RDFConstants.property_hasPublicKey, public_key.asLiteral().toString());

		return Response.status(200).entity(writeModel(output_model)).build();

	}

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

		RDFNode time_stamp = query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		RDFNode webid = query.getProperty(RDFConstants.property_hasWebID).getObject();
		//TODO Exponent+modulus
		RDFNode public_key = query.getProperty(RDFConstants.property_hasPublicKey).getObject();
		Resource wc = organization.get().registerWebID(webid.toString(),
				public_key.asLiteral().getLexicalForm());

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.Response);
		response.addLiteral(RDFConstants.property_hasTimeStamp, time_stamp.asLiteral().toString());

		response.addProperty(RDFConstants.property_hasWebID, output_model.getResource(wc.toString()));

		return Response.status(200).entity(writeModel(output_model)).build();
	}

	Optional<DrumbeatSecurityController> organization = Optional.empty();

	@Override
	public void setBaseURI(UriInfo uriInfo) {
		super.setBaseURI(uriInfo);
		if (!this.organization.isPresent())
			this.organization = Optional.of(DrumbeatSecurityController.getOrganizationManager(getBase_url()));
	}
}