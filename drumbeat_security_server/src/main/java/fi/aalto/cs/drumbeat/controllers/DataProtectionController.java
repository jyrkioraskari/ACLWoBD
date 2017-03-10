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
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.RDFDataStore;


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
		List<String> ret=new ArrayList<String>();
		URI canonizted_requestURI = canonizateURI(request_uri);
		System.out.println("DRUMBEAT WebID oli:" + webid);
		System.out.println("DRUMBEAT req uri oli:" + request_uri);
		System.out.println("DRUMBEAT canonized uri oli:" + canonizted_requestURI);
		
		log.info("DRUMBEAT WebID oli:" + webid);
		log.info("DRUMBEAT req uri oli:" + request_uri);
		log.info("DRUMBEAT canonized uri oli:" + canonizted_requestURI);

		final List<RDFNode> matched_paths = new ArrayList<>();
		rdf_datastore.ifPresent(x -> x.match(matched_paths, canonizted_requestURI.toString()));
		
		for (RDFNode r : matched_paths) {
			System.out.println("match: " + r.toString());
			//rdf_datastore.ifPresent(x -> 
			RDFDataStore x=rdf_datastore.get();
			{
				Resource current_node = x.getModel().getResource(r.toString());
				
				List<Resource> rulepath_list = null;
				Resource authorizationRule=r.asResource().getPropertyResourceValue(RDFConstants.property_hasAuthorizationRule);
				if(authorizationRule!=null) {
					Resource rule_path=authorizationRule.getPropertyResourceValue(RDFConstants.property_hasRulePath);
					Resource path_root=rule_path.getPropertyResourceValue(RDFConstants.property_hasPath);
					rulepath_list = x.parseRulePath(path_root); 
				}
				
				rulepath_list = rulepath_list.stream().filter(rule -> !((Resource) rule).isLiteral()).collect(Collectors.toList());
				ListIterator<Resource> iterator = rulepath_list.listIterator();
				while (iterator.hasNext()) {
					Resource step = iterator.next();
					Property p = x.getModel().getProperty(step.getURI());
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
								log.info("Equals");
								List<String> perms=x.getPermissions(r.toString()).stream().map(y->{
									String sy=y.asResource().getURI();
									int i=sy.lastIndexOf("/");
									sy=sy.substring(i+1);
									return sy;
								}).collect(Collectors.toCollection(ArrayList::new));
								ret.addAll(perms); 
							}
							else
							{
								System.out.println("Not Equals: "+node.toString());
								log.info("Not Equals: "+node.toString());
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
						
						
						if(checkPath_HTTP(current_node.getURI(),webid,new_path)) {
							System.out.println("remote "+current_node.getURI()+" says OK");
							log.info("remote "+current_node.getURI()+" says OK");
							List<String> perms=x.getPermissions(r.toString()).stream().map(y->{
								String sy=y.asResource().getURI();
								int i=sy.lastIndexOf("/");
								sy=sy.substring(i+1);
								return sy;
							}).collect(Collectors.toCollection(ArrayList::new));
							ret.addAll(perms); 
						}
						break;
					}
				}
	
			//});
			}

		}
		
		return ret;
	}
	public boolean checkPath_HTTP(String nextStepURL,String webid,List<Resource> new_path ) {
		final Model query_model = ModelFactory.createDefaultModel();
		System.out.println("Next step URL is: "+nextStepURL);
		try {
			RDFNode[] rulepath_list = new RDFNode[new_path.size()];
			for(int i=0;i<new_path.size();i++)
			{
			   rulepath_list[i] = new_path.get(i);
			}
			RDFList rulepath = query_model.createList(rulepath_list);
			Resource query = query_model.createResource();
			query.addProperty(RDFConstants.property_hasRulePath, rulepath);

			Literal time_inMilliseconds = query_model.createTypedLiteral(new Long(System.currentTimeMillis()));
			query.addProperty(RDF.type, RDFConstants.Query);
			query.addLiteral(RDFConstants.property_hasTimeStamp, time_inMilliseconds);
			query.addProperty(RDFConstants.property_hasWebID, query_model.getResource(webid));

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

			final Model response_model = ModelFactory.createDefaultModel();
			response_model.read(new ByteArrayInputStream( response_txt.getBytes()), null, "JSON-LD");
			
			
			ResIterator iter = response_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
			
			
			
			Resource result = null;
			if (iter.hasNext())
				result = iter.next();
			
			RDFNode time_stamp = result.getProperty(RDFConstants.property_hasTimeStamp).getObject();
			return result.getProperty(RDFConstants.property_status).getObject().asLiteral().getBoolean();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	


	public URI canonizateURI(String uri_txt) {
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
