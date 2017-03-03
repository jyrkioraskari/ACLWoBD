package fi.ni;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.http.client.utils.URIBuilder;
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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import fi.aalto.drumbeat.RDFConstants;
import fi.ni.test_categories.IntegrationTest;

@Category(IntegrationTest.class)

public class RESTAPIIntegrationTests {

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
	public void testHelloGET_HTTP_architect_local_org() {
		try {

			javax.ws.rs.client.Client  client = IgnoreSSLClient();

			Response response = client.target("https://architect.local.org:8443/security/organization/hello").request("text/plain").get();
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
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

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://architect.local.org:8080/security/rest/organization/hello");
			System.out.println("QUERY (POST Hello ) " + createEmptyQueryString());
			ClientResponse response = webResource.type("application/ld+json").post(ClientResponse.class,
					createEmptyQueryString());
			System.out.println("RESPONSE (POST Hello ) from Server .... \n");
			String output = response.getEntity(String.class);
			System.out.println(output);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@Test
	public void testRegisterWebID_HTTP_architect_local_org() {
		assertNotNull(registerWebID());
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

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://architect.local.org:8080/security/rest/organization/registerWebID");
			ClientResponse response = webResource.type("application/ld+json").post(ClientResponse.class,
					writer.toString());

			String output = response.getEntity(String.class);
			response.close();
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
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

			Client client = Client.create();

			WebResource webResource = client
					.resource("http://architect.local.org:8080/security/rest/organization/checkPath");
			ClientResponse response = webResource.type("application/ld+json").post(ClientResponse.class,
					writer.toString());

			String output = response.getEntity(String.class);
			System.out.println("RESPONSE (Check RulePath) " + output);
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_webIDProfileTTLTest() {
		try {
			//TODO SSL
			URI webid_url = new URIBuilder(registerWebID()).setScheme("http").build();
			System.out.println("TTL integration test.  URL was: "+webid_url);
			
			Client client = Client.create();
			WebResource webResource = client
					.resource(webid_url.toString());
			
			ClientResponse response = webResource.accept("text/turtle").get(ClientResponse.class);
			System.out.println(""+response);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
		    response.close();
			String result_string = response.getEntity(String.class);
			
			Model input_model = ModelFactory.createDefaultModel();
			input_model.read(new ByteArrayInputStream(result_string.getBytes()), null, "TTL");
			Resource rwebid= input_model.getResource(webid_url.toString());
			String pk=rwebid.getProperty(RDFConstants.property_hasPublicKey).getObject().asLiteral().getLexicalForm();
			
			assertNotNull(pk);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


}
