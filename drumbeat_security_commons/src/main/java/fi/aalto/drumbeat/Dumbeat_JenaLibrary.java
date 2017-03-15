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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.drumbeat.ontology.Ontology;
import fi.aalto.drumbeat.ontology.Ontology.Club;
import fi.aalto.drumbeat.ontology.Ontology.Contractor;

public class Dumbeat_JenaLibrary {


	static public LinkedList<Resource> parseRulePath(Model model,Resource node) {
		LinkedList<Resource> ret = new LinkedList<Resource>();
		Resource current = node;
		while (current != null && current.asResource().hasProperty(Ontology.Authorization.rest)) {
			if (current.hasProperty(Ontology.Authorization.first))
				ret.add(current.getPropertyResourceValue(Ontology.Authorization.first));
			current = current.getPropertyResourceValue(Ontology.Authorization.rest);
		}
		return ret;
	}

	static public Resource createRulePath(OntModel model,List<String> lista) {
		Individual rule_path = model.createIndividual(null, Ontology.Authorization.RulePath);

		Individual current = rule_path;
		for (String ps : lista) {
			ObjectProperty p = model
					.createObjectProperty(ps);
			Individual node = model.createIndividual(null, Ontology.Authorization.ListNode);
			current.addProperty(Ontology.Authorization.rest, node);
			current.addProperty(Ontology.Authorization.first, p);
			current = node;
		}

		return rule_path.asResource();
	}
	


	static public List<RDFNode> getPermissions(Model model,String uri) {
		List<RDFNode> ret = new ArrayList<RDFNode>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?p WHERE {");
		sb.append(" <" + uri + ">  <" + Ontology.Authorization.hasAuthorizationRule.getURI() + "> ?x .");
		sb.append(" ?x  <" + Ontology.Authorization.hasPermittedRole.getURI() + "> ?p .");
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

	static public void match(Model model,List<RDFNode> ret, String request_url) {
		System.out.println("etsitty: " + request_url);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?path WHERE {");
		sb.append(" ?path  <" + Ontology.Authorization.hasAuthorizationRule.getURI() + "> ?x");
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
		
		Individual musiikkitalo = model.createIndividual(rootURI.toString()+"musiikkitalo", Ontology.Authorization.ProtectedResource);
		Individual musiikkitalo_authorizationRule = model.createIndividual(null, Ontology.Authorization.AuthorizationRule);
		musiikkitalo.addProperty(Ontology.Authorization.hasAuthorizationRule, musiikkitalo_authorizationRule);
		
		List<String> lista=new ArrayList<>();
		lista.add(Club.hasClub.toString());
		lista.add(Contractor.hasMainContractor.toString());
		lista.add(Contractor.trusts.toString());
		Resource rlista=Dumbeat_JenaLibrary.createRulePath(model,lista);
		musiikkitalo_authorizationRule.addProperty(Ontology.Authorization.hasRulePath, rlista);
		musiikkitalo_authorizationRule.addProperty(Ontology.Authorization.hasPermittedRole, Ontology.Authorization.read);
		
		Individual occupation1 = model.createIndividual(null, Club.Club);
		// HTTP since local virtual hosts need a new configuration
		Individual main_contractor = model.createIndividual("http://fabricator.local.org/", Contractor.Contractor);
		musiikkitalo.addProperty(Club.hasClub, occupation1);
		occupation1.addProperty(Contractor.hasContractor, main_contractor);
		//this.model.write(System.out,"TURTLE");

		//Me
		Individual company=model.createIndividual(rootURI, Contractor.Contractor);
		Individual test_person = model.createIndividual("https://jyrkio2.databox.me/profile/card#me", Contractor.Person);
		company.addProperty(Contractor.trusts, test_person);
		
		
	}
}