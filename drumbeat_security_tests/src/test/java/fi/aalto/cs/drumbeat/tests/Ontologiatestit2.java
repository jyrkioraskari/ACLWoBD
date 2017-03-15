package fi.aalto.cs.drumbeat.tests;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.ontology.Ontology;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class Ontologiatestit2 {
	
	public Ontologiatestit2() {
		String NS= Constants.security_ontology_base;
		OntModel schema = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		

		
		
		OntClass Occupation = schema.createClass( NS + "#Occupation" );
		OntClass Project = schema.createClass( NS + "#Project" );
		OntClass Contractor = schema.createClass( NS + "#Contractor" );
		
		//Occupation.addSubClass(Project);
		//Project.addSuperClass(Occupation);
		
		
		ObjectProperty hasContractor = schema.createObjectProperty( NS + "#hasContractor" );
		hasContractor.addDomain( Occupation );
		hasContractor.addRange( Contractor );
		//Occupation.addSubClass( schema.createAllValuesFromRestriction( null, hasContractor, Contractor ));
		
		ObjectProperty hasSubContractor = schema.createObjectProperty( NS + "#hasSubcontractor" );
		hasSubContractor.addDomain( Contractor );
		hasSubContractor.addRange( Contractor );
		
		//Contractor.addSubClass( schema.createAllValuesFromRestriction( null, hasSubContractor, Contractor ));
		
		ObjectProperty hasMainContractor = schema.createObjectProperty( NS + "#hasMainContractor" );
		hasMainContractor.addDomain( Occupation );
		hasMainContractor.addRange( Contractor );
		
		//Occupation.addSubClass( schema.createAllValuesFromRestriction( null, hasMainContractor, Contractor ));
		
		hasContractor.addSubProperty( hasMainContractor );
		hasMainContractor.addSuperProperty( hasContractor );
		
		Individual occupation = schema.createIndividual( null, Occupation );
		Individual maincontractor= schema.createIndividual( null, Contractor );
		occupation.addProperty(hasMainContractor, maincontractor);	
		StmtIterator iter=occupation.listProperties();
		System.out.println("starts");
		while(iter.hasNext()) {
			Statement s=iter.next();
			System.out.println(s);
		}
		System.out.println("--");
		Ontology.getSchema().write(System.out);
		
	}

	public static void main(String[] args) {
		new Ontologiatestit2();
	}
}
