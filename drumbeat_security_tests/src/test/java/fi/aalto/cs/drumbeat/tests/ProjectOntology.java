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
import fi.aalto.drumbeat.ontology.Ontology.LBD;

//TODO try this: http://vowl.visualdataweb.org/webvowl.html
public class ProjectOntology {

	ProjectOntology() {

		OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		schema.setNsPrefix("ds", "https://drumbeat.cs.hut.fi/owl/security.ttl#");

		OntClass ProtectedResource = schema.createClass(Constants.security_ontology_base + "#ProtectedResource");
		OntClass AuthorizationRule = schema.createClass(Constants.security_ontology_base + "#AuthorizationRule");
		OntClass RulePath = schema.createClass(Constants.security_ontology_base + "#RulePath");

		OntClass PermissionRole = schema.createClass(Constants.security_ontology_base + "#PermissionRole");
		Individual create = schema.createIndividual(Constants.security_ontology_base + "#CREATE", PermissionRole);
		Individual read = schema.createIndividual(Constants.security_ontology_base + "#READ", PermissionRole);
		Individual update = schema.createIndividual(Constants.security_ontology_base + "#UPDATE", PermissionRole);
		Individual delete = schema.createIndividual(Constants.security_ontology_base + "#DELETE", PermissionRole);
		RDFList enums = schema.createList();
		enums = enums.cons(create);
		enums = enums.cons(read);
		enums = enums.cons(update);
		enums = enums.cons(delete);

		OntClass PermittedRole = schema.createEnumeratedClass(Constants.security_ontology_base + "#PermittedRole",
				enums);
		ProtectedResource.addSubClass(LBD.Collection);
		LBD.Collection.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(LBD.DataSource);
		LBD.DataSource.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(LBD.DataSet);
		LBD.DataSet.addSuperClass(ProtectedResource);

		ObjectProperty hasAuthorizationRule = schema
				.createObjectProperty(Constants.security_ontology_base + "#authorization");
		hasAuthorizationRule.addDomain(ProtectedResource);
		hasAuthorizationRule.addRange(AuthorizationRule);

		ObjectProperty hasPermittedRole = schema
				.createObjectProperty(Constants.security_ontology_base + "#permittedRole");
		hasPermittedRole.addDomain(AuthorizationRule);
		hasPermittedRole.addRange(PermittedRole);

		ObjectProperty hasRulePath = schema.createObjectProperty(Constants.security_ontology_base + "#rulePath");

		hasRulePath.addDomain(AuthorizationRule);
		hasRulePath.addRange(RulePath);

		OntClass ListNode = schema.createClass(Constants.security_ontology_base + "#ListNode");
		ObjectProperty first = schema.createObjectProperty(Constants.security_ontology_base + "#first");

		first.addDomain(ListNode);
		first.addRange(RDF.Property);

		ObjectProperty rest = schema.createObjectProperty(Constants.security_ontology_base + "#rest");

		rest.addDomain(RulePath);
		rest.addDomain(ListNode);
		rest.addRange(ListNode);

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
		

		// @formatter:on
	}

	public static void main(String[] args) {
		new ProjectOntology();
	}
}
