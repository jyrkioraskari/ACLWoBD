package fi.ni;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import fi.ni.test_categories.IntegrationTest;

@Category(IntegrationTest.class)

public class IntegrationTests {

	@Test
	public void testHelloGET_HTTP_architect_local_org() {
		try {

		Client client = Client.create();

		WebResource webResource = client
		   .resource("http://architect.local.org:8080/security/rest/organization/hello");
		ClientResponse response = webResource.accept("text/plain")
                .get(ClientResponse.class);
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : "
			+ response.getStatus());
		}
		String output = response.getEntity(String.class);
		assertEquals("OK!", output);

	  } catch (Exception e) {

		e.printStackTrace();

	  }

	}

}
