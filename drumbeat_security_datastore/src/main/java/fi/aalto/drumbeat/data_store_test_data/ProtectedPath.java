package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.data_store_test_data.AbstractData;
import fi.aalto.drumbeat.data_store_test_data.AuthenticationRule;
import fi.aalto.drumbeat.data_store_test_data.Project;

abstract class ProtectedPath extends AbstractData {
	private Project  project;
	private AuthenticationRule  rule;


	public ProtectedPath(URI root,String name, Model model) {
		super(root, name, model);
	}

	
	public Project addProject(String name) throws URISyntaxException {
		Property hasProject = model.getProperty(Constants.security_ontology_base + "#hasProject");

		this.project = new Project(new URI(self.getURI()), name, model);
		self.addProperty(hasProject, this.project.self);
		return this.project;
	}


	public AuthenticationRule addRule(String name) throws URISyntaxException {
		Property hasAuthorizationRule = model.getProperty(Constants.security_ontology_base + "#hasAuthorizationRule");

		this.rule = new AuthenticationRule(new URI(self.getURI()), name, model);
		self.addProperty(hasAuthorizationRule, this.rule.self);
		return this.rule;
	}

}
