package fi.aalto.drumbeat.ontology;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.vocabulary.XSD;

import fi.aalto.drumbeat.Constants;

public class Message {
	static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	static public OntClass SecurityMessage = schema.createClass(Constants.security_ontology_base + "#SecurityMessage");
	static public OntClass SecurityQuery = schema.createClass(Constants.security_ontology_base + "#SecurityQuery");
	static public OntClass SecurityResponse = schema.createClass(Constants.security_ontology_base + "#SecurityResponse");
	static {
		SecurityMessage.addSubClass(SecurityQuery);
		SecurityMessage.addSubClass(SecurityResponse);
		SecurityQuery.addSuperClass(SecurityMessage);
		SecurityResponse.addSuperClass(SecurityMessage);
	}
	static public DatatypeProperty hasTimeStamp = schema
			.createDatatypeProperty(Constants.security_ontology_base + "#hasTimeStamp");
	static {
		hasTimeStamp.addDomain(SecurityMessage);
		hasTimeStamp.addRange(XSD.dateTime);
	}

	static public OntClass Status = schema.createClass(Constants.security_ontology_base + "#Status");
	static public Individual accepted = schema.createIndividual(Constants.security_ontology_base + "#ACCEPTED", Status);
	static public Individual denied = schema.createIndividual(Constants.security_ontology_base + "#DENIED", Status);

	static private RDFList status_enums = schema.createList();
	static {
		status_enums = status_enums.cons(accepted);
		status_enums = status_enums.cons(denied);
	}

	static public OntClass PermissionStatus = schema
			.createEnumeratedClass(Constants.security_ontology_base + "#PermissionStatus", status_enums);
	
	static public ObjectProperty hasPermissionStatus = schema.createObjectProperty(Constants.security_ontology_base  + "#hasPermissionStatus" );
	static {
		hasPermissionStatus.addDomain( SecurityResponse );
		hasPermissionStatus.addRange( PermissionStatus);
		
		SecurityResponse.addSubClass( schema.createAllValuesFromRestriction( null, hasPermissionStatus, PermissionStatus ));
	}
	
	static {
		Authorization.hasRulePath.addDomain( SecurityQuery );
		SecurityQuery.addSubClass( schema.createAllValuesFromRestriction( null, Authorization.hasRulePath, Authorization.RulePath));

	}
	
	static public ObjectProperty hasWebID = schema.createObjectProperty( Constants.security_ontology_base + "#hasWebID" );
	static {
		hasWebID.addDomain( SecurityQuery );
		hasWebID.addRange( Contractor.Person );
		SecurityQuery.addSubClass( schema.createAllValuesFromRestriction( null, hasWebID, Contractor.Person));
		
	}
	
	static public DatatypeProperty hasMessage = schema
			.createDatatypeProperty(Constants.security_ontology_base + "#hasMessage");
	static {
		hasMessage.addDomain(SecurityResponse);
		hasMessage.addRange(XSD.xstring);
	}

}
