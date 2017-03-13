package fi.aalto.drumbeat;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class RDFConstants {
	static private OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	static PropertyOperation property = (a) -> ResourceFactory
			.createProperty(Constants.security_ontology_base + "#" + a);
	static ResourceOperation resource = (a) -> ResourceFactory
			.createResource(Constants.security_ontology_base + "#" + a);

	interface PropertyOperation {
		Property create(String name);
	}

	static private Property create(PropertyOperation operation, String name) {
		return operation.create(name);
	}

	interface ResourceOperation {
		Resource create(String name);
	}

	static private Resource create(ResourceOperation operation, String name) {
		return operation.create(name);
	}

	public RDFConstants() {
	}

	// Ontology classes
	static public OntClass Site = m.createClass(Constants.security_ontology_base + "#Site");
	static public OntClass Collection = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#Collection");
	static public OntClass DataSource = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSource");
	static public OntClass DataSet = m.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSet");

	static public ObjectProperty hasCollection = m
			.createObjectProperty(Constants.security_ontology_base + "#hasCollection");
	static public ObjectProperty hasDataSource = m
			.createObjectProperty(Constants.security_ontology_base + "#hasDataSource");
	static public ObjectProperty hasDataSet = m.createObjectProperty(Constants.security_ontology_base + "#hasDataSet");

	static {
		hasCollection.addDomain(Site);
		hasCollection.addRange(Collection);

		Site.addSubClass(m.createAllValuesFromRestriction(null, hasCollection, Collection));

		hasDataSource.addDomain(Collection);
		hasDataSource.addRange(DataSource);

		Collection.addSubClass(m.createAllValuesFromRestriction(null, hasDataSource, DataSource));

		hasDataSet.addDomain(DataSource);
		hasDataSet.addRange(DataSet);
	}

	static public OntClass SecurityMessage = m.createClass(Constants.security_ontology_base  + "#SecurityMessage");
	static public OntClass SecurityQuery = m.createClass(Constants.security_ontology_base  + "#SecurityQuery");
	static public OntClass SecurityResponse = m.createClass(Constants.security_ontology_base  + "#SecurityResponse");
	static {
		SecurityMessage.addSubClass(SecurityQuery);
		SecurityMessage.addSubClass(SecurityResponse);
		SecurityQuery.addSuperClass(SecurityMessage);
		SecurityResponse.addSuperClass(SecurityMessage);
	}

	static public Resource DataStore = create(resource, "DataStore");
	static public OntClass Occupation = m.createClass( Constants.security_ontology_base + "#Occupation" );
	static public OntClass Project = m.createClass(Constants.security_ontology_base + "#Project" );
     
	static {
		Occupation.addSubClass(Project);
		Project.addSuperClass(Occupation);
	}
	static public OntClass Contractor = m.createClass( Constants.security_ontology_base + "#Contractor" );


	static public Property property_hasTimeStamp = create(property, "hasTimeStamp");

	static public Property property_hasWebID = create(property, "hasWebID");
	static public Property property_hasName = create(property, "hasName");
	static public Property property_hasPublicKey = create(property, "hasPublicKey");

	static public Property property_hasAuthorizationRule = create(property, "hasAuthorizationRule");
	static public Property property_hasPermittedRole = create(property, "hasPermittedRole");
	static public Property property_hasRulePath = create(property, "hasRulePath");
	static public Property property_hasPath = create(property, "hasPath");

	static public Property property_hasProject = create(property, "hasProject");
	static public Property property_hasMainContractor = create(property, "hasMainContractor");
	static public Property property_hasContractor = create(property, "hasContractor");
	static public Property property_trusts = create(property, "knowsPerson");
	static public Property property_status = create(property, "status");
	static public Property property_information = create(property, "information");

}
