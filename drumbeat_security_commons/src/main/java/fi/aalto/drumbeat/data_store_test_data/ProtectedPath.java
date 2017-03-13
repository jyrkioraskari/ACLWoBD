package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Property;

import fi.aalto.drumbeat.RDFConstants;

abstract class ProtectedPath extends AbstractData {
	private Project  project;
	private AuthenticationRule  rule;


	public ProtectedPath(URI root,String name, OntModel model) {
		super(root, name, model);
	}

	
	public Project addProject(String name) throws URISyntaxException {
		Property hasProject = RDFConstants.property_hasProject;

		this.project = new Project(new URI(self.getURI()), name, model);
		self.addProperty(hasProject, this.project.self);
		//Resource contractor= this.model.createResource("http://fabricator.local.org/");
		Individual contractor = this.model.createIndividual( "http://fabricator.local.org/", RDFConstants.Contractor);
		this.project.self.addProperty(RDFConstants.property_hasContractor, contractor);
		return this.project;
	}


	public AuthenticationRule addRule(String name) throws URISyntaxException {
		Property hasAuthorizationRule = RDFConstants.property_hasAuthorizationRule;

		this.rule = new AuthenticationRule(new URI(self.getURI()), name, model);
		self.addProperty(hasAuthorizationRule, this.rule.self);
		return this.rule;
	}

}
