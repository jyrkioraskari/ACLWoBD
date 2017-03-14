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

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
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
import fi.aalto.drumbeat.RDFOntology;
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
		OntModel input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();

		OntModel output_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		RDFNode time_stamp = query.getProperty(RDFOntology.Message.hasTimeStamp).getObject();

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFOntology.Message.SecurityResponse);
		response.addLiteral(RDFOntology.Message.hasTimeStamp, time_stamp.asLiteral().toString());
		response.addLiteral(RDFOntology.Message.hasMessage, "base was: " + getBase_url());

		response.addProperty(RDFOntology.Message.hasPermissionStatus, RDFOntology.Message.accepted);
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
		OntModel input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();

		OntModel output_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		RDFNode time_stamp = query.getProperty(RDFOntology.Message.hasTimeStamp).getObject();
		RDFNode webid_url = query.getProperty(RDFOntology.Message.hasWebID).getObject();
		RDFNode path = query.getProperty(RDFOntology.Authorization.hasRulePath).getObject();
		
		boolean result = organization.get().checkRDFPath(webid_url.toString(), path.asResource());

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFOntology.Message.SecurityResponse);
		response.addLiteral(RDFOntology.Message.hasTimeStamp, time_stamp.asLiteral().toString());

		if(result)
			response.addProperty(RDFOntology.Message.hasPermissionStatus, RDFOntology.Message.accepted);
		else
			response.addProperty(RDFOntology.Message.hasPermissionStatus, RDFOntology.Message.denied);
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
		OntModel input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();

		OntModel output_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		RDFNode time_stamp = query.getProperty(RDFOntology.Message.hasTimeStamp).getObject();
		RDFNode webid_url = query.getProperty(RDFOntology.Message.hasWebID).getObject();
		Resource wp = organization.get().getWebIDProfile(webid_url.toString());
		if (wp == null)
			return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("No user").build();
		
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFOntology.Message.SecurityResponse);

		RDFNode public_key=wp.getProperty(RDFOntology.property_hasPublicKey).getObject();
		response.addLiteral(RDFOntology.property_hasPublicKey, public_key.asLiteral().toString());

		response.addLiteral(RDFOntology.Message.hasTimeStamp, time_stamp.asLiteral().toString());
		return Response.status(200).entity(writeModel(output_model)).build();

	}
	
	@POST
	@Path("/registerWebID")
	@Consumes("application/ld+json")
	@Produces("application/ld+json")
	public Response registerWebID(@Context UriInfo uriInfo, String msg) {
		setBaseURI(uriInfo);
		if (!this.organization.isPresent())
		{
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors")
					.build();
		}
		OntModel input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
		{
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();
		}
		OntModel output_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		RDFNode time_stamp = query.getProperty(RDFOntology.Message.hasTimeStamp).getObject();
		RDFNode webid = query.getProperty(RDFOntology.Message.hasWebID).getObject();
		
		//TODO Exponent+modulus
		RDFNode public_key = query.getProperty(RDFOntology.property_hasPublicKey).getObject();
		Resource wc = organization.get().registerWebID(webid.toString(),
				public_key.asLiteral().getLexicalForm());


		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFOntology.Message.SecurityResponse);
		response.addLiteral(RDFOntology.Message.hasTimeStamp, time_stamp.asLiteral().toString());

		response.addProperty(RDFOntology.Message.hasWebID, output_model.getResource(wc.toString()));
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