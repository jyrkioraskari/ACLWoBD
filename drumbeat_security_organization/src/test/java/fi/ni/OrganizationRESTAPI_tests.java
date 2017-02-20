package fi.ni;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.rest.Organization_RESTfulAPI;

public class OrganizationRESTAPI_tests extends JerseyTest {


	@Override
	protected Application configure() {
		return new ResourceConfig(Organization_RESTfulAPI.class);
	}

	@Test
	public void test_hello() {
		Response response = target("/security/hello").request().get();
		String hello = response.readEntity(String.class);
		assertEquals("OK!", hello);
		response.close();
	}
	
	@Test
	public void test_checkUser_parameters() {
		Model model =  ModelFactory.createDefaultModel();

		try {
			Property knowsPerson = model.getProperty(Constants.security_ontology_base + "#knowsPerson");
		
			Resource query_resource = model.getResource(Constants.security_ontology_base+"#CheckUser_query");
			
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] =   knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);	
			
			Property hasPath = model.getProperty(Constants.security_ontology_base + "#hasRulePath");
			query_resource.addProperty(hasPath, rulepath);
			
			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
	        writer.flush();

			Response response = target("/security/check_user").request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_string = response.readEntity(String.class);
			System.out.println("Vastaus oli: " + response_string);
			// assertEquals("OK!", response_string);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		;

	}
}