

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.rdf.model.Resource;
import org.junit.Test;

import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.RDFOntology;

public class TestLibraryFunctions {

	// RulePath creation and parse
	@Test
	public void test() {
		try {
			RDFDataStore store=new RDFDataStore(new URI("https://test.org"), "datastore");
			List<String> lista=new ArrayList<>();
			lista.add(RDFOntology.Occupation.hasOccupation.toString());
			lista.add(RDFOntology.Contractor.hasMainContractor.toString());
			lista.add(RDFOntology.Contractor.trusts.toString());
			Resource rlista=Dumbeat_JenaLibrary.createRulePath(store.getModel(),lista);
			LinkedList<Resource> uusi_lista=Dumbeat_JenaLibrary.parseRulePath(store.getModel(),rlista);
			int i=0;
			for(Resource p:uusi_lista) {
				assertEquals(lista.get(i++), p.toString());
			}
			
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		
	}

}
