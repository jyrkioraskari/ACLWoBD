package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.RDFConstants;

public class Collection extends ProtectedPath {
	private Map<String,DataSource>  datasources=new HashMap<String,DataSource>();

	public Collection(URI root,String name, Model model) {
		super(root, name, model);
	}

	
	public DataSource addDataSource(String name) throws URISyntaxException {
		Property hasDataSource = RDFConstants.hasDataSource;

		DataSource d = new DataSource(new URI(self.getURI()), name, model);
		self.addProperty(hasDataSource, d.self);
		datasources.put(name,d);
		return d;
	}

}
