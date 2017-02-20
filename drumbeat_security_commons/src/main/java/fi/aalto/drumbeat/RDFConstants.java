package fi.aalto.drumbeat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class RDFConstants {
	private Model model;
	
	public RDFConstants(Model model)
	{
		this.model=model;
	}
	
	public Resource query(){ return model.getResource(Constants.security_ontology_base+"#Query");}
	public Resource Response(){ return model.getResource(Constants.security_ontology_base+"#Response");}
	
    public Properties property=new Properties();
    public class Properties
    {
		public Property knowsPerson(){ return model.getProperty(Constants.security_ontology_base + "#knowsPerson");}
		public Property hasRulePath(){ return model.getProperty(Constants.security_ontology_base + "#hasRulePath");}
		public Property hasTimeStamp(){ return model.getProperty(Constants.security_ontology_base + "#hasTimeStamp");}
		public Property hasWebID(){ return model.getProperty(Constants.security_ontology_base + "#hasWebID");}
    }
    
}
