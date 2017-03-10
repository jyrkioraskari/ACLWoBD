package fi.aalto.cs.drumbeat.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.rest.DrumbeatSecurityAPI;

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
		Model model = ModelFactory.createDefaultModel();
		try {
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] = RDFConstants.property_knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);
			Resource query = model.createResource();
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response response = target("/hello").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));
			String response_string = response.readEntity(String.class);
			Model response_model = parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
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
		Model model = ModelFactory.createDefaultModel();
		try {
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] = RDFConstants.property_knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);
			Resource query = model.createResource();
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addLiteral(RDFConstants.property_hasWebID, "https:/joku#me");
			query.addLiteral(RDFConstants.property_hasPublicKey, "1234");

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
		ResIterator iter = model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
		Resource response = null;
		if (iter.hasNext())
			response = iter.next();
		RDFNode webid_url = response.getProperty(RDFConstants.property_hasWebID).getObject();
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
		Model model = ModelFactory.createDefaultModel();
		try {
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] = RDFConstants.property_knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);
			Resource query = model.createResource();
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addProperty(RDFConstants.property_hasWebID, model.getResource(webid_url));

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response response = target("/getWebIDProfile").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			//System.out.println("Vastaus haettu webid profiili oli: " + response_string);
			
			Model response_model=parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
			Resource rest_response = null;
			if (iter.hasNext())
				rest_response = iter.next();
			String pk=rest_response.getProperty(RDFConstants.property_hasPublicKey).getObject().asLiteral().getLexicalForm();
			assertNotNull(pk);
			
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	

	
	@Test
	public void test_CheckPath_CreateAndFind() {
		String webid_url = registerWebID();
		Model model = ModelFactory.createDefaultModel();

		try {
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] = RDFConstants.property_knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);
			Resource query = model.createResource();
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addProperty(RDFConstants.property_hasWebID, model.getResource(webid_url));

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response http_response = target("/").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));
			String response_string = http_response.readEntity(String.class);
			http_response.close();
			Model response_model = parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
			Resource response = null;
			if (iter.hasNext())
				response = iter.next();
			boolean status = response.getProperty(RDFConstants.property_status).getObject().asLiteral().getBoolean();
			assertEquals(true, status);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


}