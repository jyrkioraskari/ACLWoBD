package fi.aalto.cs.drumbeat.tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

import fi.aalto.cs.drumbeat.controllers.DrumbeatSecurityController;
import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.ontology.Contractor;
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
	

	/*public void testSimpleWCRegistration() {
		Resource wc = organization.get().registerWebID("http://person#i","1234");
		//TODO hae PK
		assertNotNull(wc);
		
	}*/
	
	
	@Test
	public void testTestPath() {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		//Resource wc = organization.get().registerWebID("http://person#i","1234");
		
		RDFDataStore store=null;
		try {
			store = new RDFDataStore(new URI("https://test.org/"), "datastore");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		assertNotNull("RDFDataStore store should not be null", store);
		List<String> lista=new ArrayList<>();
		lista.add(Contractor.trusts.toString());
		Resource rulepath=Dumbeat_JenaLibrary.createRulePath(store.getModel(),lista);
		
		//TODO tulisiko olla totta?
		LinkedList<Resource> rulepath_list = Dumbeat_JenaLibrary.parseRulePath(store.getModel(),rulepath);
		List<String> rulepath_strlist = new ArrayList<>();
		
		for (Resource r : rulepath_list)
			rulepath_strlist.add(r.getURI());
		
		boolean result_true = organization.get().validate("https://jyrkio2.databox.me/profile/card#me", rulepath_strlist);
		assertEquals(true, result_true);
		
		//boolean result_false = organization.get().checkRDFPath("http://unknown/person", rulepath.asResource());
		//assertEquals(false, result_false);

	}
	
	
}