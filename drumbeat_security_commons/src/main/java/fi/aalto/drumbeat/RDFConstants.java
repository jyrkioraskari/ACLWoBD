package fi.aalto.drumbeat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class RDFConstants {
	private Model model;

	public RDFConstants(Model model) {
		this.model = model;
	}

	// Ontolohy classes
	static public Resource Query = ResourceFactory.createResource(Constants.security_ontology_base + "#Query");
	static public Resource Response = ResourceFactory.createResource(Constants.security_ontology_base + "#Response");
	
	static public Resource DataStore = ResourceFactory.createResource(Constants.security_ontology_base + "#DataStore");
	static public Resource Organization = ResourceFactory.createResource(Constants.security_ontology_base + "#Organization");
	static public Resource Project = ResourceFactory.createResource(Constants.security_ontology_base + "#Project");
	
	static public Property property_hasTimeStamp = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasTimeStamp");

	static public Property property_hasWebID = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasWebID");
	static public Property property_hasName = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasName");
	static public Property property_hasPublicKey = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasPublicKey");
	
	static public Property property_hasCollection = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasCollection");
	static public Property property_hasDataSource = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasDataSource");
	static public Property property_hasDataSet = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasDataSet");
	

	static public Property property_hasAuthorizationRule = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasAuthorizationRule");
	static public Property property_hasPermission = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasPermission");
	static public Property property_hasRulePath = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasRulePath");
	static public Property property_hasPath = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasPath");
	

	static public Property property_hasProject = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasProject");
	static public Property property_hasMainContractor = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasMainContractor");
	static public Property property_hasContractor = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasContractor");
	static public Property property_knowsPerson = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#knowsPerson");
	
	static public Property property_status = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#status");

	
	static public Property property_information = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#information");
}
