package fi.ni;

import static org.junit.Assert.assertEquals;
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

			RDFConstants rdf = new RDFConstants(model);
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
			System.out.println("Vastaus postHello oli: " + response_string);
			// assertEquals("OK!", response_string);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		;

	}

	private String call_registerWebID() {
		Model model = ModelFactory.createDefaultModel();
		try {

			RDFConstants rdf = new RDFConstants(model);
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
			System.out.println("Vastaus webid oli: " + response);
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
		System.out.println("webid oli: " + webid_url);
		
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

			RDFConstants rdf = new RDFConstants(model);
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
			System.out.println("WebID profile get query was: "+writer.toString());

			Response response = target("/organization/getWebIDProfile").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			System.out.println("Vastaus haettu webid profiili oli: " + response);
			// assertEquals("OK!", response_string);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	@Test
	public void test_CheckPath_Simple() {
		Model model = ModelFactory.createDefaultModel();
		String webid = "http://user.com/user#me";
		try {

			RDFConstants rdf = new RDFConstants(model);
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] = RDFConstants.property_knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);
			Resource query = model.createResource();
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addProperty(RDFConstants.property_hasWebID, model.getResource(webid));

			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
			writer.flush();

			Response response = target("/organization/checkPath").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			System.out.println("Vastaus simple oli: " + response_string);
			// assertEquals("OK!", response_string);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_CheckPath_CreateAndFind() {
		String webid_url = registerWebID();
		Model model = ModelFactory.createDefaultModel();
	
		try {

			RDFConstants rdf = new RDFConstants(model);
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

			Response response = target("/organization/checkPath").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			System.out.println("Vastaus C&Fe oli: " + response_string);
			// assertEquals("OK!", response_string);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}