package fi.aalto.cs.drumbeat.tests.network_integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.junit.experimental.categories.Category;


import fi.aalto.cs.drumbeat.tests.test_categories.IntegrationTest;
import fi.aalto.drumbeat.RDFConstants;

@Category(IntegrationTest.class)


public class OrganizationRESTAPI {

	public static javax.ws.rs.client.Client IgnoreSSLClient() throws Exception {
	    SSLContext sslcontext = SSLContext.getInstance("TLS");
	    sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
	        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
	        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
	        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }

	    }}, new java.security.SecureRandom());
	    return ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
	}
	
	@Test
	public void testHelloGET_HTTPS_architect_local_org() {
		try {

			javax.ws.rs.client.Client  client = IgnoreSSLClient();

			Response response = client.target("https://architect.local.org/hello").request("text/plain").get();
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			assertEquals(200, response.getStatus());
			String output = response.readEntity(String.class);
			assertEquals("Hello OK!", output);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	

	private String createEmptyQueryString() {
		Model model = ModelFactory.createDefaultModel();
		try {

			Resource query = model.createResource();
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

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("http://architect.local.org/hello");

			
			Response response = target.request()
					.post(Entity.entity(createEmptyQueryString(), "application/ld+json"));
			
			String response_txt = response.readEntity(String.class);
			
			
			System.out.println("RESPONSE (POST Hello ) from Server .... \n");
			System.out.println(response_txt);
			response.close();

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());

		}

	}

	
	
	private String registerWebID() {
		String reply = call_registerWebID();
		if(reply==null)
			return reply;
		Model model = parseInput(reply);
		ResIterator iter = model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
		Resource response = null;
		if (iter.hasNext())
			response = iter.next();
		RDFNode webid_url = response.getProperty(RDFConstants.property_hasWebID).getObject();
		return webid_url.toString();
	}
	
	private Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "JSON-LD");
		return json_input_model;
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
			System.out.println("QUERY (WebID registration) " + writer.toString());

			
			
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("http://architect.local.org/registerWebID");
			Response response = target.request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));
			String response_txt = response.readEntity(String.class);
			assertEquals(200, response.getStatus());
			response.close();
			return response_txt;
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		fail("null result");
		return null;
	}

	@Test
	public void testCheckPath_HTTP_architect_local_org() {
		Model model = ModelFactory.createDefaultModel();
		String webid = "http://user.com/user#me";

		try {
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
			System.out.println("QUERY (Check RulePath) " + writer.toString());

			
			
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target("http://architect.local.org/");

			
			Response response = target.request()
					.post(Entity.entity(writer.toString(), "application/ld+json"));
			
			String response_txt = response.readEntity(String.class);

			assertEquals(200, response.getStatus());
			System.out.println("RESPONSE (Check RulePath) " + response_txt);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testCheckPath_HTTPS_insecure_architect_local_org() {
		

		Model model = ModelFactory.createDefaultModel();
		String webid = "http://user.com/user#me";

		try {
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
			System.out.println("QUERY (Check RulePath) " + writer.toString());

			javax.ws.rs.client.Client  client = IgnoreSSLClient();

			Response response = client.target("https://architect.local.org/").request().post(Entity.entity(writer.toString(), "application/ld+json"));
			
			assertEquals(200, response.getStatus());
			String response_string = response.readEntity(String.class);

			System.out.println("HTTPS RESPONSE (Check RulePath) " + response_string);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	

}
