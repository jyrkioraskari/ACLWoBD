package fi.aalto.drumbeat.rest;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

import fi.aalto.drumbeat.ontology.Ontology;
import fi.aalto.drumbeat.util.MessageChecksum;

public class RESTfulAPI {
	private URI base_url;
	private MessageChecksum md5 = new MessageChecksum();
	private Map<String, Long> check_repeats = new HashMap<String, Long>();

	protected Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		try {
			json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "JSON-LD");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json_input_model;
	}

	protected boolean checkRepetition(RDFNode time_stamp, String msg) {
		long time = time_stamp.asLiteral().getLong();
		String sum = md5.getChecksumValue(time_stamp + ":" + msg);
		if (Math.abs(time - System.currentTimeMillis()) > 10000)
			return true;

		List<String> poistot = new LinkedList<String>();
		for (Map.Entry<String, Long> entry : check_repeats.entrySet()) {
			if (Math.abs(entry.getValue() - System.currentTimeMillis()) > 15000) {
				poistot.add(entry.getKey());
			}
		}
		for (String key : poistot)
			check_repeats.remove(key);

		if (check_repeats.get(sum) != null)
			return true;
		return false;
	}

	protected Resource getQuery(Model model) {
		ResIterator iter = model.listSubjectsWithProperty(Ontology.Message.hasTimeStamp);
		Resource query = null;
		if (iter.hasNext())
			query = iter.next();
		return query;
	}

	protected String modelToString(Model model) {
		StringWriter writer = new StringWriter();
		model.write(writer, "JSON-LD");
		return writer.toString();
	}

	public URI getBase_url() {
		return base_url;
	}

	protected void setBaseURI(UriInfo uriInfo) {
		this.base_url = uriInfo.getRequestUri();

	}

}
