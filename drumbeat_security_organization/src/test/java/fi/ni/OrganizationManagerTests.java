package fi.ni;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.junit.Test;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.security.OrganizationManager;
import fi.aalto.drumbeat.webid.WebIDCertificate;
import fi.aalto.drumbeat.webid.WebIDProfile;
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
	

	public void testSimpleWCRegistration() {
		WebIDCertificate wc = organization.get().registerWebID("Etu Sukunimi","1234");
		
		assertNotNull(wc);
		
		WebIDProfile wp = organization.get().getWebIDProfile(wc.getWebid_uri().toString());

		assertNotNull(wp);
	}
	

	@Test
	public void testCresteAndTestPath() {
		Model model = ModelFactory.createDefaultModel();
		WebIDCertificate wc = organization.get().registerWebID("Etu Sukunimi","1234");
		
		RDFNode[] rulepath_list = new RDFNode[1];
		rulepath_list[0] = RDFConstants.property_knowsPerson;
		RDFList rulepath = model.createList(rulepath_list);
		
		boolean result_true = organization.get().checkRDFPath(wc.getWebid_uri().toString(), rulepath.asResource());
		assertEquals(true, result_true);
		
		boolean result_false = organization.get().checkRDFPath("http://unknown/person", rulepath.asResource());
		assertEquals(false, result_false);

	}
}