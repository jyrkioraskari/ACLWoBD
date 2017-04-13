package fi.aalto.cs.drumbeat.tests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.ontology.EnumeratedClass;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.ontology.Ontology;
import fi.aalto.drumbeat.ontology.Ontology.Authorization;
import fi.aalto.drumbeat.ontology.Ontology.LBD;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class PermissionOntology {

	PermissionOntology() {

		OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		schema.setNsPrefix("", "https://drumbeat.cs.hut.fi/owl/security.ttl#");
		schema.setNsPrefix("drumbeat", "http://drumbeat.cs.hut.fi/owl/lbdho.ttl#");
		schema.setNsPrefix("acl", "http://www.w3.org/ns/auth/acl#");
		
		//schema.read("c://jo/ontology/acl.rdf");

		schema.add(Ontology.getSchema());

		// @formatter:off

		FileWriter out = null;
		try {

			schema.write(System.out, "Turtle");
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

		try {
			out = new FileWriter("c:/jo/ontology/drumbeat_security.ttl");
			schema.write(out, "Turtle");
		} catch (IOException e) {
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
		new PermissionOntology();
	}
}
