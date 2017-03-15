package fi.aalto.drumbeat.ontology;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Constants;

public class Authorization {
	static public OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
	static public OntClass ProtectedResource = schema
			.createClass(Constants.security_ontology_base + "#ProtectedResource");
	static public OntClass AuthorizationRule = schema
			.createClass(Constants.security_ontology_base + "#AuthorizationRule");
	static public OntClass RulePath = schema.createClass(Constants.security_ontology_base + "#RulePath");

	static public OntClass Permission = schema.createClass(Constants.security_ontology_base + "#Permission");
	static public Individual create = schema.createIndividual(Constants.security_ontology_base + "#CREATE", Permission);
	static public Individual read = schema.createIndividual(Constants.security_ontology_base + "#READ", Permission);
	static public Individual update = schema.createIndividual(Constants.security_ontology_base + "#UPDATE", Permission);
	static public Individual delete = schema.createIndividual(Constants.security_ontology_base + "#DELETE", Permission);
	static private RDFList enums = schema.createList();
	static {
		enums = enums.cons(create);
		enums = enums.cons(read);
		enums = enums.cons(update);
		enums = enums.cons(delete);
	}
	static public OntClass PermittedRole = schema
			.createEnumeratedClass(Constants.security_ontology_base + "#PermittedRole", enums);

	static {
		ProtectedResource.addSubClass(LBD.Collection);
		LBD.Collection.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(LBD.DataSource);
		LBD.DataSource.addSuperClass(ProtectedResource);
		ProtectedResource.addSubClass(LBD.DataSet);
		LBD.DataSet.addSuperClass(ProtectedResource);

	}

	static public ObjectProperty hasAuthorizationRule = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasAuthorizationRule");
	static {
		hasAuthorizationRule.addDomain(ProtectedResource);
		hasAuthorizationRule.addRange(AuthorizationRule);
	}

	static {
		ProtectedResource
				.addSubClass(schema.createAllValuesFromRestriction(null, hasAuthorizationRule, AuthorizationRule));
		ProtectedResource.addSubClass(schema.createMinCardinalityRestriction(null, hasAuthorizationRule, 0));
	}

	static public ObjectProperty hasPermittedRole = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasPermittedRole");
	static {
		hasPermittedRole.addDomain(AuthorizationRule);
		hasPermittedRole.addRange(PermittedRole);
		AuthorizationRule.addSubClass(schema.createMinCardinalityRestriction(null, hasPermittedRole, 1));
		AuthorizationRule.addSubClass(schema.createAllValuesFromRestriction(null, hasPermittedRole, PermittedRole));
	}

	static public ObjectProperty hasRulePath = schema
			.createObjectProperty(Constants.security_ontology_base + "#hasRulePath");
	static {
		hasRulePath.addDomain(AuthorizationRule);
		hasRulePath.addRange(RulePath);

		AuthorizationRule.addSubClass(schema.createMinCardinalityRestriction(null, hasRulePath, 1));
		AuthorizationRule.addSubClass(schema.createAllValuesFromRestriction(null, hasRulePath, RulePath));
		

	}

	static public OntClass ListNode = schema.createClass(Constants.security_ontology_base + "#ListNode");
	static public ObjectProperty first = schema.createObjectProperty(Constants.security_ontology_base + "#first");
	static {
		first.addDomain(ListNode);
		first.addRange(RDF.Property);
		ListNode.addSubClass(schema.createAllValuesFromRestriction(null, first, RDF.Property));
		ListNode.addSubClass(schema.createCardinalityRestriction(null, first, 1));

	}

	static public ObjectProperty rest = schema.createObjectProperty(Constants.security_ontology_base + "#rest");
	static {
		rest.addDomain(RulePath);
		rest.addDomain(ListNode);
		rest.addRange(ListNode);

		ListNode.addSubClass(schema.createAllValuesFromRestriction(null, rest, ListNode));
		ListNode.addSubClass(schema.createMinCardinalityRestriction(null, rest, 0));
		ListNode.addSubClass(schema.createMaxCardinalityRestriction(null, rest, 1));
	}
	static {

	}
}
