package fi.aalto.drumbeat;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

public class RDFDataStore {

	private final URI rootURI;
	private final String rdf_filename;

	final private OntModel data_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

	public RDFDataStore(URI rootURI, String type) {
		super();
		this.rootURI = rootURI;
		if (rootURI.getHost() == null)
			rdf_filename = Constants.RDF_filePath + "localhost_" + type + "_securitydata.ttl";
		else
			rdf_filename = Constants.RDF_filePath + rootURI.getHost() + "_" + type + "_securitydata.ttl";
		Dumbeat_JenaLibrary.createDemoData(data_model,rootURI.toString());
	}

	private Resource getRoot() {
		return data_model.getResource(rootURI.toString());
	}

	public List<RDFNode> getData(String property) {
		List<RDFNode> ret = new ArrayList<RDFNode>();
		Property p = data_model.getProperty(Constants.security_ontology_base + property);
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
		Property p = data_model.getProperty(Constants.security_ontology_base + property);
		if (node.isResource()) {
			return node.asResource().getProperty(p).getObject();
		} else
			return null;
	}

	public void saveRDFData() {
		try {
			FileOutputStream fout = new FileOutputStream(this.rdf_filename);
			RDFDataMgr.write(fout, data_model, Lang.TURTLE);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readRDFData() {

		InputStream in = FileManager.get().open(this.rdf_filename);
		if (in == null) {
			Dumbeat_JenaLibrary.createDemoData(data_model,rootURI.toString());
			return; // nonexistent!
		}

		RDFDataMgr.read(data_model, in, Lang.TURTLE);
	}

	

	

	public OntModel getModel() {
		return data_model;
	}
	
	public InfModel getInferenceModel() {
		Model schema = RDFOntology.getSchema();
		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);
		return ModelFactory.createInfModel(reasoner, data_model);
	}

}
