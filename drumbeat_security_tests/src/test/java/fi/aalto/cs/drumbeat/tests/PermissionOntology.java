package fi.aalto.cs.drumbeat.tests;

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
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.ontology.Ontology.Authorization;
import fi.aalto.drumbeat.ontology.Ontology.LBD;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class PermissionOntology {

	PermissionOntology() {

		OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		schema.setNsPrefix("ds", "https://drumbeat.cs.hut.fi/owl/security.ttl#");

		OntClass ProtectedResource = schema.createClass(Constants.security_ontology_base + "#ProtectedResource");
		OntClass ACL = schema.createClass(Constants.security_ontology_base + "#ACL");
		OntClass RolePath = schema.createClass(Constants.security_ontology_base + "#RolePath");

		OntClass PermissionRole = schema.createClass(Constants.security_ontology_base + "#PermissionRole");
		Individual create = schema.createIndividual(Constants.security_ontology_base + "#CREATE", PermissionRole);
		Individual read = schema.createIndividual(Constants.security_ontology_base + "#READ", PermissionRole);
		Individual update = schema.createIndividual(Constants.security_ontology_base + "#UPDATE", PermissionRole);
		Individual delete = schema.createIndividual(Constants.security_ontology_base + "#DELETE", PermissionRole);

		EnumeratedClass  Permission = schema.createEnumeratedClass(Constants.security_ontology_base + "#Permission",
				null);
		Permission.addOneOf(create);
		Permission.addOneOf(read);
		Permission.addOneOf(update);
		Permission.addOneOf(delete);
		

		ObjectProperty hasACL = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasACL");
		hasACL.addDomain(ProtectedResource);
		hasACL.addRange(ACL);

		ObjectProperty hasPermission = schema
				.createObjectProperty(Constants.security_ontology_base + "#hasPermission");
		hasPermission.addDomain(ACL);
		hasPermission.addRange(Permission);

		ObjectProperty hasRolePath = schema.createObjectProperty(Constants.security_ontology_base + "#hasRolePath");

		hasRolePath.addDomain(ACL);
		hasRolePath.addRange(RolePath);

		OntClass ListNode = schema.createClass(Constants.security_ontology_base + "#ListNode");
		ObjectProperty first = schema.createObjectProperty(Constants.security_ontology_base + "#first");

		first.addDomain(ListNode);
		//first.addRange(RDF.Property);
		schema.createAllValuesFromRestriction(null, first, RDF.Property);

		ObjectProperty rest = schema.createObjectProperty(Constants.security_ontology_base + "#rest");

		rest.addDomain(RolePath);
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
