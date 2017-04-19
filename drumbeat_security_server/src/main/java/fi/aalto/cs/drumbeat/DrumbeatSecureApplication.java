package fi.aalto.cs.drumbeat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

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
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

import fi.aalto.cs.drumbeat.controllers.AccessController;
import net.java.dev.sommer.foafssl.claims.WebIdClaim;
import net.java.dev.sommer.foafssl.claims.X509Claim;
import net.java.dev.sommer.foafssl.verifier.FoafSslVerifier;

public class DrumbeatSecureApplication extends ResourceConfig {

	public static class DrumbeatAuthFilter extends FoafSslVerifier implements ContainerRequestFilter {

		private static final Log log = LogFactory.getLog(DrumbeatAuthFilter.class);

		@Override
		public void filter(ContainerRequestContext requestContext) throws IOException {
			String authentication = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

			X509Certificate[] chain = (X509Certificate[]) requestContext
					.getProperty("javax.servlet.request.X509Certificate");

			if (chain != null && chain.length > 0) {
				X509Certificate certificate = chain[0];
				try {
					for (Object altlist : certificate.getSubjectAlternativeNames().toArray()) {
						for (Object alt : (Collection) altlist) {
							if (String.class.isInstance(alt)) {
								System.out.println("filter alt: " + alt);

								SecurityContext sc = requestContext.getSecurityContext();

								Collection<? extends WebIdClaim> pls = null;
								X509Claim x509Claim = new X509Claim(certificate);
								if (x509Claim.verify()) {
									pls = x509Claim.getVerified();
									if (pls == null || pls.isEmpty()) {
										requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
												.entity("User cannot access the resource.").build());
									}
								} else
									log.info("DRUM WEBID cert verification is not OK");

								UriInfo uriInfo = requestContext.getUriInfo();
								URI requestUri = uriInfo.getRequestUri();
								log.info("DrumbeatAuthFilter req url: " + requestUri.toString());

								AccessController ds = AccessController
										.getAuthenticationController(requestUri.toString());
								// final List<String> roles =
								// ds.autenticate(sc.getUserPrincipal().getName(),
								// requestUri.toString());
								final List<String> roles = ds.grantPermissions(alt.toString(), requestUri.toString());
								roles.add("default");
								log.info("DrumbeatAuthFilter Tomcat ROLES are:"
										+ roles.stream().collect(Collectors.joining(",")));

								// requestContext.setSecurityContext(new
								// DrumbeatSecurityContext(sc.getUserPrincipal().getName(),
								// roles));
								requestContext.setSecurityContext(new DrumbeatSecurityContext(alt.toString(), roles));
								return;
							}
						}
					}
				} catch (CertificateParsingException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("filter no certs");
			}

			requestContext.abortWith(
					Response.status(Response.Status.UNAUTHORIZED).entity("User cannot access the resource.").build());

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
			System.out.println("num:  " + num);
			System.out.println("tpe:  " + tpe);
			return null;
		}

		static final private char[] hexchars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'a', 'B', 'b',
				'C', 'c', 'D', 'd', 'E', 'e', 'F', 'f' };

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

		@Override
		public boolean verify(WebIdClaim webid) {
			PublicKey publicKey = webid.getVerifiedPublicKey();
			if (publicKey instanceof RSAPublicKey) {
				RSAPublicKey certRsakey = (RSAPublicKey) publicKey;

				System.setProperty("javax.net.ssl.trustStore", "c:\\jo\\certs\\keystore.jks");

				String profile_content = getResponse(webid.getGraphName().toString());

				Model model = parseInput(profile_content.toString());

				// The query was originally in FOAF+SSL by Story
				// and Harbulot
				String req = "PREFIX cert: <http://www.w3.org/ns/auth/cert#>"
						+ "PREFIX rsa: <http://www.w3.org/ns/auth/rsa#>" + "SELECT ?m ?e ?mod ?exp FROM <"
						+ webid.getGraphName().toString() + ">" + "WHERE {  { ?key cert:identity ?agent }  "
						+ "UNION  { ?agent cert:key ?key } " + "	 ?key cert:modulus ?m ;       "
						+ "cert:exponent ?e .   " + "OPTIONAL { ?m cert:hex ?mod . }   "
						+ "OPTIONAL { ?e cert:decimal ?exp . }}";
				Query query = QueryFactory.create(req);
				QueryExecution qe = QueryExecutionFactory.create(query, model);
				ResultSet res = qe.execSelect();
				while (res.hasNext()) {
					QuerySolution solution = res.next();

					RDFNode m = solution.get("m");
					RDFNode e = solution.get("e");
					RDFNode mod = solution.get("mod");
					RDFNode exp = solution.get("exp");

					BigInteger publicExponent = certRsakey.getPublicExponent();
					BigInteger modulus = certRsakey.getModulus();
					// 1. find the exponent
					BigInteger exp_int = toInteger(e, cert + "decimal", exp);
					if (exp == null || !exp.equals(publicExponent)) {
						;// return false; // access denied
					}
					System.out.println("exp_int: " + exp_int);

					// 2. Find the modulus
					BigInteger mod_int = toInteger(m, cert + "hex", mod);
					if (mod == null || !mod.equals(modulus)) {
						;// return false; // access debied
					}
					System.out.println("mod_int:  " + mod_int);
				}

				return true;
			}
			return false;
		}

	}

	static class DrumbeatSecurityContext implements SecurityContext {
		private final String webid;
		final List<String> roles;

		public DrumbeatSecurityContext(String webid, List<String> roles) {
			this.webid = webid;
			this.roles = roles;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() {
				@Override
				public String getName() {
					return webid;
				}
			};
		}

		@Override
		public boolean isUserInRole(String role) {
			return roles.contains(role);
		}

		@Override
		public boolean isSecure() {
			return true;
		}

		@Override
		public String getAuthenticationScheme() {
			return "DRUMBEAT_SECURITY";
		}

	}

	static class AuthenticationException extends WebApplicationException {

		public AuthenticationException(String message) {
			super(Response.status(Status.UNAUTHORIZED)
					.header("Drumbeat Authenticate", "Basic realm=\"" + "Dummy Realm" + "\"").type("text/plain")
					.entity(message).build());
		}
	}

	public DrumbeatSecureApplication() {
		packages("fi.aalto.cs.drumbeat");
		register(LoggingFeature.class);
		register(JspMvcFeature.class);
		property("jersey.config.server.mvc.templateBasePath", "/WEB-INF/jsp");
		register(DrumbeatAuthFilter.class);
		register(RolesAllowedDynamicFeature.class);
		register(MvcFeature.class);
	}

}