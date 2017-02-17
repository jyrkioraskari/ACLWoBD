package fi.aalto.drumbeat.rest;

import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;

public class RESTfulAPI {

	protected String getJSON_LDContent() {
		System.out.println("Send triples...");
		StringWriter writer = new StringWriter();
		Model ret = null;//filterJavaFromModel();
		ret.write(writer, "JSON-LD");
		return writer.toString();
	}

}
