package fi.ni;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

import fi.ni.data_store_test_data.Collection;
import fi.ni.data_store_test_data.DataSet;
import fi.ni.data_store_test_data.DataSource;
import junit.framework.TestCase;

public class TestDataTests extends TestCase {
	
	final private Model model = ModelFactory.createDefaultModel();


	public TestDataTests()
	{
		super();
	}
	

	@Test
	public void test_JenaFromBean() {
		try {
			Collection c= new Collection(new URI("https://architectural.drb.cs.hut.fi/security/"), "turva", model);
			c.addProject("project1");
			c.addRule("rule1");
			
			DataSource ds=c.addDataSource("architectural");
			ds.addRule("rule2");
			
			
			DataSet dset=ds.addDataSet("20151125");
			dset.addRule("rule32");
			
			//model.write(System.out,"TURTLE");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
