package fi.ni;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import fi.aalto.drumbeat.data_store_test_data.Collection;
import fi.aalto.drumbeat.data_store_test_data.DataSet;
import fi.aalto.drumbeat.data_store_test_data.DataSource;
import junit.framework.TestCase;

public class TestDataTests extends TestCase {
	

	public TestDataTests()
	{
		super();
		
	}
	

	public void test_JenaFromBean() {
		BasicConfigurator.configure();
		Logger l=Logger.getLogger("org.apache");
		l.setLevel(Level.INFO);
		Model model = ModelFactory.createDefaultModel();
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
