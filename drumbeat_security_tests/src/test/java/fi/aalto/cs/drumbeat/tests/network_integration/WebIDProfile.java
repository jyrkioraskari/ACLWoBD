package fi.aalto.cs.drumbeat.tests.network_integration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.glassfish.jersey.internal.util.collection.Value;
import org.junit.Test;

public class WebIDProfile {

	@Test
	public void test() {

		System.setProperty("javax.net.ssl.trustStore", "c:\\jo\\certs\\keystore.jks");
		String webid = "https://jyrkio2.databox.me/profile/card#me";
		String profile_uri = webid.split("#")[0];

		String profile_content = getResponse(webid);

		// System.out.println(profile_content);
		Model model = parseInput(profile_content.toString());

		// The query was originally in FOAF+SSL by Story and Harbulot
		String req = "PREFIX cert: <http://www.w3.org/ns/auth/cert#>" + "PREFIX rsa: <http://www.w3.org/ns/auth/rsa#>"
				+ "SELECT ?m ?e ?mod ?exp FROM <" + profile_uri + ">" + "WHERE {  { ?key cert:identity ?agent }  "
				+ "UNION  { ?agent cert:key ?key } " + "	 ?key cert:modulus ?m ;       " + "cert:exponent ?e .   "
				+ "OPTIONAL { ?m cert:hex ?mod . }   " + "OPTIONAL { ?e cert:decimal ?exp . }}";
		Query query = QueryFactory.create(req);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet res = qe.execSelect();
		while (res.hasNext()) {
			QuerySolution solution = res.next();
			
			RDFNode m=solution.get("m");
			RDFNode e=solution.get("e");
			RDFNode mod=solution.get("mod");
			RDFNode exp=solution.get("exp");
			System.out.println("m: " + m);
			System.out.println("e: " + e);
			System.out.println("mod: " + e);
			System.out.println("exp: " + exp);			
			
			String publicExponent="";
			String modulus="";
			// 1. find the exponent
            BigInteger exp_int = toInteger(e, cert + "decimal",
                    exp);
            if (exp == null || !exp.equals(publicExponent)) {
            	//false
            }
            System.out.println("exp_int: "+exp_int);

            // 2. Find the modulus
            BigInteger mod_int = toInteger(m, cert + "hex", mod);
            if (mod == null || !mod.equals(modulus)) {
            	//false
            }
            System.out.println("mod_int:  "+mod_int);
		}

	}
	

	// Originally in FOAF+SSL by Story and Harbulot
	
	BigInteger toInteger(RDFNode numVal, String optRel, RDFNode optstr) {
        if (null == numVal)
            return null;
        
        if (numVal instanceof Literal) { // we do in fact have "ddd"^^type
            Literal ln = (Literal) numVal;
            String type = ln.getDatatypeURI();
            return toInteger_helper(ln.getLexicalForm(), type);
        } else if (numVal instanceof Resource) { // we had _:n type "ddd" .
            if (optstr != null && optstr instanceof Literal) {
                Literal ls = (Literal) optstr;
                return toInteger_helper(ls.getLexicalForm(), optRel);
            }
        }
        return null;
    }

    final static String cert = "http://www.w3.org/ns/auth/cert#";
    final static String xsd = "http://www.w3.org/2001/XMLSchema#";

	private BigInteger toInteger_helper(String num, String tpe) {
		if (tpe.equals(cert + "decimal") || tpe.equals(cert + "int") || tpe.equals(xsd + "integer")
				|| tpe.equals(xsd + "int") || tpe.equals(xsd + "nonNegativeInteger")) {
			// cert:decimal is deprecated
			return new BigInteger(num.trim(), 10);
		} else if (tpe.equals(cert + "hex")) {
			String strval = cleanHex(num);
			return new BigInteger(strval, 16);
		}
		// addition by JO
		else if (tpe.equals(xsd + "hexBinary")) {
			String strval = cleanHex(num);
			return new BigInteger(strval, 16);
		} else {
			// it could be some other encoding - one should really write a
			// special literal transformation class
		}
		System.out.println("null:  in toInteger_helper");
		System.out.println("num:  "+num);
		System.out.println("tpe:  "+tpe);
		return null;
	}

	static final private char[] hexchars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'a', 'B', 'b', 'C',
			'c', 'D', 'd', 'E', 'e', 'F', 'f' };

	// Originally in FOAF+SSL by Story and Harbulot
	private static String cleanHex(String strval) {
		StringBuffer cleanval = new StringBuffer();
		for (char c : strval.toCharArray()) {
			if (Arrays.binarySearch(hexchars, c) >= 0) {
				cleanval.append(c);
			}
		}
		return cleanval.toString();
	}

	private Model parseInput(String msg) {
		final Model json_input_model = ModelFactory.createDefaultModel();
		try {
			json_input_model.read(new ByteArrayInputStream(msg.getBytes()), null, "N3");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json_input_model;
	}

	public String getResponse(String url) {
		int timeout = 20000;

		HttpURLConnection c = null;
		try {
			URL u = new URL(url);
			c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("GET");
			c.setRequestProperty("Content-length", "0");
			c.setRequestProperty("Accept", "text/turtle");
			c.setUseCaches(false);
			c.setAllowUserInteraction(false);
			c.setConnectTimeout(timeout);
			c.setReadTimeout(timeout);

			c.connect();
			int status = c.getResponseCode();

			switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				return sb.toString();
			}

		} catch (MalformedURLException ex) {

		} catch (IOException ex) {
		} finally {
			if (c != null) {
				try {
					c.disconnect();
				} catch (Exception ex) {
				}
			}
		}
		return null;
	}
}
