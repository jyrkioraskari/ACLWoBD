package fi.ni;

import static org.junit.Assert.*;

import java.io.StringWriter;

import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import fi.aalto.drumbeat.RDFConstants;
import fi.ni.test_categories.IntegrationTest;

@Category(IntegrationTest.class)


public class IntegrationTests {

	
	@Test
	public void testHelloGET_HTTP_architect_local_org() {
		try {

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://architect.local.org:8080/security/rest/organization/hello");
			ClientResponse response = webResource.accept("text/plain").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			String output = response.getEntity(String.class);
			assertEquals("Hello OK!", output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/*
	 * Test the basic JSON-LD connection to the server REST Interface 
	 */
	private String createEmptyQueryString() {
			Model model =  ModelFactory.createDefaultModel();
			try {
				
				RDFConstants rdf=new RDFConstants(model);			
				Resource query =model.createResource();	
				Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
				query.addProperty(RDF.type, RDFConstants.Query);
				query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
				
				StringWriter writer = new StringWriter();
				model.write(writer, "JSON-LD");
		        writer.flush();
		        return writer.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
	}
	
	
	
	@Test
	public void testHelloPOST_HTTP_architect_local_org() {
		try {

			Client client = Client.create();

			WebResource webResource = client
			   .resource("http://architect.local.org:8080/security/rest/organization/hello");
			System.out.println("QUERY (POST Hello ) "+createEmptyQueryString());
			ClientResponse response = webResource.type("application/ld+json")
			   .post(ClientResponse.class, createEmptyQueryString());
			System.out.println("RESPONSE (POST Hello ) from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		  } catch (Exception e) {

			e.printStackTrace();

		  }

	}

	
	
	@Test
	public void testRegisterWebID_HTTP_architect_local_org() {
		Model model =  ModelFactory.createDefaultModel();
		try {
			
			RDFConstants rdf=new RDFConstants(model);			
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] =   RDFConstants.property_knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);	
			Resource query =model.createResource();	
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addLiteral(RDFConstants.property_hasName, "Matti Meikäläinen");
			query.addLiteral(RDFConstants.property_hasPublicKey, "1234");
			
			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
	        writer.flush();
	        System.out.println("QUERY (WebID registration) "+writer.toString());

			Client client = Client.create();

			WebResource webResource = client
			   .resource("http://architect.local.org:8080/security/rest/organization/registerWebID");
			ClientResponse response = webResource.type("application/ld+json")
			   .post(ClientResponse.class, writer.toString());

			String output = response.getEntity(String.class);
			System.out.println("RESPONSE (WebID registration) "+output);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	@Test
	public void testCheckPath_HTTP_architect_local_org() {
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
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addProperty(RDFConstants.property_hasWebID, model.getResource(webid));
			
			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
	        writer.flush();
	        System.out.println("QUERY (Check RulePath) "+writer.toString());

			Client client = Client.create();

			WebResource webResource = client
			   .resource("http://architect.local.org:8080/security/rest/organization/checkPath");
			ClientResponse response = webResource.type("application/ld+json")
			   .post(ClientResponse.class, writer.toString());

			String output = response.getEntity(String.class);
			System.out.println("RESPONSE (Check RulePath) "+output);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	

}
