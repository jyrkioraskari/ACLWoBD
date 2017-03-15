package fi.aalto.drumbeat.ontology;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import fi.aalto.drumbeat.Constants;

//---> CLUB
public class Club {
	static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
	static public OntClass Club = schema.createClass(Constants.security_ontology_base + "#Club");
	static public OntClass Project = schema.createClass(Constants.security_ontology_base + "#Project");

	static {
		Club.addSubClass(Project);
		Project.addSuperClass(Club);
	}
	static public ObjectProperty hasClub = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasClub");
	static {
		hasClub.addDomain(Authorization.ProtectedResource);
		hasClub.addRange(Club);

		Authorization.ProtectedResource
				.addSubClass(schema.createAllValuesFromRestriction(null, hasClub, Club));
	}

	static public ObjectProperty hasProject = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasProject");
	static {
		hasProject.addDomain(Authorization.ProtectedResource);
		hasProject.addRange(Project);

		hasProject.addSuperProperty(hasClub);
		hasClub.addSubProperty(hasProject);
		Authorization.ProtectedResource.addSubClass(schema.createAllValuesFromRestriction(null, hasProject, Project));
	}

	
}
