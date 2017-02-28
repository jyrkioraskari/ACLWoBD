package fi.aalto.drumbeat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;


public class DataServer {
	private Optional<String> host_name=Optional.empty();;
	private Optional<URI> uri=Optional.empty();;
	// at the time
	private Optional<RDFDataStore> rdf_datastore = Optional.empty();

	private static Optional<DataServer> singleton = Optional.empty();

	public static DataServer getDataServer(String uri_str) {
		if (singleton == null) {
			URI uri;
			try {
				uri = new URI(uri_str);
				URI service_oot = new URIBuilder(uri).setScheme("https").setPath("/security/").build();
				singleton = Optional.of(new DataServer(service_oot.toString()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return singleton.get();
	}

	private DataServer(String host_uri) {
		try {
			uri = Optional.of(new URI(host_uri));

		} catch (Exception e) {
			e.printStackTrace();
		}
		if(uri.isPresent())
			rdf_datastore = Optional.of(new RDFDataStore(uri.get(), "datastore"));
		rdf_datastore.get().readRDFData(); // TODO read security data
		rdf_datastore.get().saveRDFData();
	}

	public boolean connect(String wc, String request_uri) {
		URI canonizted_requestURI = canonizateURI(request_uri);
		System.out.println("WebID oli:" + wc);
		System.out.println("req uri oli:" + request_uri);
		System.out.println("canonized uri oli:" + canonizted_requestURI);

		List<RDFNode> matched_paths = rdf_datastore.get().match(canonizted_requestURI.toString());
		for (RDFNode r : matched_paths) {
			System.out.println("match: " + r.toString());
			System.out.println("permissions: " + rdf_datastore.get().getPermissions(r.toString()));
			System.out.println("rule pahth is: " + rdf_datastore.get().parseRulePath(r.asResource()));

			Resource current_node = rdf_datastore.get().getModel().getResource(r.toString());
			List<Resource> rulepath = rdf_datastore.get().parseRulePath(r.asResource());
			rulepath=rulepath.stream().filter(rule -> ((Resource)rule).isLiteral()).collect(Collectors.toList());
			ListIterator<Resource> iterator = rulepath.listIterator();
			
			while (iterator.hasNext()) {
				Resource step = iterator.next();
				Property p = rdf_datastore.get().getModel().getProperty(step.getURI());
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
		}
		return true;
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
