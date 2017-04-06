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
import fi.aalto.drumbeat.ontology.Ontology.Authorization;
import fi.aalto.drumbeat.ontology.Ontology.LBD;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class PermissionOntology {

	PermissionOntology() {

		OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		schema.setNsPrefix("ds", "https://drumbeat.cs.hut.fi/owl/security.ttl#");
		schema.setNsPrefix("acl", "http://www.w3.org/ns/auth/acl#");
		schema.read("c://jo/ontology/acl.rdf");

		OntClass ProtectedResource = schema.createClass(Constants.security_ontology_base + "#ProtectedResource");
		OntClass ACL = schema.createClass(Constants.security_ontology_base + "#ACL");

		ObjectProperty hasACL = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasACL");
		hasACL.addDomain(ProtectedResource);
		hasACL.addRange(ACL);

		ObjectProperty hasACLMode = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasACLMode");
		hasACLMode.addDomain(ACL);
		Resource access=schema.createResource("http://www.w3.org/ns/auth/acl#Access");
		hasACLMode.addRange(access);

		ObjectProperty hasRolePath = schema.createObjectProperty(Constants.security_ontology_base + "#hasRolePath");

		hasRolePath.addDomain(ACL);
		OntClass ListNode = schema.createClass(Constants.security_ontology_base + "#ListNode");
		hasRolePath.addRange(ListNode);

		ObjectProperty first = schema.createObjectProperty(Constants.security_ontology_base + "#first");

		first.addDomain(ListNode);
		//first.addRange(RDF.Property);
		schema.createAllValuesFromRestriction(null, first, RDF.Property);

		ObjectProperty rest = schema.createObjectProperty(Constants.security_ontology_base + "#rest");

		rest.addDomain(ListNode);
		rest.addRange(ListNode);

		OntClass Club = schema.createClass(Constants.security_ontology_base + "#Club");
		OntClass Project = schema.createClass(Constants.security_ontology_base + "#Project");

		Club.addSubClass(Project);
		Project.addSuperClass(Club);
		ObjectProperty hasClub = schema.createObjectProperty(Constants.security_ontology_base + "#hasClub");
		hasClub.addDomain(Authorization.ProtectedResource);
		hasClub.addRange(Club);

		ObjectProperty hasProject = schema.createObjectProperty(Constants.security_ontology_base + "#hasProject");
		hasProject.addDomain(Authorization.ProtectedResource);
		hasProject.addRange(Project);

		hasProject.addSuperProperty(hasClub);
		hasClub.addSubProperty(hasProject);

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
