package fi.aalto.cs.drumbeat.tests;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.ontology.Ontology.Authorization;
import fi.aalto.drumbeat.ontology.Ontology.LBD;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class Demoaineisto {

	Demoaineisto() {

		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		model.setNsPrefix("ds", "https://drumbeat.cs.hut.fi/owl/security.ttl#");
		Dumbeat_JenaLibrary.createDemoData(model, "http://architect.demo.org/");
		// @formatter:off

		FileWriter out = null;
		try {

			model.write(System.out, "Turtle");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
				}
			}
		}

		

		// @formatter:on
	}

	public static void main(String[] args) {
		new Demoaineisto();
	}
}
