package fi.ni;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.Test;

import fi.aalto.drumbeat.security.OrganizationManager;
import junit.framework.TestCase;

public class OrganizationManagerTests extends TestCase {
	Optional<OrganizationManager> organization = Optional.empty();

	//  Initial setup before any test
	
	protected void setUp(){
		 organization = Optional.empty();
		 try {
				organization = Optional
						.of(OrganizationManager.getOrganizationManager(new URI("http://testing.org/p1/p2/p2")));
			} catch (URISyntaxException e) {
				fail("The URL should be in a correct format.");
			}
	   }
	

	public void testSometing() {
		System.out.println(organization.isPresent());
	}
	

	@Test
	public void testSometing2() {
		System.out.println(organization.isPresent());
	}
}