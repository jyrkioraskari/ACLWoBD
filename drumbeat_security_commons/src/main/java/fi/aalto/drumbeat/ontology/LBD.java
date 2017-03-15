package fi.aalto.drumbeat.ontology;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

import fi.aalto.drumbeat.Constants;

public class LBD {
	static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	static public OntClass DataSpaceNode = schema.createClass(Constants.security_ontology_base + "#DataSpaceNode");
	static public OntClass Collection = schema.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#Collection");
	static public OntClass DataSource = schema.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSource");
	static public OntClass DataSet = schema.createClass("http://drumbeat.cs.hut.fi/owl/lbdho.ttl#DataSet");

	static public ObjectProperty hasCollection = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasCollection");
	static {
		hasCollection.addDomain(DataSpaceNode);
		hasCollection.addRange(Collection);
		DataSpaceNode.addSubClass(schema.createAllValuesFromRestriction(null, hasCollection, Collection));
	}

	static public ObjectProperty hasDataSource = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasDataSource");
	static {
		hasDataSource.addDomain(Collection);
		hasDataSource.addRange(DataSource);

		Collection.addSubClass(schema.createAllValuesFromRestriction(null, hasDataSource, DataSource));
	}

	static public ObjectProperty hasDataSet = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasDataSet");
	static {
		hasDataSet.addDomain(DataSource);
		hasDataSet.addRange(DataSet);
	}
}