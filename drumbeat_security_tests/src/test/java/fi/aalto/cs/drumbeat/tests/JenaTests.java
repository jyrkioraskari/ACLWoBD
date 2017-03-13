package fi.aalto.cs.drumbeat.tests;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Constants;
import fi.aalto.drumbeat.RDFConstants;
import junit.framework.TestCase;

public class JenaTests extends TestCase {

	final private Model model = ModelFactory.createDefaultModel();

	public JenaTests() {
		super();
	}

	public void test_JenaReadWrite() {
		Property knowsPerson = model.getProperty(Constants.security_ontology_base + "#knowsPerson");
		Resource query_resource = model.getResource(Constants.security_ontology_base + "#CheckUser_query");
		RDFNode[] rulepath_list = new RDFNode[1];
		rulepath_list[0] = knowsPerson;
		RDFList rulepath = model.createList(rulepath_list);
		Property hasPath = model.getProperty(Constants.security_ontology_base + "#hasRulePath");
		query_resource.addProperty(hasPath, rulepath);
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

		ResIterator iter = input_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
		Resource query = null;
		if (iter.hasNext())
			query = iter.next();
		else
			return;
		System.out.println(query.hasProperty(RDFConstants.property_hasTimeStamp));

		RDFNode ts = query.getProperty(RDFConstants.property_hasTimeStamp).getObject();
		System.out.println(ts);
		Resource response = output_model.createResource();
		response.addProperty(RDF.type, RDFConstants.SecurityResponse);
		response.addLiteral(RDFConstants.property_hasTimeStamp, ts);

		System.out.println(writeModel(output_model));
	}

	public void test_checkUser_parameters() {
		Model model = ModelFactory.createDefaultModel();
		String webid = "http://user.com/user#me";

		RDFNode[] rulepath_list = new RDFNode[1];
		rulepath_list[0] = RDFConstants.property_trusts;
		RDFList rulepath = model.createList(rulepath_list);
		Resource query = model.createResource();
		query.addProperty(RDFConstants.property_hasRulePath, rulepath);

		Literal time_inMilliseconds = model.createTypedLiteral(new Long(System.currentTimeMillis()));
		query.addProperty(RDF.type, RDFConstants.SecurityQuery);
		query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
		query.addProperty(RDFConstants.property_hasWebID, model.getResource(webid));

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