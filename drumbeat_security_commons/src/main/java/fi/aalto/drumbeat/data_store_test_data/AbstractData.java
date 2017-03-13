package fi.aalto.drumbeat.data_store_test_data;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import fi.aalto.drumbeat.Constants;

public abstract class AbstractData {
	protected final Resource  self;
	protected final OntModel model;

	public AbstractData(URI uri,String name, OntModel model) {
		self=model.getResource(appendSlash(uri.toString())+name);
		Resource type=model.getResource(Constants.security_ontology_base+"#"+this.getClass().getSimpleName());
		self.addProperty(RDF.type, type);
		this.model=model;
	}

	// The base root is a global one 
	public AbstractData(String name, OntModel model) throws URISyntaxException {
		self=model.getResource(getRoot(new URI(Constants.security_ontology_base))+this.getClass().getSimpleName()+"/"+name);
		Resource type=model.getResource(Constants.security_ontology_base+"#"+this.getClass().getSimpleName());
		self.addProperty(RDF.type, type);
		this.model=model;
	}
	private String getRoot(URI uri)
	{
		try {
			URI created=new URIBuilder().setScheme("https").setHost(uri.getHost()).setPath("/security/").build();
			return created.toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return "https://";
	}
	
	
	private String appendSlash(String txt)
	{
		if(txt.endsWith("/"))
			return txt;
		else
			return txt+"/";
	}
}
