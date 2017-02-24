package fi.ni;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.data_store_test_data.Collection;
import fi.aalto.drumbeat.data_store_test_data.DataSet;
import fi.aalto.drumbeat.data_store_test_data.DataSource;
import junit.framework.TestCase;

public class JenaTests extends TestCase {

	final private Model model = ModelFactory.createDefaultModel();

	public JenaTests() {
		super();
		try {
			Collection c_smc = new Collection(new URI("https://architectural.drb.cs.hut.fi/security/"), "turva", model);
			c_smc.addProject("fix1");
			c_smc.addRule("contractors_allowed_read");

			Collection c = new Collection(new URI("https://architectural.drb.cs.hut.fi/security/"), "smc2", model);
			c.addProject("project1");
			c.addRule("maincontractor_allowed_read");

			DataSource ds = c.addDataSource("architectural");
			ds.addRule("rule2");

			DataSet dset = ds.addDataSet("20151125");
			dset.addRule("rule32");

			// model.write(System.out,"TURTLE");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	
	public void test_JenaListPaths() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?path WHERE {");
		sb.append(" ?path  <" + RDFConstants.property_hasAuthorizationRule.getURI() + "> ?x");
		sb.append("}");
		Query query = QueryFactory.create(sb.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("path");
				assertNotNull(x);
				//System.out.println(""+x);
			}
		}
	}

	public void test_FetchByURI() {
		Resource root = model.getResource("https://architectural.drb.cs.hut.fi/security");
		Resource collection = model.getResource(root.toString() + "/turva");
		assertNotNull(collection);
		RDFNode projekti = collection.getProperty(RDFConstants.property_hasProject).getObject();
		assertEquals(projekti.asResource().getProperty(RDF.type).getObject().toString(),
				"https://drumbeat.cs.hut.fi/owl/security.ttl#Project");
	}

	private Resource fetchByURI(String uri) {
		Resource node = model.getResource(uri);
		return node;
	}


	public List<RDFNode> match(String request_url) {
		List<RDFNode> ret = new ArrayList<RDFNode>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?path WHERE {");
		sb.append(" ?path  <" + RDFConstants.property_hasAuthorizationRule.getURI() + "> ?x");
		sb.append("}");
		Query query = QueryFactory.create(sb.toString());
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("path");
				if (request_url.startsWith(x.toString()))
					ret.add(x);
			}
		}
		return ret;
	}

	public void test_urlMatch() {
		String test_url = "https://architectural.drb.cs.hut.fi/security/smc2/architectural/3A248E14-4504-4891-902B-5E9216C64AB9";
		System.out.println(match(test_url));
		assertEquals(2,match(test_url).size());
	}

	public List<RDFNode> getAssocatedPermissions(String uri) {
		List<RDFNode> ret = new ArrayList<RDFNode>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ?p WHERE {");
		sb.append(" <" + uri + ">  <" + RDFConstants.property_hasAuthorizationRule.getURI() + "> ?x .");
		sb.append(" ?x  <" + RDFConstants.property_hasPermission.getURI() + "> ?p .");
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

	
	public void test_getAssocatedPermissions() {
		String test_url = "https://architectural.drb.cs.hut.fi/security/smc2/architectural";
		assertTrue(!getAssocatedPermissions(test_url).isEmpty());
		assertEquals("https://drumbeat.cs.hut.fi/security/Permission/READ",getAssocatedPermissions(test_url).get(0).toString());
	}
	
	
	public List<Resource> getAssocatedPath(String uri) {
		List<Resource> ret = new ArrayList<Resource>();
		Resource node = model.getResource(uri);
		Resource rule=node.getPropertyResourceValue(RDFConstants.property_hasAuthorizationRule);
		if(rule==null)
			return ret;
		Resource rule_path=rule.getPropertyResourceValue(RDFConstants.property_hasRulePath);
		if(rule_path==null)
			return ret;
		Resource path=rule_path.getPropertyResourceValue(RDFConstants.property_hasPath);
		if(path==null)
			return ret;
		
		Resource current=path;
		while(current!=null && current.asResource().hasProperty(RDF.rest))
		{
			if(current.hasProperty(RDF.first))
					ret.add(current.getPropertyResourceValue(RDF.first));
			current=current.getPropertyResourceValue(RDF.rest);
		}
		
		return ret;
	}


	public void test_getAssocateRulePath() {
		String test_url = "https://architectural.drb.cs.hut.fi/security/smc2/architectural";
		assertEquals(getAssocatedPath(test_url).toString(),"[https://drumbeat.cs.hut.fi/owl/security.ttl#hasProject, https://drumbeat.cs.hut.fi/owl/security.ttl#hasContractor, https://drumbeat.cs.hut.fi/owl/security.ttl#knowsPerson]");
	}
}
