package fi.aalto.drumbeat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class DataServer {
	private static final Log log = LogFactory.getLog(DataServer.class);

	private Optional<URI> uri = Optional.empty();;
	// at the time
	private Optional<RDFDataStore> rdf_datastore = Optional.empty();

	private static Optional<DataServer> singleton = Optional.empty();

	public static DataServer getDataServer(String uri_str) {
		if (!singleton.isPresent()) {
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
		uri.ifPresent(x -> rdf_datastore = Optional.of(new RDFDataStore(x, "datastore")));

		rdf_datastore.ifPresent(x -> {
			// x.readRDFData(); // TODO read security data
			x.saveRDFData();
		}
		);

	}

	public List<String> connect(String wc, String request_uri) {
		List<String> ret=new ArrayList<>();
		URI canonizted_requestURI = canonizateURI(request_uri);
		System.out.println("DRUMBEAT WebID oli:" + wc);
		System.out.println("DRUMBEAT req uri oli:" + request_uri);
		System.out.println("DRUMBEAT canonized uri oli:" + canonizted_requestURI);
		
		log.info("DRUMBEAT WebID oli:" + wc);
		log.info("DRUMBEAT req uri oli:" + request_uri);
		log.info("DRUMBEAT canonized uri oli:" + canonizted_requestURI);

		final List<RDFNode> matched_paths = new ArrayList<>();
		rdf_datastore.ifPresent(x -> x.match(matched_paths, canonizted_requestURI.toString()));
		
		for (RDFNode r : matched_paths) {
			System.out.println("match: " + r.toString());
			rdf_datastore.ifPresent(x -> {

				Resource current_node = x.getModel().getResource(r.toString());
				List<Resource> rulepath = x.parseRulePath(r.asResource());
				rulepath = rulepath.stream().filter(rule -> ((Resource) rule).isLiteral()).collect(Collectors.toList());
				ListIterator<Resource> iterator = rulepath.listIterator();

				while (iterator.hasNext()) {
					Resource step = iterator.next();
					Property p = x.getModel().getProperty(step.getURI());
					Resource node = current_node.getPropertyResourceValue(p);
					if (node != null) {
						log.info("DRUMBEAT from local store:" + node);
						current_node = node;
					} else {
						log.info("DRUMBEAT located somewhere else. current node was: " + current_node);

						List<Resource> new_path = rulepath.subList(rulepath.indexOf(step), rulepath.size());
						System.out.println("Path for the rest is:" + new_path);
						break;
					}
				}
				boolean OK=true;
				if(OK) {
					log.info("DRUMBEAT permissions: " + x.getPermissions(r.toString()));
					log.info("DRUMBEAT rule path is: " + x.parseRulePath(r.asResource()));
					List<String> perms=x.getPermissions(r.toString()).stream().map(y->{
						String sy=y.asResource().getURI();
						int i=sy.lastIndexOf("/");
						sy=sy.substring(i+1);
						return sy;
					}).collect(Collectors.toCollection(ArrayList::new));
					ret.addAll(perms);
				}


			});

		}
		return ret;
	}

	public URI canonizateURI(String uri_txt) {
		URI uri;
		try {
			uri = new URI(uri_txt);
			String path = uri.getPath();
			path = path.replaceFirst("/protected", "/security");
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
