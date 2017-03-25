package fi.aalto.cs.drumbeat.rest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.glassfish.jersey.server.mvc.Viewable;

import fi.aalto.cs.drumbeat.controllers.DrumbeatSecurityController;
import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.ontology.Ontology;
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


		RDFNode time_stamp = query.getProperty(Ontology.Message.hasTimeStamp).getObject();
		if(checkRepetition(time_stamp,msg))
		{
			return Response.status(HttpServletResponse.SC_NOT_ACCEPTABLE).entity("No access").build();
		}

		Model output_model = ModelFactory.createDefaultModel();
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, Ontology.Message.SecurityResponse);
		response.addLiteral(Ontology.Message.hasTimeStamp, time_stamp.asLiteral().toString());
		response.addLiteral(Ontology.Message.hasMessage, "base was: " + getBase_url());

		response.addProperty(Ontology.Message.hasPermissionStatus, Ontology.Message.accepted);
		return Response.status(200).entity(modelToString(output_model)).build();
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
	public Response validatePath(@Context UriInfo uriInfo, String msg) {
		setBaseURI(uriInfo);
		if (!this.organization.isPresent())
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("Initialization errors")
					.build();
		Model input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();


		RDFNode time_stamp = query.getProperty(Ontology.Message.hasTimeStamp).getObject();
		if(checkRepetition(time_stamp,msg))
		{
			return Response.status(HttpServletResponse.SC_NOT_ACCEPTABLE).entity("No access").build();
		}
		Model output_model = ModelFactory.createDefaultModel();
		RDFNode webid_url = query.getProperty(Ontology.Message.hasWebID).getObject();
		Resource path = query.getProperty(Ontology.Authorization.rulePath).getObject().asResource();
		
		LinkedList<Resource> rulepath = Dumbeat_JenaLibrary.parseRulePath(input_model,path);
		List<String> rulepath_list = new ArrayList<>();
		
		for (Resource r : rulepath)
			rulepath_list.add(r.getURI());
		
		boolean result = organization.get().validate(webid_url.toString(), rulepath_list);

		Resource response = output_model.createResource();
		response.addProperty(RDF.type, Ontology.Message.SecurityResponse);
		response.addLiteral(Ontology.Message.hasTimeStamp, time_stamp.asLiteral().toString());

		if(result)
			response.addProperty(Ontology.Message.hasPermissionStatus, Ontology.Message.accepted);
		else
			response.addProperty(Ontology.Message.hasPermissionStatus, Ontology.Message.denied);
		return Response.status(200).entity(modelToString(output_model)).build();

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

		
		RDFNode time_stamp = query.getProperty(Ontology.Message.hasTimeStamp).getObject();
		if(checkRepetition(time_stamp,msg))
		{
			return Response.status(HttpServletResponse.SC_NOT_ACCEPTABLE).entity("No access").build();
		}
		
		Model output_model = ModelFactory.createDefaultModel();
		RDFNode webid_url = query.getProperty(Ontology.Message.hasWebID).getObject();
		Resource wp = organization.get().getWebIDProfile(webid_url.toString());
		if (wp == null)
			return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("No user").build();
		
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, Ontology.Message.SecurityResponse);

		RDFNode public_key=wp.getProperty(Ontology.property_hasPublicKey).getObject();
		response.addLiteral(Ontology.property_hasPublicKey, public_key.asLiteral().toString());

		response.addLiteral(Ontology.Message.hasTimeStamp, time_stamp.asLiteral().toString());
		return Response.status(200).entity(modelToString(output_model)).build();

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
		Model input_model = parseInput(msg);
		Resource query = getQuery(input_model);
		if (query == null)
		{
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("No queries").build();
		}
		RDFNode time_stamp = query.getProperty(Ontology.Message.hasTimeStamp).getObject();
		if(checkRepetition(time_stamp,msg))
		{
			return Response.status(HttpServletResponse.SC_NOT_ACCEPTABLE).entity("No access").build();
		}
		Model output_model = ModelFactory.createDefaultModel();
		RDFNode webid = query.getProperty(Ontology.Message.hasWebID).getObject();
		
		//TODO Exponent+modulus
		RDFNode public_key = query.getProperty(Ontology.property_hasPublicKey).getObject();
		Resource wc = organization.get().registerWebID(webid.toString(),
				public_key.asLiteral().getLexicalForm());


		Resource response = output_model.createResource();
		response.addProperty(RDF.type, Ontology.Message.SecurityResponse);
		response.addLiteral(Ontology.Message.hasTimeStamp, time_stamp.asLiteral().toString());

		response.addProperty(Ontology.Message.hasWebID, output_model.getResource(wc.toString()));
		return Response.status(200).entity(modelToString(output_model)).build();
	}

	Optional<DrumbeatSecurityController> organization = Optional.empty();

	@Override
	public void setBaseURI(UriInfo uriInfo) {
		super.setBaseURI(uriInfo);
		if (!this.organization.isPresent())
			this.organization = Optional.of(DrumbeatSecurityController.getDrumbeatSecurityController(getBase_url()));
	}
}