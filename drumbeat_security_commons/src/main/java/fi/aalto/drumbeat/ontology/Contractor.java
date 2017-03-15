package fi.aalto.drumbeat.ontology;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import fi.aalto.drumbeat.Constants;

public class Contractor {
	static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
	static public OntClass Contractor = schema.createClass(Constants.security_ontology_base + "#Contractor");
	// WebID profile ontology class:
	static public OntClass Person = schema.createClass("http://xmlns.com/foaf/0.1/Person");


	static public ObjectProperty trusts = schema.createObjectProperty(Constants.security_ontology_base + "#trusts");
	static {
		trusts.addDomain(Contractor);
		trusts.addRange(Person);
		Contractor.addSubClass(schema.createAllValuesFromRestriction(null, trusts, Person));
	}

	static public ObjectProperty hasContractor = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasContractor");
	static {
		hasContractor.addDomain(Club.Club);
		hasContractor.addRange(Contractor);
		Club.Club.addSubClass(schema.createAllValuesFromRestriction(null, hasContractor, Contractor));
	}

	static public ObjectProperty hasMainContractor = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasMainContractor");
	static {
		hasMainContractor.addDomain(Club.Club);
		hasMainContractor.addRange(Contractor);
		Club.Club.addSubClass(schema.createAllValuesFromRestriction(null, hasMainContractor, Contractor));
		hasContractor.addSubProperty(hasMainContractor);
		hasMainContractor.addSuperProperty(hasContractor);
	}

	static public ObjectProperty hasSubContractor = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasSubcontractor");
	static {
		hasSubContractor.addDomain(Contractor);
		hasSubContractor.addRange(Contractor);
		Contractor.addSubClass(schema.createAllValuesFromRestriction(null, hasSubContractor, Contractor));
	}

}

