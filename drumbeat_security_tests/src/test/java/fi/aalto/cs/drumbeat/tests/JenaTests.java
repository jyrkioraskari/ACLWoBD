package fi.aalto.cs.drumbeat.tests;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.ontology.Ontology;
import junit.framework.TestCase;

public class JenaTests extends TestCase {

	final private OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	public JenaTests() {
		super();
	}
	public void test_JenaReadWrite() {
		RDFDataStore store=null;
		try {
			store = new RDFDataStore(new URI("https://test.org"), "datastore");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		assertNotNull("RDFDataStore store should not be null", store);
		List<String> lista=new ArrayList<>();
		lista.add(Ontology.Contractor.trusts.toString());
		Resource rulepath=Dumbeat_JenaLibrary.createRulePath(store.getModel(),lista);
		
		Individual query_resource = this.model.createIndividual(null, Ontology.Message.SecurityQuery);
		query_resource.addProperty(Ontology.Authorization.hasRulePath, rulepath);
		StringWriter writer = new StringWriter();
		model.write(writer, "JSON-LD");

		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(writer.toString().getBytes()), null, "JSON-LD");

		json_input_model.write(System.out);
	}

	public void jenaReadWrite(String msg) {

		Model input_model = parseInput(msg);
		Model output_model = ModelFactory.createDefaultModel();
		input_model.write(System.out, "TTL");

		ResIterator iter = input_model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);
		Resource query = null;
		if (iter.hasNext())
			query = iter.next();
		else
			return;
		System.out.println(query.hasProperty(Ontology.Message.hasTimeStamp));

		RDFNode ts = query.getProperty(Ontology.Message.hasTimeStamp).getObject();
		System.out.println(ts);
		Individual response = this.model.createIndividual(null, Ontology.Message.SecurityResponse);

		response.addProperty(RDF.type, Ontology.Message.SecurityResponse);
		response.addLiteral(Ontology.Message.hasTimeStamp, ts);

		System.out.println(writeModel(output_model));
	}

	
	public void test_checkUser_parameters() {
		Model model = ModelFactory.createDefaultModel();
		String webid = "http://user.com/user#me";

		RDFDataStore store=null;
		try {
			store = new RDFDataStore(new URI("https://test.org"), "datastore");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		assertNotNull("RDFDataStore store should not be null", store);
		List<String> lista=new ArrayList<>();
		lista.add(Ontology.Contractor.trusts.toString());
		Resource rulepath=Dumbeat_JenaLibrary.createRulePath(store.getModel(),lista);
		
		Individual query = this.model.createIndividual(null, Ontology.Message.SecurityQuery);
		query.addProperty(Ontology.Authorization.hasRulePath, rulepath);

		Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
		query.addProperty(RDF.type, Ontology.Message.SecurityQuery);
		query.addLiteral(Ontology.Message.hasTimeStamp, time_inMilliseconds);
		query.addProperty(Ontology.Message.hasWebID, model.getResource(webid));

		StringWriter writer = new StringWriter();
		model.write(writer, "JSON-LD");
		writer.flush();

		jenaReadWrite(writer.toString());

	}

	protected Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "JSON-LD");
		return json_input_model;
	}

	protected String writeModel(Model model) {
		StringWriter writer = new StringWriter();
		model.write(writer, "JSON-LD");
		return writer.toString();
	}
}