package fi.aalto.cs.drumbeat.controllers;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.utils.Tuple;

import fi.aalto.drumbeat.Dumbeat_JenaLibrary;
import fi.aalto.drumbeat.RDFDataStore;
import fi.aalto.drumbeat.ontology.Ontology;


public class DataProtectionController {
	private static final Log log = LogFactory.getLog(DataProtectionController.class);

	private Optional<URI> uri = Optional.empty();;
	// at the time
	private Optional<RDFDataStore> rdf_datastore = Optional.empty();

	private static Optional<DataProtectionController> singleton = Optional.empty();

	public static DataProtectionController getDataServer(String uri_str) {
		if (!singleton.isPresent()) {
			URI uri;
			try {
				uri = new URI(uri_str);
				URI service_root = new URIBuilder(uri).setScheme("https").setPath("/").build();
				System.out.println("DataServer root: "+service_root.toString());
				singleton = Optional.of(new DataProtectionController(service_root.toString()));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return singleton.get();
	}

	private DataProtectionController(String host_uri) {
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

	public List<String> autenticate(String webid, String request_uri) {
		DrumbeatSecurityController.getAccessList().add(new Tuple<String, Long>("autenticate: "+webid+" req uri: "+request_uri,System.currentTimeMillis()));
		List<String> ret=new ArrayList<String>();
		URI canonizted_requestURI = canonizateURI(request_uri);
		System.out.println("DRUMBEAT WebID oli:" + webid);
		System.out.println("DRUMBEAT req uri oli:" + request_uri);
		System.out.println("DRUMBEAT canonized uri oli:" + canonizted_requestURI);
		
		log.info("DRUMBEAT WebID oli:" + webid);
		log.info("DRUMBEAT req uri oli:" + request_uri);
		log.info("DRUMBEAT canonized uri oli:" + canonizted_requestURI);

		final List<RDFNode> matched_paths = new ArrayList<>();
		rdf_datastore.ifPresent(x -> Dumbeat_JenaLibrary.match(x.getInferenceModel(),matched_paths, canonizted_requestURI.toString()));
		// We select only the longest path that has a matching rules
		int longest_matching_url_length=0;
		for (RDFNode r : matched_paths) {
			System.out.println("match: " + r.toString());
			//rdf_datastore.ifPresent(x -> 
			RDFDataStore x=rdf_datastore.get();
			{
				Resource current_node = x.getInferenceModel().getResource(r.toString());
				
				List<Resource> rulepath_list = null;
				Resource authorizationRule=r.asResource().getPropertyResourceValue(Ontology.Authorization.hasAuthorizationRule);
				if(authorizationRule!=null) {
					Resource rule_path=authorizationRule.getPropertyResourceValue(Ontology.Authorization.hasRulePath);
					rulepath_list = Dumbeat_JenaLibrary.parseRulePath(x.getInferenceModel(),rule_path); 
				}
				else
					continue;
				if(longest_matching_url_length<r.toString().length())
				{
					ret.clear();  // Only longest wins
				}
				
				rulepath_list = rulepath_list.stream().filter(rule -> !((Resource) rule).isLiteral()).collect(Collectors.toList());
				ListIterator<Resource> iterator = rulepath_list.listIterator();
				//TODO toteuta sama kuin organisaatiopuolella  eli rekursiivinen haku.. voidaan toteuttaa 
				//tästä kutsumalla sitä (eli ei tarvitse olla etäkutsu)
				while (iterator.hasNext()) {
					Resource step = iterator.next();
					Property p = x.getInferenceModel().getProperty(step.getURI());
					Resource node = current_node.getPropertyResourceValue(p); //TODO Huomaa  URL ei voi olla Literal!
					if (node != null) {
						System.out.println("DRUMBEAT from local store:" + node);
						log.info("DRUMBEAT from local store:" + node);
						current_node = node;
						if(!iterator.hasNext())
						{
							if(node.toString().equals(webid))
							{
								System.out.println("Equals");
								DrumbeatSecurityController.getAccessList().add(new Tuple<String, Long>("a-->"+webid+" found here",System.currentTimeMillis()));

								log.info("Equals");
								List<String> perms=Dumbeat_JenaLibrary.getPermissions(x.getInferenceModel(),r.toString()).stream().map(y->{
									String sy=y.asResource().getURI();
									int i=sy.lastIndexOf("#");
									sy=sy.substring(i+1);
									return sy;
								}).collect(Collectors.toCollection(ArrayList::new));
								ret.addAll(perms); 
							}
							else
							{
								System.out.println("Not Equals: "+node.toString());
								log.info("Not Equals: "+node.toString());
								ret.add("localguest"); 
							}
						}
						
					} else {
						System.out.println("DRUMBEAT located somewhere else. current node was: " + current_node+" property was:"+p.toString());
						log.info("DRUMBEAT located somewhere else. current node was: " + current_node+" property was:"+p.toString());
						{
						 Iterator i=current_node.listProperties();
						 while(i.hasNext())
						 {
							 String ir=i.next().toString();
							 System.out.println("str: "+ir);
						 	 log.info("str: "+ir);
						 }
						}

						List<Resource> new_path = rulepath_list.subList(rulepath_list.indexOf(step), rulepath_list.size());
						System.out.println("Path for the rest is:" + new_path);
						log.info("Path for the rest is:" + new_path);
						
						
						if(validatePath_HTTP(current_node.getURI(),webid,new_path)) {
							System.out.println("remote "+current_node.getURI()+" says OK");
							log.info("remote "+current_node.getURI()+" says OK");
							List<String> perms=Dumbeat_JenaLibrary.getPermissions(x.getInferenceModel(),r.toString()).stream().map(y->{
								String sy=y.asResource().getURI();
								int i=sy.lastIndexOf("#");
								sy=sy.substring(i+1);
								return sy;
							}).collect(Collectors.toCollection(ArrayList::new));
							ret.addAll(perms); 
						} else 
						{
							System.out.println("remote "+current_node.getURI()+" says NOT");
							log.info("remote "+current_node.getURI()+" says NOT");
							ret.add("remoteguest"); 
						}
						break;
					}
				}
	
			//});
			}

		}
		
		return ret;
	}
	
	private boolean validatePath_HTTP(String nextStepURL,String webid,List<Resource> new_path ) {
		final OntModel query_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		System.out.println("Next step URL is: "+nextStepURL);
		DrumbeatSecurityController.getAccessList().add(new Tuple<String, Long>("-->"+webid+"-->"+nextStepURL,System.currentTimeMillis()));
		if(!rdf_datastore.isPresent())
			return false;
		try {
			
			List<String> rulepath_lista=new ArrayList<>();
			for (int i = 0; i < new_path.size(); i++) {
				rulepath_lista.add(new_path.get(i).toString());
			}
			Resource rulepath=Dumbeat_JenaLibrary.createRulePath(query_model,rulepath_lista);
			
			Individual query = query_model.createIndividual(null, Ontology.Message.SecurityQuery);
			
			query.addProperty(Ontology.Authorization.hasRulePath, rulepath);

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
			if(response.getStatus()!=200)
			{
				System.err.println(response_txt);
				return false;
				
			}

			final OntModel response_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
			response_model.read(new ByteArrayInputStream( response_txt.getBytes()), null, "JSON-LD");
			
			Resource result = null;
			ResIterator iter = response_model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);			
			if (iter.hasNext())
				result = iter.next();
			
			RDFNode time_stamp = result.getProperty(Ontology.Message.hasTimeStamp).getObject();
			return result.getPropertyResourceValue(Ontology.Message.hasPermissionStatus).toString().equals(Ontology.Message.accepted.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
