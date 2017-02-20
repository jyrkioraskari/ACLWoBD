package fi.aalto.drumbeat.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import fi.aalto.drumbeat.Fetchable;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.webid.WebIDCertificate;
import fi.aalto.drumbeat.webid.WebIDProfile;

public class Organization extends Fetchable {
	private URI rootURI;

	private Map<String, WebIDProfile> webid_profiles = new HashMap<String, WebIDProfile>();

	private RDFDataStore rdf_datastore = null;

	public Organization(String uri_str) {
		super();
		try {
			rootURI = new URI(uri_str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		rdf_datastore = new RDFDataStore(rootURI);
		rdf_datastore.readRDFData();
		rdf_datastore.saveRDFData();
	}

	public WebIDProfile get(String local_path) {
		return webid_profiles.get(local_path);
	}

	

	public WebIDCertificate getWebID(String name) {
		String public_key = "1234";
		URI webid_uri;
		try {
			webid_uri = new URIBuilder(rootURI).setScheme("https").setPath("/" + name).build();
			WebIDCertificate wc = new WebIDCertificate(webid_uri, public_key);
			webid_profiles.put(webid_uri.getPath(), new WebIDProfile(webid_uri.toString(), public_key));
			return wc;

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
