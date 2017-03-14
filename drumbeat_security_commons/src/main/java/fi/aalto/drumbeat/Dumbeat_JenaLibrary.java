package fi.aalto.drumbeat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class Dumbeat_JenaLibrary {


	static public LinkedList<Resource> parseRulePath(Model model,Resource node) {
		LinkedList<Resource> ret = new LinkedList<Resource>();
		Resource current = node;
		while (current != null && current.asResource().hasProperty(RDFOntology.Authorization.rest)) {
			if (current.hasProperty(RDFOntology.Authorization.first))
				ret.add(current.getPropertyResourceValue(RDFOntology.Authorization.first));
			current = current.getPropertyResourceValue(RDFOntology.Authorization.rest);
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
		System.out.println(ret.stream().map(x->x.toString()).collect(Collectors.joining(",")));
		return ret;

	}

	static public void match(OntModel model,List<RDFNode> ret, String request_url) {
		System.out.println("etsitty: " + request_url);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?path WHERE {");
		sb.append(" ?path  <" + RDFOntology.Authorization.hasAuthorizationRule.getURI() + "> ?x");
		sb.append("}");
		Query query = QueryFactory.create(sb.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("path");
				System.out.println("path: " + x.toString());
				if (request_url.startsWith(x.toString()))
					ret.add(x);
			}
		}
	}

	static public  void createDemoData(OntModel model,String rootURI) {
		
		Individual musiikkitalo = model.createIndividual(rootURI.toString()+"musiikkitalo", RDFOntology.Authorization.ProtectedResource);
		Individual musiikkitalo_authorizationRule = model.createIndividual(null, RDFOntology.Authorization.AuthorizationRule);
		musiikkitalo.addProperty(RDFOntology.Authorization.hasAuthorizationRule, musiikkitalo_authorizationRule);
		
		List<String> lista=new ArrayList<>();
		lista.add(RDFOntology.Occupation.hasOccupation.toString());
		lista.add(RDFOntology.Contractor.hasMainContractor.toString());
		lista.add(RDFOntology.Contractor.trusts.toString());
		Resource rlista=Dumbeat_JenaLibrary.createRulePath(model,lista);
		musiikkitalo_authorizationRule.addProperty(RDFOntology.Authorization.hasRulePath, rlista);
		musiikkitalo_authorizationRule.addProperty(RDFOntology.Authorization.hasPermittedRole, RDFOntology.Authorization.read);
		
		Individual occupation1 = model.createIndividual(null, RDFOntology.Occupation.Occupation);
		// HTTP since local virtual hosts need a new configuration
		Individual main_contractor = model.createIndividual("http://fabricator.local.org/", RDFOntology.Contractor.Contractor);
		musiikkitalo.addProperty(RDFOntology.Occupation.hasOccupation, occupation1);
		occupation1.addProperty(RDFOntology.Contractor.hasMainContractor, main_contractor);
		//this.model.write(System.out,"TURTLE");

		//Me
		Individual company=model.createIndividual(rootURI, RDFOntology.Contractor.Contractor);
		Individual test_person = model.createIndividual("https://jyrkio2.databox.me/profile/card#me", RDFOntology.Contractor.Person);
		company.addProperty(RDFOntology.Contractor.trusts, test_person);
		
		
	}
}
