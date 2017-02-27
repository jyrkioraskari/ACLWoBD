package fi.aalto.drumbeat.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Fetchable;
import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.webid.WebIDCertificate;
import fi.aalto.drumbeat.webid.WebIDProfile;

public class OrganizationManager extends Fetchable {
	private URI rootURI;
	private final Model datamodel;
	private final Resource root;

	private Map<String, WebIDProfile> webid_profiles = new HashMap<String, WebIDProfile>();

	private static OrganizationManager singleton = null;

	public static OrganizationManager getOrganizationManager(URI uri) {
		if (singleton == null)
			singleton = new OrganizationManager(uri);
		return singleton;

	}
	private OrganizationManager(URI uri) {
		super();
		rootURI = uri;
		rdf_datastore = new RDFDataStore(rootURI, "organization");
		datamodel = rdf_datastore.getModel();
		// rdf_datastore.readRDFData();
		root = datamodel.getResource(rootURI.toString());

	}
	
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
	}

	private RDFDataStore rdf_datastore = null;

	

	public boolean checkRDFPath(String webid_uri, Resource path) {
		LinkedList<Resource> rulepath = parseRulePath(path);
		Resource current_node = root;
		ListIterator<Resource> iterator = rulepath.listIterator();
		while (iterator.hasNext()) {
			Resource step = iterator.next();
			Property p = rdf_datastore.getModel().getProperty(step.getURI());
			Resource node = current_node.getPropertyResourceValue(p);
			if (node != null) {
				System.out.println("from local store:" + node);
				current_node = node;
			} else {
				System.out.println("located somewhere else. current node was: " + current_node);

				List<Resource> new_path = rulepath.subList(rulepath.indexOf(step), rulepath.size());
				System.out.println("Path for the rest is:" + new_path);

				break;
			}
		}
		if (current_node.toString().equals(webid_uri))
			return true;
		else
			return false;
	}

	public WebIDProfile getWebIDProfile(String webid_uri) {
		return webid_profiles.get(webid_uri);
	}

	public WebIDCertificate registerWebID(String name, String public_key) {
		String id = UUID.randomUUID().toString();
		URI webid_uri;
		try {
			String root_path=rootURI.getPath();
			root_path=root_path.substring(0, root_path.substring(1).indexOf("/")+1);  //TODO lis�� testej�
			webid_uri = new URIBuilder(rootURI).setScheme("https").setPath(root_path+"/profile/" + id).build();
			WebIDCertificate wc = new WebIDCertificate(webid_uri, name, public_key);
			webid_profiles.put(webid_uri.toString(), new WebIDProfile(webid_uri.toString(), name, public_key));
			rdf_datastore.saveRDFData();

			Resource widr=datamodel.getResource(webid_uri.toString());
			root.addProperty(RDFConstants.property_knowsPerson, widr);
			widr.addLiteral(RDFConstants.property_hasPublicKey, public_key);

			return wc;

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LinkedList<Resource> parseRulePath(Resource node) {
		LinkedList<Resource> ret = new LinkedList<Resource>();

		Resource current = node;
		while (current != null && current.asResource().hasProperty(RDF.rest)) {
			if (current.hasProperty(RDF.first))
				ret.add(current.getPropertyResourceValue(RDF.first));
			current = current.getPropertyResourceValue(RDF.rest);
		}

		return ret;
	}
}
