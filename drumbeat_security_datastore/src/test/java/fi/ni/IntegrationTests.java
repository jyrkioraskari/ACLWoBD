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


			System.out.println(createEmptyQueryString());
			ClientResponse response = webResource.type("application/ld+json")
			   .post(ClientResponse.class, createEmptyQueryString());

			/*if (response.getStatus() != 201) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}*/

			System.out.println("Output from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		  } catch (Exception e) {

			e.printStackTrace();

		  }

	}

}
