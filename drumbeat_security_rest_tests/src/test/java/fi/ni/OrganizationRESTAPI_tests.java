package fi.ni;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

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
import fi.aalto.drumbeat.rest.Organization;

public class OrganizationRESTAPI_tests extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(Organization.class);
	}

	@Test
	public void test_getHello() {
		Response response = target("/organization/hello").request().get();
		String hello = response.readEntity(String.class);
		assertEquals("Hello OK!", hello);
		response.close();
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

			Response response = target("/organization/hello").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			Model response_model = parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
			Resource rest_response = null;
			if (iter.hasNext())
				rest_response = iter.next();
			assertNotNull(rest_response);
			
			response.close();
		} catch (Exception e) {
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
			query.addLiteral(RDFConstants.property_hasName, "Matti Meikäläinen");
			query.addLiteral(RDFConstants.property_hasPublicKey, "1234");

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response response = target("/organization/registerWebID").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			response.close();
			return response_string;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "JSON-LD");
		return json_input_model;
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

			Response response = target("/organization/getWebIDProfile").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			System.out.println("Vastaus haettu webid profiili oli: " + response_string);
			
			Model response_model=parseInput(response_string);
			ResIterator iter = response_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
			Resource rest_response = null;
			if (iter.hasNext())
				rest_response = iter.next();
			String name=rest_response.getProperty(RDFConstants.property_hasName).getObject().asLiteral().getLexicalForm();
			assertNotNull(name);
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

			Response http_response = target("/organization/checkPath").request()
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

	@Test
	public void test_webIDProfileTTLTest() {
		try {
			URI webid_url = new URI(registerWebID());
			String path = webid_url.getPath();
			String webid = URLEncoder.encode(webid_url.toString());
			Response response = target(path).request().get();
			String result_string = response.readEntity(String.class);
			response.close();
			
			
			Model input_model = ModelFactory.createDefaultModel();
			input_model.read(new ByteArrayInputStream(result_string.getBytes()), null, "TTL");
			Resource rwebid= input_model.getResource(webid_url.toString());
			String pk=rwebid.getProperty(RDFConstants.property_hasPublicKey).getObject().asLiteral().getLexicalForm();
			
			assertNotNull(pk);

		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}