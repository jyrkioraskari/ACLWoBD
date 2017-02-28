package fi.aalto.drumbeat;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class RDFConstants {
	static PropertyOperation property = (a) -> ResourceFactory.createProperty(Constants.security_ontology_base + "#"+a);
	static ResourceOperation resource = (a) -> ResourceFactory.createResource(Constants.security_ontology_base + "#"+a);
	interface PropertyOperation {
		Property create(String name);
	   }
	static private Property create(PropertyOperation operation,String name){
	      return operation.create(name);
	   }
	
	interface ResourceOperation {
		Resource create(String name);
	   }
	static private Resource create(ResourceOperation operation,String name){
	      return operation.create(name);
	   }

	

	public RDFConstants() {
	}

	// Ontolohy classes
	static public Resource Query = create(resource,"Query");
	static public Resource Response = create(resource,"Response");
	
	static public Resource DataStore = create(resource,"DataStore");
	static public Resource Organization = create(resource,"Organization");
	static public Resource Project = create(resource,"Project");
	
	static public Property property_hasTimeStamp = create(property,"hasTimeStamp");

	static public Property property_hasWebID = create(property,"hasWebID");
	static public Property property_hasName = create(property,"hasName");
	static public Property property_hasPublicKey = create(property,"hasPublicKey");
	
	static public Property property_hasCollection = create(property,"hasCollection");
	static public Property property_hasDataSource = create(property,"hasDataSource");
	static public Property property_hasDataSet = create(property,"hasDataSet");
	

	static public Property property_hasAuthorizationRule = create(property,"hasAuthorizationRule");
	static public Property property_hasPermission = create(property,"hasPermission");
	static public Property property_hasRulePath = create(property,"hasRulePath");
	static public Property property_hasPath = create(property,"hasPath");
	

	static public Property property_hasProject = create(property,"hasProject");
	static public Property property_hasMainContractor =create(property,"hasMainContractor");
	static public Property property_hasContractor = create(property,"hasContractor");
	static public Property property_knowsPerson = create(property,"knowsPerson");
	static public Property property_status = create(property,"status");
	static public Property property_information = create(property,"information");
	
	
}
