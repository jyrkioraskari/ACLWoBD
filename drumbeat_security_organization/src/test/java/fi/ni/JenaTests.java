package fi.ni;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.data_store_test_data.Collection;
import fi.aalto.drumbeat.data_store_test_data.DataSet;
import fi.aalto.drumbeat.data_store_test_data.DataSource;
import junit.framework.TestCase;

public class JenaTests extends TestCase {

	final private Model model = ModelFactory.createDefaultModel();

	public JenaTests() {
		super();
	}


	@Test
	public void test_JenaReadWrite() {
			Property knowsPerson = model.getProperty(Constants.security_ontology_base + "#knowsPerson");
			Resource query_resource = model.getResource(Constants.security_ontology_base+"#CheckUser_query");
			RDFNode[] rulepath_list = new RDFNode[1];
			rulepath_list[0] =   knowsPerson;
			RDFList rulepath = model.createList(rulepath_list);	
			Property hasPath = model.getProperty(Constants.security_ontology_base + "#hasRulePath");
			query_resource.addProperty(hasPath, rulepath);
			StringWriter writer = new StringWriter();
			model.write(writer, "JSON-LD");
          
			
			final Model json_input_model = ModelFactory.createDefaultModel();
			json_input_model.read(new ByteArrayInputStream(writer.toString().getBytes()), null,  "JSON-LD");
			
			
			json_input_model.write(System.out);
	}
}
