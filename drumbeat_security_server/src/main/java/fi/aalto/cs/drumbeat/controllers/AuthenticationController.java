package fi.aalto.cs.drumbeat.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.utils.Tuple;

import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.ontology.Ontology;

public class AuthenticationController {
	private static final Log log = LogFactory.getLog(AuthenticationController.class);

	private Optional<URI> uri = Optional.empty();;
	// at the time
	private Optional<RDFDataStore> rdf_datastore = Optional.empty();

	private static Optional<AuthenticationController> singleton = Optional.empty();

	public static AuthenticationController getAuthenticationController(String uri_str) {
		if (!singleton.isPresent()) {
			URI uri;
			try {
				uri = new URI(uri_str);
				URI service_root = new URIBuilder(uri).setScheme("https").setPath("/").build();
				System.out.println("DataServer root: " + service_root.toString());
				singleton = Optional.of(new AuthenticationController(service_root.toString()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return singleton.get();
	}

	private AuthenticationController(String host_uri) {
		try {
			uri = Optional.of(new URI(host_uri));

		} catch (Exception e) {
			e.printStackTrace();
		}
		uri.ifPresent(x -> rdf_datastore = Optional.of(new RDFDataStore(x, "datastore")));

		rdf_datastore.ifPresent(x -> {
			// x.readRDFData(); // TODO read security data
			x.saveRDFData();
		});

	}

	public List<String> autenticate(String webid, String request_uri) {
		DrumbeatSecurityController.getAccessList().add(new Tuple<String, Long>(
				"autenticate: " + webid + " req uri: " + request_uri, System.currentTimeMillis()));
		List<String> ret = new ArrayList<String>();
		URI canonizted_requestURI = canonizateURI(request_uri);
		System.out.println("DRUMBEAT WebID oli:" + webid);
		System.out.println("DRUMBEAT req uri oli:" + request_uri);
		System.out.println("DRUMBEAT canonized uri oli:" + canonizted_requestURI);

		log.info("DRUMBEAT WebID oli:" + webid);
		log.info("DRUMBEAT req uri oli:" + request_uri);
		log.info("DRUMBEAT canonized uri oli:" + canonizted_requestURI);

		final List<RDFNode> matched_paths = new ArrayList<>();
		rdf_datastore.ifPresent(
				x -> Dumbeat_JenaLibrary.match(x.getInferenceModel(), matched_paths, canonizted_requestURI.toString()));
		// We select only the longest path that has a matching rules
		int longest_matching_url_length = 0;
		for (RDFNode r : matched_paths) {
			System.out.println("match: " + r.toString());
			// rdf_datastore.ifPresent(x ->
			RDFDataStore x = rdf_datastore.get();
			{
				Resource current_node = x.getInferenceModel().getResource(r.toString());

				List<Resource> rolepath_list = null;
				Resource authorizationRule = r.asResource()
						.getPropertyResourceValue(Ontology.Authorization.hasACL);
				if (authorizationRule != null) {
					Resource rule_path = authorizationRule.getPropertyResourceValue(Ontology.Authorization.rolePath);
					rolepath_list = Dumbeat_JenaLibrary.parseRolePath(x.getInferenceModel(), rule_path);
				} else
					continue;
				if (longest_matching_url_length < r.toString().length()) {
					ret.clear(); // Only longest wins
				}

				rolepath_list = rolepath_list.stream().filter(rule -> !((Resource) rule).isLiteral())
						.collect(Collectors.toList());
				List<String> rolepath_list_str = new ArrayList<>();
				for (Resource rs : rolepath_list)
					rolepath_list_str.add(rs.toString());
				if (uri.isPresent()) {
					DrumbeatSecurityController dsc = DrumbeatSecurityController
							.getDrumbeatSecurityController(uri.get());
					if (dsc.validatePath(current_node, webid, rolepath_list_str)) {
						System.out.println("validation " + current_node.getURI() + " says OK");
						log.info("validation " + current_node.getURI() + " says OK");
						List<String> perms = Dumbeat_JenaLibrary.getPermissions(x.getInferenceModel(), r.toString())
								.stream().map(y -> {
									String sy = y.asResource().getURI();
									int i = sy.lastIndexOf("#");
									sy = sy.substring(i + 1);
									return sy;
								}).collect(Collectors.toCollection(ArrayList::new));
						ret.addAll(perms);
					} else {
						System.out.println("validation " + current_node.getURI() + " says NOT");
						log.info("validation " + current_node.getURI() + " says NOT");
						ret.add("guest");
					}

				} else {
					System.out.println("no base uri");
					log.info("no base uri");
				}

			}

		}

		return ret;
	}


	private URI canonizateURI(String uri_txt) {
		URI uri;
		try {
			uri = new URI(uri_txt);
			String path = uri.getPath();
			path = path.replaceFirst("/protected", "");
			path = path.replaceFirst("/drumbeat/objects", "");
			path = path.replaceFirst("/drumbeat/collections", "");
			path = path.replaceFirst("/drumbeat/datasources", "");
			path = path.replaceFirst("/drumbeat/datasets", "/");
			return new URIBuilder().setScheme("https").setHost(uri.getHost()).setPath(path).build();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
}
