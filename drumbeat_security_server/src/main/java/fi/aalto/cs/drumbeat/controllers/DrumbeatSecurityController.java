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
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.utils.Tuple;

import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.ontology.Ontology;

public class DrumbeatSecurityController {
	private static final Log log = LogFactory.getLog(DrumbeatSecurityController.class);
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

	public static DrumbeatSecurityController getDrumbeatSecurityController(URI uri) {
		if (!singleton.isPresent()) {
			singleton = Optional.of(new DrumbeatSecurityController(uri));
		}
		return singleton.get();
	}

	private DrumbeatSecurityController(URI uri) {
		super();
		rootURI = Optional.of(uri);
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

	public boolean validatePath(Resource start_node, String webid_uri, List<String> rolepath_strlist) {
		log.info("validatePath: node " + start_node + " path: " + rolepath_strlist);
		DrumbeatSecurityController.getAccessList()
				.add(new Tuple<String, Long>(
						"validatePath: " + webid_uri + " path: "
								+ rolepath_strlist.stream().collect(Collectors.joining("-")),
						System.currentTimeMillis()));
		Resource current_node = root;
		if (start_node != null)
			current_node = start_node;
		ListIterator<String> iterator = rolepath_strlist.listIterator();
		if (iterator.hasNext()) {
			String first_step = iterator.next();
			log.info("Finding property " + first_step + " from the inference model");
			Property p = rdf_datastore.getInferenceModel().getProperty(first_step);
			StmtIterator connected_triples = current_node.listProperties(p);
			if (!connected_triples.hasNext()) {
				log.info("No triples connected to the property");
				log.info("located here?:" + current_node);
				log.info("Path for the rest is:" + rolepath_strlist);
				return checkPath_HTTP(current_node.toString(), webid_uri, rolepath_strlist);
			}
			while (connected_triples.hasNext()) {
				Statement triple = connected_triples.next();
				Resource node = triple.getObject().asResource();
				log.info("from local store:" + node);
				current_node = node;
				if (!iterator.hasNext()) {
					if (current_node.toString().equals(webid_uri)) {
						DrumbeatSecurityController.getAccessList().add(
								new Tuple<String, Long>("-->" + webid_uri + " found here", System.currentTimeMillis()));
						return true;
					}
				} else {
					List<String> new_path = rolepath_strlist.subList(1, rolepath_strlist.size());
					return validatePath(current_node, webid_uri, new_path);
				}

			}
		}

		saveUnsucceeLocal(webid_uri, rolepath_strlist.toString());
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
			List<String> rolepath_list = new ArrayList<>();
			for (int i = 0; i < new_path.size(); i++) {
				rolepath_list.add(new_path.get(i));
			}
			Resource rolepath = Dumbeat_JenaLibrary.createRolePath(query_model, rolepath_list);

			Resource query = query_model.createResource();
			query.addProperty(Ontology.Authorization.hasRolePath, rolepath);

			Literal time_inMilliseconds = query_model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, Ontology.Message.SecurityQuery);
			query.addLiteral(Ontology.Message.hasTimeStamp, time_inMilliseconds);
			query.addProperty(Ontology.Message.hasWebID, query_model.getResource(webid));

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

			ResIterator iter = response_model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);
			Resource result = null;
			if (iter.hasNext())
				result = iter.next();

			@SuppressWarnings("unused")
			RDFNode time_stamp = result.getProperty(Ontology.Message.hasTimeStamp).getObject();
			// TODO check the time_stamp
			return result.getProperty(Ontology.Message.hasPermissionStatus).getObject()
					.asResource().toString().equals(Ontology.Message.accepted.toString());

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
		root.addProperty(Ontology.Contractor.hasEmployee, widr);
		widr.addLiteral(Ontology.property_hasPublicKey, public_key);
		rdf_datastore.saveRDFData();
		return widr;
	}


}
