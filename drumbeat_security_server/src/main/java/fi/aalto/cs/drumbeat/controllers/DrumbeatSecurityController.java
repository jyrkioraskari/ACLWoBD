package fi.aalto.cs.drumbeat.controllers;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.utils.Tuple;

import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.ontology.Authorization;
import fi.aalto.drumbeat.ontology.Contractor;
import fi.aalto.drumbeat.ontology.Message;
import fi.aalto.drumbeat.ontology.Ontology;

public class DrumbeatSecurityController {
	final static private List<Tuple<String, String>> unseen_locals = new ArrayList<>();
	final static private List<Tuple<String, Long>> access_list = new ArrayList<>();

	public static List<Tuple<String, String>> getUnseenLocals() {
		return unseen_locals;
	}

	public static List<Tuple<String, Long>> getAccessList() {
		return access_list;
	}

	private Optional<URI> rootURI = Optional.empty();;
	private final Model datamodel;
	private final Resource root;

	private static Optional<DrumbeatSecurityController> singleton = Optional.empty();

	public static DrumbeatSecurityController getOrganizationManager(URI uri) {
		if (!singleton.isPresent()) {
			singleton = Optional.of(new DrumbeatSecurityController(uri));
		}
		return singleton.get();
	}

	private DrumbeatSecurityController(URI uri) {
		super();
		rootURI =  Optional.of(uri);
		rdf_datastore = new RDFDataStore(rootURI.get(), "organization");
		datamodel = rdf_datastore.getInferenceModel();
		// rdf_datastore.readRDFData();
		root = datamodel.getResource(rootURI.toString());
		registerWebID("https://jyrkio2.databox.me/profile/card#me", "1234");
	}

	private RDFDataStore rdf_datastore = null;

	public boolean validate(String webid_uri, List<String> rulepath_list) {
		return validatePath(null, webid_uri, rulepath_list);
	}

	private boolean validatePath(Resource previous_node, String webid_uri, List<String> rulepath_strlist) {
		
		DrumbeatSecurityController.getAccessList()
				.add(new Tuple<String, Long>(
						"validatePath: " + webid_uri + " path: " + rulepath_strlist.stream().collect(Collectors.joining("-")),
						System.currentTimeMillis()));
		Resource current_node = root;
		if (previous_node != null)
			current_node = previous_node;
		ListIterator<String> iterator = rulepath_strlist.listIterator();
		int stepper_inx=0;
		while (iterator.hasNext()) {
			String step = iterator.next();
			stepper_inx++;
			Property p = rdf_datastore.getInferenceModel().getProperty(step);
			StmtIterator connected_triples = current_node.listProperties(p);
			while (connected_triples.hasNext()) {
				Statement triple = connected_triples.next();
				Resource node = triple.getObject().asResource();
				if (node != null) {
					System.out.println("from local store:" + node);
					current_node = node;
					if (!iterator.hasNext()) {
						if (current_node.toString().equals(webid_uri)) {
							DrumbeatSecurityController.getAccessList().add(new Tuple<String, Long>(
									"-->" + webid_uri + " found here", System.currentTimeMillis()));
							return true;
						}
					} else {
						List<String> new_path = rulepath_strlist.subList(stepper_inx, rulepath_strlist.size());
						return validatePath(current_node, webid_uri, new_path);
					}
				} else {
					System.out.println("located somewhere else. current node was: " + current_node);

					List<String> new_path = rulepath_strlist.subList(stepper_inx, rulepath_strlist.size());
					System.out.println("Path for the rest is:" + new_path);
					return checkPath_HTTP(current_node.toString(), webid_uri, new_path);
				}
			}
		}

		saveUnsucceeLocal(webid_uri, rulepath_strlist.toString());
		return false;
	}

	private void saveUnsucceeLocal(String webid_uri, String path_url) {
		System.out.println("unsuccessful webid: " + webid_uri);
		unseen_locals.add(new Tuple<String, String>(webid_uri, path_url));

	}

	private boolean checkPath_HTTP(String nextStepURL, String webid, List<String> new_path) {
		DrumbeatSecurityController.getAccessList()
				.add(new Tuple<String, Long>("v-->" + webid + "-->" + nextStepURL, System.currentTimeMillis()));

		final OntModel query_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		System.out.println("Next step URL is: " + nextStepURL);
		try {
			List<String> rulepath_lista=new ArrayList<>();
			for (int i = 0; i < new_path.size(); i++) {
				rulepath_lista.add(new_path.get(i));
			}
			Resource rulepath=Dumbeat_JenaLibrary.createRulePath(query_model,rulepath_lista);
			
			Resource query = query_model.createResource();
			query.addProperty(Authorization.hasRulePath, rulepath);

			Literal time_inMilliseconds = query_model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, Message.SecurityQuery);
			query.addLiteral(Message.hasTimeStamp, time_inMilliseconds);
			query.addProperty(Message.hasWebID, query_model.getResource(webid));

			StringWriter writer = new StringWriter();
			query_model.write(writer, "JSON-LD");
			writer.flush();

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(nextStepURL);

			Response response = target.request().post(Entity.entity(writer.toString(), "application/ld+json"));

			String response_txt = response.readEntity(String.class);
			response.close();

			final Model response_model = ModelFactory.createDefaultModel();
			response_model.read(new ByteArrayInputStream(response_txt.getBytes()), null, "JSON-LD");

			ResIterator iter = response_model.listSubjectsWithProperty(Message.hasTimeStamp);
			Resource result = null;
			if (iter.hasNext())
				result = iter.next();

			@SuppressWarnings("unused")
			RDFNode time_stamp = result.getProperty(Message.hasTimeStamp).getObject();
			// TODO check the time_stamp
			return result.getProperty(Message.hasPermissionStatus).getObject().asResource() == Message.accepted;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Resource getWebIDProfile(String webid_uri) {
		return datamodel.getResource(webid_uri.toString());
	}

	public Resource registerWebID(String webidURI, String public_key) {		
		Resource widr = datamodel.getResource(webidURI);
		root.addProperty(Contractor.trusts, widr);
		widr.addLiteral(Ontology.property_hasPublicKey, public_key);
		rdf_datastore.saveRDFData();
		return widr;
	}

	
	/*
	public Model getWebID(String webid) {
		//http://stackoverflow.com/questions/1820908/how-to-turn-off-the-eclipse-code-formatter-for-certain-sections-of-java-code
		// @formatter:off
		String queryString =	 "CONSTRUCT { \n" +
								"	?webid ?p ?o \n" + 
								"} WHERE { \n" + 
								"     ?webid ?p ?o . \n" + 
								"} \n";
		// @formatter:on
		ParameterizedSparqlString ps = new ParameterizedSparqlString(queryString);
		
		ps.setIri("webid", webid);
		Query query=ps.asQuery();
		Model result = QueryExecutionFactory.create(query, datamodel).execConstruct();
		return result;
	}*/
}
