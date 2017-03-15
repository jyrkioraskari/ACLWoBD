package fi.aalto.drumbeat.ontology;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

import fi.aalto.drumbeat.Constants;

public class Ontology {
	static private OntModel schema = null;

	static PropertyOperation property = (a) -> ResourceFactory.createProperty(Constants.security_ontology_base + "#"+a);
	interface PropertyOperation {
		Property create(String name);
	   }
	static private Property create(PropertyOperation operation,String name){
	      return operation.create(name);
	   }
	 static public Property property_hasName = create(property, "hasName");
	 static public Property property_hasPublicKey = create(property,"hasPublicKey");
	 
	
	 static public OntModel getSchema()
	 {
		 if(schema==null)
		 {
			 schema=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
			 schema.add(LBD.schema); //Can be read from the net, but this is more safe
			 schema.add(Authorization.schema);
			 schema.add(Club.schema);
			 schema.add(Contractor.schema);
			 schema.add(Message.schema);
		 }
		 return schema;
	 }
	 
	 
}
