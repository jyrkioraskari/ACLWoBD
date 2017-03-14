package fi.aalto.drumbeat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;

public class RDFDataStore {
	private final URI rootURI;
	private final String rdf_filename;

	final private OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	public RDFDataStore(URI rootURI, String type) {
		super();
		this.rootURI = rootURI;
		if (rootURI.getHost() == null)
			rdf_filename = Constants.RDF_filePath + "localhost_" + type + "_securitydata.ttl";
		else
			rdf_filename = Constants.RDF_filePath + rootURI.getHost() + "_" + type + "_securitydata.ttl";
		createDemoData();
	}

	private Resource getRoot() {
		return model.getResource(rootURI.toString());
	}

	public List<RDFNode> getData(String property) {
		List<RDFNode> ret = new ArrayList<RDFNode>();
		Property p = model.getProperty(Constants.security_ontology_base + property);
		System.out.println("root:" + getRoot().getURI());
		System.out.println("property:" + p.getURI());

		StmtIterator it = getRoot().listProperties(p);
		while (it.hasNext()) {
			Statement stmt = it.nextStatement();
			ret.add(stmt.getObject());
		}
		return ret;
	}

	public RDFNode getData(RDFNode node, String property) {
		Property p = model.getProperty(Constants.security_ontology_base + property);
		if (node.isResource()) {
			return node.asResource().getProperty(p).getObject();
		} else
			return null;
	}

	public void saveRDFData() {
		try {
			FileOutputStream fout = new FileOutputStream(this.rdf_filename);
			RDFDataMgr.write(fout, model, Lang.TURTLE);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readRDFData() {

		InputStream in = FileManager.get().open(this.rdf_filename);
		if (in == null) {
			createDemoData();
			return; // nonexistent!
		}

		RDFDataMgr.read(model, in, Lang.TURTLE);
	}

	private void createDemoData() {
		Individual musiikkitalo = this.model.createIndividual(rootURI.toString()+"/protected/musiikkitalo", RDFOntology.Authorization.ProtectedResource);
		Individual musiikkitalo_authorizationRule = this.model.createIndividual(null, RDFOntology.Authorization.AuthorizationRule);
		musiikkitalo.addProperty(RDFOntology.Authorization.hasAuthorizationRule, musiikkitalo_authorizationRule);
		
		List<Resource> lista=new ArrayList<>();
		lista.add(RDFOntology.Occupation.hasOccupation);
		lista.add(RDFOntology.Contractor.hasMainContractor);
		lista.add(RDFOntology.Contractor.trusts);
		Resource rlista=createRulePath(lista);
		musiikkitalo_authorizationRule.addProperty(RDFOntology.Authorization.hasRulePath, rlista);
		musiikkitalo_authorizationRule.addProperty(RDFOntology.Authorization.hasPermittedRole, RDFOntology.Authorization.read);
		
		Individual occupation1 = this.model.createIndividual(null, RDFOntology.Occupation.Occupation);
		Individual main_contractor = this.model.createIndividual("https://fabricator.local.org/", RDFOntology.Contractor.Contractor);
		musiikkitalo.addProperty(RDFOntology.Occupation.hasOccupation, occupation1);
		occupation1.addProperty(RDFOntology.Contractor.hasMainContractor, main_contractor);
		//this.model.write(System.out,"TURTLE");
	}

	public void match(List<RDFNode> ret, String request_url) {
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

	public List<RDFNode> getPermissions(String uri) {
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

	public Resource createRulePath(List<Resource> lista) {
		Individual rule_path = this.model.createIndividual(null, RDFOntology.Authorization.RulePath);

		Individual current = rule_path;
		for (Resource p : lista) {
			Individual node = this.model.createIndividual(null, RDFOntology.Authorization.ListNode);
			current.addProperty(RDFOntology.Authorization.rest, node);
			current.addProperty(RDFOntology.Authorization.first, p);
			current = node;
		}

		return rule_path.asResource();
	}
	

	public LinkedList<Resource> parseRulePath(Resource node) {
		LinkedList<Resource> ret = new LinkedList<Resource>();
		Resource current = node;
		while (current != null && current.asResource().hasProperty(RDFOntology.Authorization.rest)) {
			if (current.hasProperty(RDFOntology.Authorization.first))
				ret.add(current.getPropertyResourceValue(RDFOntology.Authorization.first));
			current = current.getPropertyResourceValue(RDFOntology.Authorization.rest);
		}
		return ret;
	}

	public LinkedList<Resource> parseRulePathOrg(Resource node) {
		LinkedList<Resource> ret = new LinkedList<Resource>();

		Resource current = node;
		while (current != null && current.asResource().hasProperty(RDF.rest)) {
			if (current.hasProperty(RDF.first))
				ret.add(current.getPropertyResourceValue(RDF.first));
			current = current.getPropertyResourceValue(RDF.rest);
		}

		if (ret.size() == 0) {
			// Resource
			// rr=node.getPropertyResourceValue(RDFConstants.property_hasAuthorizationRule);
			System.out.println("properties... ");
			Iterator i = node.listProperties();
			while (i.hasNext()) {
				System.out.println("property was:" + i.next().toString());
			}
		} else
			System.out.println("rule path: " + ret);

		return ret;
	}

	public Model getModel() {
		return model;
	}

}
