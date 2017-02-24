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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.drumbeat.Fetchable;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.webid.WebIDCertificate;
import fi.aalto.drumbeat.webid.WebIDProfile;

public class OrganizationManager extends Fetchable {
	private URI rootURI;
	private final Model organization_datamodel;

	private Map<String, WebIDProfile> webid_profiles = new HashMap<String, WebIDProfile>();

	private RDFDataStore rdf_datastore = null;

	public OrganizationManager(URI uri) {
		super();
		rootURI = uri;
		rdf_datastore = new RDFDataStore(rootURI, "organization");
		organization_datamodel = rdf_datastore.getModel();
		rdf_datastore.readRDFData();

	}

	public boolean checkRDFPath(String webid_uri, Resource path) {
		LinkedList<Resource> rulepath = rdf_datastore.getRulePath(path);
		Resource current_node = rdf_datastore.getModel().getResource(rootURI.toString());
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
			webid_uri = new URIBuilder(rootURI).setScheme("https").setPath("/webid/" + id ).build();
			WebIDCertificate wc = new WebIDCertificate(webid_uri, name, public_key);
			webid_profiles.put(webid_uri.toString(), new WebIDProfile(webid_uri.toString(), name, public_key));
			rdf_datastore.saveRDFData();
			return wc;

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
