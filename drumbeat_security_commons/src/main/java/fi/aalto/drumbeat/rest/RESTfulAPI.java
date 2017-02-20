package fi.aalto.drumbeat.rest;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriInfo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class RESTfulAPI {
	URI base_url;

	protected Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "JSON-LD");
		return json_input_model;
	}

	protected String writeModel(Model model) {
		StringWriter writer = new StringWriter();
		model.write(writer, "JSON-LD");
		return writer.toString();
	}

	public void setBaseURI(UriInfo uriInfo) {		
		try {
			base_url=new URI(uriInfo.getBaseUri().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
