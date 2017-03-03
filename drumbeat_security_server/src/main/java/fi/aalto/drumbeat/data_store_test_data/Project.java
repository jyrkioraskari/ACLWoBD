package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;

import org.apache.jena.rdf.model.Model;

import fi.aalto.drumbeat.data_store_test_data.AbstractData;

public class Project extends AbstractData {

	public Project(URI root,String name, Model model) {
		super(root, name, model);
	}

	

}
