package fi.ni;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import fi.aalto.drumbeat.RDFConstants;
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
        String webid="http://user.com/user#me";
		try {
			
			RDFConstants rdf=new RDFConstants(model);			
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] =   RDFConstants.property_knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);	
			Resource query =model.createResource();	
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, rdf.query());
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addProperty(RDFConstants.property_hasWebID, model.getResource(webid));
			
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