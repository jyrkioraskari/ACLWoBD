package fi.aalto.cs.drumbeat.tests;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.ontology.Individual;
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
		//OntModel schema = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		OntModel schema = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM_RULE_INF);
		schema.add(Ontology.getSchema());

		
		Individual project = schema.createIndividual( null, Ontology.Club.Project );
		Individual maincontractor= schema.createIndividual( "http://somewhere.else", Ontology.Contractor.Contractor );
		project.addProperty(Ontology.Contractor.hasMainContractor, maincontractor);	
		StmtIterator iter=project.listProperties();
		System.out.println("---");
		while(iter.hasNext()) {
			Statement s=iter.next();
			System.out.println(s);
		}
		System.out.println("--");
		//schema.write(System.out);
		FileWriter out = null;
		try {
		  out = new FileWriter( "c:/jo/ontology/drumbeat_security.ttl" );
		  schema.write( out, "Turtle" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		  if (out != null) {
		    try {out.close();} catch (IOException ignore) {}
		  }
		}
	}

	public static void main(String[] args) {
		new Ontologiatestit2();
	}
}
