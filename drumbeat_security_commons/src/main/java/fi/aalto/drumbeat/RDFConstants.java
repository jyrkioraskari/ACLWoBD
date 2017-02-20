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

	public Resource query() {
		return model.getResource(Constants.security_ontology_base + "#Query");
	}

	public Resource Response() {
		return model.getResource(Constants.security_ontology_base + "#Response");
	}

	static public Property property_knowsPerson = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#knowsPerson");
	static public Property property_hasRulePath = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasRulePath");
	static public Property property_hasTimeStamp = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasTimeStamp");

	static public Property property_hasWebID = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasWebID");
	static public Property property_hasName = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasName");
	static public Property property_hasPublicKey = ResourceFactory
			.createProperty(Constants.security_ontology_base + "#hasPublicKey");

}
