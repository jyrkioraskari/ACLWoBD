package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.drumbeat.Constants;

public class RulePath extends AbstractData {

	public RulePath(URI root,Model model) {
		super(root, UUID.randomUUID().toString(), model);  // uudeelleenkäyttöä varten!, muuten olisi blank node
		RDFProperties property = new RDFProperties();
		Resource person1 = model.getResource("http://company1/mats");
		
		RDFNode[] rulepath_list_contractor1 = new RDFNode[3];
		rulepath_list_contractor1[0]  =   property.hasProject;
		rulepath_list_contractor1[1] =   property.hasContractor;
		rulepath_list_contractor1[2] =   property.knowsPerson;
		RDFList rulepath_contractor1 = model.createList(rulepath_list_contractor1);	
		
		Property hasPath = model.getProperty(Constants.security_ontology_base + "#hasPath");
		self.addProperty(hasPath, rulepath_contractor1);
		
	}

	private class RDFProperties{
		public Property hasCollection = model.getProperty(Constants.security_ontology_base + "#hasCollection");
		public Property hasDataSource = model.getProperty(Constants.security_ontology_base + "#hasDataSource");
		public Property hasDataSet =model.getProperty(Constants.security_ontology_base + "#hasDataSet");
		
		public Property hasAuthorizationRule = model.getProperty(Constants.security_ontology_base + "#hasAuthorizationRule");
		public Property hasPermission = model.getProperty(Constants.security_ontology_base + "#Permission");
		public Property hasRulePath = model.getProperty(Constants.security_ontology_base + "#hasRulePath");


		public Property hasProject = model.getProperty(Constants.security_ontology_base + "#hasProject");
		public Property hasMainContractor = model.getProperty(Constants.security_ontology_base + "#hasMainContractor");
		public Property hasContractor = model.getProperty(Constants.security_ontology_base + "#hasContractor");
		public Property knowsPerson = model.getProperty(Constants.security_ontology_base + "#knowsPerson");
	} 
	

/*
 * RDFNode[] rulepath_list_maincontractor1 = new RDFNode[4];
		rulepath_list_maincontractor1[0] =   property.hasProject;
		rulepath_list_maincontractor1[1] =   property.hasMainContractor;
		rulepath_list_maincontractor1[2] =   property.knowsPerson;
		rulepath_list_maincontractor1[3] =   person1;
		RDFList rulepath_maincontractor1 = model.createList(rulepath_list_maincontractor1);	
		
 */
}
