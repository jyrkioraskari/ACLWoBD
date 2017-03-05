package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.data_store_test_data.AbstractData;

public class RulePath extends AbstractData {

	public RulePath(URI root,Model model) {
		super(root, UUID.randomUUID().toString(), model);  // uudeelleenkäyttöä varten!, muuten olisi blank node
		RDFNode[] rulepath_list_contractor1 = new RDFNode[3];
		rulepath_list_contractor1[0]  =   RDFConstants.property_hasProject;
		rulepath_list_contractor1[1] =   RDFConstants.property_hasContractor;
		rulepath_list_contractor1[2] =   RDFConstants.property_knowsPerson;
		RDFList rulepath_contractor1 = model.createList(rulepath_list_contractor1);	
		
		Property hasPath =RDFConstants.property_hasPath;
		self.addProperty(hasPath, rulepath_contractor1);
		
	}

	

/*
 * RDFNode[] rulepath_list_maincontractor1 = new RDFNode[3];
		rulepath_list_maincontractor1[0] =   property.hasProject;
		rulepath_list_maincontractor1[1] =   property.hasMainContractor;
		rulepath_list_maincontractor1[2] =   property.knowsPerson;
		RDFList rulepath_maincontractor1 = model.createList(rulepath_list_maincontractor1);	
		
 */
}
