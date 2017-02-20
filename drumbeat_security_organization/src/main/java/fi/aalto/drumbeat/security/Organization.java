package fi.aalto.drumbeat.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.rdf.model.RDFNode;

import fi.aalto.drumbeat.Fetchable;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.webid.WebIDCertificate;
import fi.aalto.drumbeat.webid.WebIDProfile;

public class Organization extends Fetchable {
	private URI rootURI;
	private String uri = "";

	private Map<String, WebIDProfile> webid_profiles = new HashMap<String, WebIDProfile>();

	private RDFDataStore rdf_datastore = null;

	public Organization(String uri_str, String name) {
		super();
		try {
			URI uri = new URI(uri_str);
			this.uri = uri.getHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			rootURI = new URIBuilder().setScheme("http").setHost(uri).setPath("/" + name).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		rdf_datastore = new RDFDataStore(rootURI);
		rdf_datastore.readRDFData();
		rdf_datastore.saveRDFData();
	}

	public WebIDProfile get(String local_path) {
		return webid_profiles.get(local_path);
	}

	/*public boolean connect(RulePath rulepath, String webID_url) {
		System.out.println(rulepath.getHead());
		boolean ret = false;
		List<RDFNode> result_list = rdf_datastore.getData(rulepath.getHead());
		for (RDFNode one_match : result_list) {
			System.out.println("return from organization base: " + one_match);
			if (webID_url.equals(one_match.toString()))
				ret = true;
		}
		return ret;
	}*/

	public String getUri() {
		return uri;
	}



	public WebIDCertificate getWebID(String local_path) {
		String public_key = "1234";
		URI webid_uri;
		try {
			webid_uri = new URIBuilder().setScheme("http").setHost(uri).setPath("/" + local_path).build();
			WebIDCertificate wc = new WebIDCertificate(webid_uri, public_key);
			webid_profiles.put(webid_uri.getPath(), new WebIDProfile(webid_uri.toString(), public_key));
			return wc;

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
