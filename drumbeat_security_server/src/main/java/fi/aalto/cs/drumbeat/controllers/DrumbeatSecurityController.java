package fi.aalto.cs.drumbeat.controllers;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.utils.Tuple;

import fi.aalto.drumbeat.RDFConstants;
import fi.aalto.drumbeat.RDFDataStore;

public class DrumbeatSecurityController {
	final static private List<Tuple<String,Resource>> unseen_locals=new ArrayList<>();
	final static private List<Tuple<String,Long>> access_list=new ArrayList<>();
	
	
	public static List<Tuple<String, Resource>> getUnseenLocals() {
		return unseen_locals;
	}

	public static List<Tuple<String, Long>> getAccessList() {
		return access_list;
	}


	private URI rootURI;
	private final Model datamodel;
	private final Resource root;

	private static DrumbeatSecurityController singleton = null;

	public static DrumbeatSecurityController getOrganizationManager(URI uri) {
		if (singleton == null)
			singleton = new DrumbeatSecurityController(uri);
		return singleton;

	}

	private DrumbeatSecurityController(URI uri) {
		super();
		rootURI = uri;
		rdf_datastore = new RDFDataStore(rootURI, "organization");
		datamodel = rdf_datastore.getModel();
		// rdf_datastore.readRDFData();
		root = datamodel.getResource(rootURI.toString());
		registerWebID("https://jyrkio2.databox.me/profile/card#me", "1234");
	}



	private RDFDataStore rdf_datastore = null;
	public boolean checkRDFPath(String webid_uri, Resource path) {
		return validatePath(null,webid_uri, path);
	}
	private boolean validatePath(Resource previous_node,String webid_uri, Resource path) {
		LinkedList<Resource> rulepath = parseRulePath(path);
		access_list.add(new Tuple<String, Long>(webid_uri,System.currentTimeMillis()));
		Resource current_node = root;
		if(previous_node!=null)
		  current_node=previous_node;
		ListIterator<Resource> iterator = rulepath.listIterator();
		while (iterator.hasNext()) {
			Resource step = iterator.next();
			Property p = rdf_datastore.getModel().getProperty(step.getURI());
			StmtIterator connected_triples = current_node.listProperties(p);
			while (connected_triples.hasNext()) {
				Statement triple = connected_triples.next();
				Resource node = triple.getObject().asResource();
				if (node != null) {
					System.out.println("from local store:" + node);
					current_node = node;
					if (!iterator.hasNext()) {
						if (current_node.toString().equals(webid_uri))
							return true;
					}
					else
					{
						List<Resource> new_path = rulepath.subList(rulepath.indexOf(step), rulepath.size());
						return validatePath(current_node,webid_uri, new_path.get(0));
					}
				} else {
					System.out.println("located somewhere else. current node was: " + current_node);

					List<Resource> new_path = rulepath.subList(rulepath.indexOf(step), rulepath.size());
					System.out.println("Path for the rest is:" + new_path);
					return checkPath_HTTP(current_node.toString(), webid_uri, new_path);
				}
			}
		}

		saveUnsucceeLocal(webid_uri, path);
		return false;
	}

	private void saveUnsucceeLocal(String webid_uri, Resource path) {
        System.out.println("unsuccessful webid: "+webid_uri);
        unseen_locals.add(new Tuple<String, Resource>(webid_uri,path));
        
	}

	public boolean checkPath_HTTP(String nextStepURL, String webid, List<Resource> new_path) {
		final Model query_model = ModelFactory.createDefaultModel();
		System.out.println("Next step URL is: " + nextStepURL);
		try {
			RDFNode[] rulepath_list = new RDFNode[new_path.size()];
			for (int i = 0; i < new_path.size(); i++) {
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

			final Model response_model = ModelFactory.createDefaultModel();
			response_model.read(new ByteArrayInputStream(response_txt.getBytes()), null, "JSON-LD");

			ResIterator iter = response_model.listSubjectsWithProperty(RDFConstants.property_hasTimeStamp);
			Resource result = null;
			if (iter.hasNext())
				result = iter.next();

			@SuppressWarnings("unused")
			RDFNode time_stamp = result.getProperty(RDFConstants.property_hasTimeStamp).getObject();
			//TODO check the time_stamp
			return result.getProperty(RDFConstants.property_status).getObject().asLiteral().getBoolean();
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Resource getWebIDProfile(String webid_uri) {
		return datamodel.getResource(webid_uri.toString());
	}

	public Resource registerWebID(String webidURI, String public_key) {
		rdf_datastore.saveRDFData();
		Resource widr = datamodel.getResource(webidURI);
		root.addProperty(RDFConstants.property_knowsPerson, widr);
		widr.addLiteral(RDFConstants.property_hasPublicKey, public_key);
		return widr;
	}

	public LinkedList<Resource> parseRulePath(Resource node) {
		LinkedList<Resource> ret = new LinkedList<Resource>();

		Resource current = node;
		while (current != null && current.asResource().hasProperty(RDF.rest)) {
			if (current.hasProperty(RDF.first))
				ret.add(current.getPropertyResourceValue(RDF.first));
			current = current.getPropertyResourceValue(RDF.rest);
		}

		return ret;
	}
	
	/*
	public Model getWebID(String webid) {
		//http://stackoverflow.com/questions/1820908/how-to-turn-off-the-eclipse-code-formatter-for-certain-sections-of-java-code
		// @formatter:off
		String queryString =	 "CONSTRUCT { \n" +
								"	?webid ?p ?o \n" + 
								"} WHERE { \n" + 
								"     ?webid ?p ?o . \n" + 
								"} \n";
		// @formatter:on
		ParameterizedSparqlString ps = new ParameterizedSparqlString(queryString);
		
		ps.setIri("webid", webid);
		Query query=ps.asQuery();
		Model result = QueryExecutionFactory.create(query, datamodel).execConstruct();
		return result;
	}*/
}