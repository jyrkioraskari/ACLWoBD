package fi.aalto.cs.drumbeat.tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

import fi.aalto.cs.drumbeat.controllers.DrumbeatSecurityController;
import fi.aalto.drumbeat.RDFOntology;
import junit.framework.TestCase;

public class TestOrganizationManager extends TestCase {
	Optional<DrumbeatSecurityController> organization = Optional.empty();

	//  Initial setup before any test
	
	protected void setUp(){
		 organization = Optional.empty();
		 try {
				organization = Optional
						.of(DrumbeatSecurityController.getOrganizationManager(new URI("http://testing.org/p1/p2/p2")));
			} catch (URISyntaxException e) {
				fail("The URL should be in a correct format.");
			}
	   }
	

	public void testSimpleWCRegistration() {
		Resource wc = organization.get().registerWebID("http://person#i","1234");
		//TODO hae PK
		assertNotNull(wc);
		
	}
	
	//TODO replace RDFList with new RulePath
	@Test
	public void testCreateAndTestPath() {
		Model model = ModelFactory.createDefaultModel();
		Resource wc = organization.get().registerWebID("http://person#i","1234");
		
		RDFNode[] rulepath_list = new RDFNode[1];
		rulepath_list[0] = RDFOntology.Contractor.trusts;
		RDFList rulepath = model.createList(rulepath_list);
		
		boolean result_true = organization.get().checkRDFPath(wc.toString(), rulepath.asResource());
		assertEquals(true, result_true);
		
		boolean result_false = organization.get().checkRDFPath("http://unknown/person", rulepath.asResource());
		assertEquals(false, result_false);

	}
	
	
}