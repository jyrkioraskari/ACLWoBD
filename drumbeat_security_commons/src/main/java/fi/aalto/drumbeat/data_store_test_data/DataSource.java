package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import fi.aalto.drumbeat.Constants;
import fi.ni.data_store_test_data.DataSet;
import fi.ni.data_store_test_data.ProtectedPath;

public class DataSource extends ProtectedPath {
	private Map<String,DataSet>  datasets=new HashMap<String,DataSet>();


	public DataSource(URI root,String name, Model model) {
		super(root, name, model);
	}

	

	public DataSet addDataSet(String name) throws URISyntaxException {
		Property hasDataSet = model.getProperty(Constants.security_ontology_base + "#hasDataSet");

		DataSet d = new DataSet(new URI(self.getURI()), name, model);
		self.addProperty(hasDataSet, d.self);
		datasets.put(name,d);
		return d;
	}

}
