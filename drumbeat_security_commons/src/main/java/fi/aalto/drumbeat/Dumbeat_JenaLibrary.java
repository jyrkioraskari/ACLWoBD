package fi.aalto.drumbeat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class Dumbeat_JenaLibrary {

	static public List<RDFNode> getPermissions(OntModel model,String uri) {
		List<RDFNode> ret = new ArrayList<RDFNode>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?p WHERE {");
		sb.append(" <" + uri + ">  <" + RDFOntology.Authorization.hasAuthorizationRule.getURI() + "> ?x .");
		sb.append(" ?x  <" + RDFOntology.Authorization.hasPermittedRole.getURI() + "> ?p .");
		sb.append("}");
		Query query = QueryFactory.create(sb.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("p");
				ret.add(x);
			}
		}
		return ret;

	}

	static public Resource createRulePath(OntModel model,List<String> lista) {
		Individual rule_path = model.createIndividual(null, RDFOntology.Authorization.RulePath);

		Individual current = rule_path;
		for (String ps : lista) {
			ObjectProperty p = model
					.createObjectProperty(ps);
			Individual node = model.createIndividual(null, RDFOntology.Authorization.ListNode);
			current.addProperty(RDFOntology.Authorization.rest, node);
			current.addProperty(RDFOntology.Authorization.first, p);
			current = node;
		}

		return rule_path.asResource();
	}
	

	
	static public LinkedList<Resource> parseRulePath(OntModel model,Resource node) {
		LinkedList<Resource> ret = new LinkedList<Resource>();
		Resource current = node;
		while (current != null && current.asResource().hasProperty(RDFOntology.Authorization.rest)) {
			if (current.hasProperty(RDFOntology.Authorization.first))
				ret.add(current.getPropertyResourceValue(RDFOntology.Authorization.first));
			current = current.getPropertyResourceValue(RDFOntology.Authorization.rest);
		}
		return ret;
	}

	


}
