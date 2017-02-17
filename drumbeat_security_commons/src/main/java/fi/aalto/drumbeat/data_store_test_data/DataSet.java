package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;

import org.apache.jena.rdf.model.Model;

public class DataSet extends ProtectedPath {

	public DataSet(URI root,String name, Model model) {
		super(root, name, model);
	}

	

}
