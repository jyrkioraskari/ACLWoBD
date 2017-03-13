package fi.aalto.drumbeat.rest;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;

import javax.ws.rs.core.UriInfo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.drumbeat.RDFConstants;


public class RESTfulAPI {
	private URI base_url;

	protected Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "JSON-LD");
		
		return json_input_model;
	}


	protected Resource getQuery(Model model) {
		ResIterator iter = model.listSubjectsWithProperty(RDFConstants.Message.hasTimeStamp);
		Resource query = null;
		if (iter.hasNext())
			query = iter.next();
		return query;
	}

	protected String writeModel(Model model) {
		StringWriter writer = new StringWriter();
		model.write(writer, "JSON-LD");
		return writer.toString();
	}

	
	public URI getBase_url() {
		return base_url;
	}


	public void setBaseURI(UriInfo uriInfo) {
		this.base_url = uriInfo.getRequestUri();
		
	}

}
