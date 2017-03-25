package fi.aalto.cs.drumbeat.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import fi.aalto.cs.drumbeat.rest.DrumbeatSecurityAPI;
import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.ontology.Ontology;

public class TestOrganizationRESTAPI extends JerseyTest {

	private Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "JSON-LD");
		return json_input_model;
	}

	@Override
	protected Application configure() {
		return new ResourceConfig(DrumbeatSecurityAPI.class);
	}

	
	@Test
	public void test_getHello() {
		String hello=target("/hello").request().get(String.class);
		assertEquals("Hello OK!", hello);
	}
	
	@Test
	public void test_postHello() {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		try {
			Individual query = model.createIndividual(null, Ontology.Message.SecurityQuery);
			RDFDataStore store=null;
			try {
				store = new RDFDataStore(new URI("https://test.org"), "datastore");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			assertNotNull("RDFDataStore store should not be null", store);
			List<String> lista=new ArrayList<>();
			lista.add(Ontology.Contractor.trusts.toString());
			Resource rulepath=Dumbeat_JenaLibrary.createRulePath(model,lista);
			
			query.addProperty(Ontology.Authorization.rulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, Ontology.Message.SecurityQuery);
			query.addLiteral(Ontology.Message.hasTimeStamp, time_inMilliseconds);

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response response = target("/hello").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));
			String response_string = response.readEntity(String.class);
			Model response_model = parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);
			Resource rest_response = null;
			if (iter.hasNext())
				rest_response = iter.next();
			assertNotNull(rest_response);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}


	private String call_registerWebID() {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		try {
			Individual query = model.createIndividual(null, Ontology.Message.SecurityQuery);
			RDFDataStore store=null;
			try {
				store = new RDFDataStore(new URI("https://test.org"), "datastore");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			assertNotNull("RDFDataStore store should not be null", store);
			List<String> lista=new ArrayList<>();
			lista.add(Ontology.Contractor.trusts.toString());
			Resource rulepath=Dumbeat_JenaLibrary.createRulePath(model,lista);
			query.addProperty(Ontology.Authorization.rulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, Ontology.Message.SecurityQuery);
			query.addLiteral(Ontology.Message.hasTimeStamp, time_inMilliseconds);
			Individual person = model.createIndividual("https:/joku#me", Ontology.Contractor.Person);
			query.addProperty(Ontology.Message.hasWebID, person);
			query.addLiteral(Ontology.property_hasPublicKey, "1234");

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response response = target("/registerWebID").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			return response_string;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return "";
	}

	

	private String registerWebID() {
		String reply = call_registerWebID();
		Model model = parseInput(reply);
		ResIterator iter = model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);
		Resource response = null;
		if (iter.hasNext())
			response = iter.next();
		RDFNode webid_url = response.getProperty(Ontology.Message.hasWebID).getObject();
		return webid_url.toString();
	}
	
	

	@Test
	public void test_registerWebID() {

		String webid_url = registerWebID();

		try {
			new URL(webid_url.toString());
		} catch (Exception e) {
			fail("The registered WebID URL should be in a correct format.");
		}
	}
	

	@Test
	public void test_getWebIDProfile() {
		String webid_url = registerWebID();
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		try {
			Individual query = model.createIndividual(null, Ontology.Message.SecurityQuery);
			RDFDataStore store=null;
			try {
				store = new RDFDataStore(new URI("https://test.org"), "datastore");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			assertNotNull("RDFDataStore store should not be null", store);
			List<String> lista=new ArrayList<>();
			lista.add(Ontology.Contractor.trusts.toString());
			Resource rulepath=Dumbeat_JenaLibrary.createRulePath(model,lista);
			query.addProperty(Ontology.Authorization.rulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, Ontology.Message.SecurityQuery);
			query.addLiteral(Ontology.Message.hasTimeStamp, time_inMilliseconds);
			query.addProperty(Ontology.Message.hasWebID, model.getResource(webid_url));

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response response = target("/getWebIDProfile").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			//System.out.println("Vastaus haettu webid profiili oli: " + response_string);
			
			Model response_model=parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);
			Resource rest_response = null;
			if (iter.hasNext())
				rest_response = iter.next();
			String pk=rest_response.getProperty(Ontology.property_hasPublicKey).getObject().asLiteral().getLexicalForm();
			assertNotNull(pk);
			
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	

	//TODO create test data and use new style RulePath, not RDFList
	@Test
	public void test_CheckPath_CreateAndFind() {
		String webid_url = registerWebID();
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

		try {
			Individual query = model.createIndividual(null, Ontology.Message.SecurityQuery);
			RDFDataStore store=null;
			try {
				store = new RDFDataStore(new URI("https://test.org"), "datastore");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			assertNotNull("RDFDataStore store should not be null", store);
			List<String> lista=new ArrayList<>();
			lista.add(Ontology.Contractor.trusts.toString());
			Resource rulepath=Dumbeat_JenaLibrary.createRulePath(model,lista);
			query.addProperty(Ontology.Authorization.rulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, Ontology.Message.SecurityQuery);
			query.addLiteral(Ontology.Message.hasTimeStamp, time_inMilliseconds);
			query.addProperty(Ontology.Message.hasWebID, model.getResource(webid_url));

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response http_response = target("/").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));
			String response_string = http_response.readEntity(String.class);
			http_response.close();
			Model response_model = parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);
			Resource response = null;
			if (iter.hasNext())
				response = iter.next();
			
			//boolean status = response.getProperty(RDFConstants.Ontology.Message.hasPermissionStatus).getObject().asResource() == RDFConstants.Ontology.Message.accepted;
			//assertEquals(true, status);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}