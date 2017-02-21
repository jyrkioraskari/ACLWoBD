package fi.aalto.drumbeat.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import fi.aalto.drumbeat.Fetchable;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.webid.WebIDCertificate;
import fi.aalto.drumbeat.webid.WebIDProfile;

public class OrganizationManager extends Fetchable {
	private URI rootURI;
	Model organization_datamodel = ModelFactory.createOntologyModel();

	private Map<String, WebIDProfile> webid_profiles = new HashMap<String, WebIDProfile>();

	private RDFDataStore rdf_datastore = null;

	public OrganizationManager(URI uri) {
		super();
			rootURI = uri;
		rdf_datastore = new RDFDataStore(rootURI,"organization");
		rdf_datastore.readRDFData();
		
		
		
	}

	public WebIDProfile getWebIDProfile(String webid_uri) {
		return webid_profiles.get(webid_uri);
	}

	

	public WebIDCertificate getWebID(String name, String public_key) {
		String id = UUID.randomUUID().toString();
		URI webid_uri;
		try {
			webid_uri = new URIBuilder(rootURI).setScheme("https").setPath("/webid" + id+"#i").build();
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
