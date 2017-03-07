package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.drumbeat.RDFConstants;

abstract class ProtectedPath extends AbstractData {
	private Project  project;
	private AuthenticationRule  rule;


	public ProtectedPath(URI root,String name, Model model) {
		super(root, name, model);
	}

	
	public Project addProject(String name) throws URISyntaxException {
		Property hasProject = RDFConstants.property_hasProject;

		this.project = new Project(new URI(self.getURI()), name, model);
		self.addProperty(hasProject, this.project.self);
		Resource contractor= this.model.createResource("http://fabricator.local.org/");
		this.project.self.addProperty(RDFConstants.property_hasContractor, contractor);
		//Resource jyrkio= this.model.createResource("https://jyrkio2.databox.me/profile/card#me");
		//contractor.addProperty(RDFConstants.property_knowsPerson,jyrkio);
		
		
		return this.project;
	}


	public AuthenticationRule addRule(String name) throws URISyntaxException {
		Property hasAuthorizationRule = RDFConstants.property_hasAuthorizationRule;

		this.rule = new AuthenticationRule(new URI(self.getURI()), name, model);
		self.addProperty(hasAuthorizationRule, this.rule.self);
		return this.rule;
	}

}
