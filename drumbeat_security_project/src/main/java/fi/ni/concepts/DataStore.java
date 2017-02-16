package fi.ni.concepts;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import fi.ni.Fetchable;
import fi.ni.Internet;
import fi.ni.RDFDataStore;
import fi.ni.webid.WebIDCertificate;
import fi.ni.webid.WebIDProfile;

public class DataStore extends Fetchable {
	private String host_name;
	private URI uri;
	// at the time
	private RDFDataStore rdf_datastore = null;

	public DataStore(String host_uri) {
		try {
			uri = new URI(host_uri);

		} catch (Exception e) {
			e.printStackTrace();
		}

		rdf_datastore = new RDFDataStore(uri);
		rdf_datastore.readRDFData(); // TODO read security data
		rdf_datastore.saveRDFData();
	}

	public boolean connect(WebIDCertificate wc, String request_uri) {
		URI canonizted_requestURI = canonizateURI(request_uri);
		System.out.println("WebID oli:" + wc.getWebid_uri().toString());
		System.out.println("req uri oli:" + request_uri);
		System.out.println("canonized uri oli:" + canonizted_requestURI);

		Organization o = (Organization) Internet.get(wc.getWebid_uri().toString());
		if (o == null)
			return false;
		WebIDProfile wp = (WebIDProfile) o.get(wc.getWebid_uri().getPath());
		if (wc.getPublic_key().equals(wp.getPublic_key())) {
			List<RDFNode> matched_paths = rdf_datastore.match(canonizted_requestURI.toString());
			for (RDFNode r : matched_paths) {
				System.out.println("match: " + r.toString());
				System.out.println("permissions: " + rdf_datastore.getPermissions(r.toString()));
				System.out.println("rule pahth is: " + rdf_datastore.getRulePath(r.toString()));

				Resource current_node = rdf_datastore.getModel().getResource(r.toString());
				LinkedList<Resource> rulepath = rdf_datastore.getRulePath(r.toString());
				ListIterator<Resource> iterator = rulepath.listIterator();
				while (iterator.hasNext()) {
					Resource step = iterator.next();
					Property p = rdf_datastore.getModel().getProperty(step.getURI());
					Resource node = current_node.getPropertyResourceValue(p);
					if (node != null)
					{
						System.out.println("from local store:" + node);
						current_node = node;
					}
					else
					{
						System.out.println("located somewhere else. current node was: "+current_node);
						
						List<Resource> new_path=rulepath.subList(rulepath.indexOf(step),rulepath.size());
						System.out.println("Path for the rest is:" + new_path);

						break;
					}
				}
			}
			return true;
		} else
			return false;
	}

	public URI canonizateURI(String uri_txt) {
		URI uri;
		try {
			uri = new URI(uri_txt);
			String path = uri.getPath();
			path = path.replaceFirst("/drumbeat/objects", "/security");
			path = path.replaceFirst("/drumbeat/collections", "/security");
			path = path.replaceFirst("/drumbeat/datasources", "/security");
			path = path.replaceFirst("/drumbeat/datasets", "/security");
			return new URIBuilder().setScheme("https").setHost(uri.getHost()).setPath(path).build();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
}
