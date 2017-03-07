/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fi.aalto.cs.drumbeat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.java.dev.sommer.foafssl.claims.WebIdClaim;
import net.java.dev.sommer.foafssl.claims.X509Claim;

public class CertificateAuthenticator extends AuthenticatorBase {
	private static final Log log = LogFactory.getLog(CertificateAuthenticator.class);

	public boolean authenticate(Request request, HttpServletResponse response) throws IOException {
		log.info("DRUM WEBID authenticate started");
		// TODO laita johonkin vakiopaikkaan
		// Miten suhtautuu truststoreen?
		System.setProperty("javax.net.ssl.trustStore", "c:\\jo\\certs\\keystore.jks");

		if (checkForCachedAuthentication(request, response, false)) {
			return true;
		}
		X509Certificate certs[] = getRequestCertificates(request);

		if ((certs == null) || (certs.length < 1)) {
			log.info("DRUM cert  No certificates included with this request");
			if (containerLog.isDebugEnabled()) {
				containerLog.debug("  No certificates included with this request");
			}
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, sm.getString("authenticator.certificates"));
			return false;
		}
		for (X509Certificate cert : certs) {
			Collection<? extends WebIdClaim> pls = null;
			try {
				X509Claim x509Claim = new X509Claim(cert);

				if (x509Claim.verify()) {
					pls = x509Claim.getVerified();
					if (pls == null || pls.isEmpty()) {
						response.getOutputStream().write("No foaf+ssl certificates".getBytes());
					} else {
						log.info("DRUMBEAT WEBID cert verified OK");
						try {
							for (Object altlist : cert.getSubjectAlternativeNames().toArray()) {
								for (Object alt : (Collection) altlist) {
									if (String.class.isInstance(alt)) {
										log.info("DRUMBEAT WEBID cert alt class:" + alt.getClass().getName());
										String[] roles_list=server_connect((String) alt.toString(), request.getRequestURL().toString()).split(",");
										final List<String> roles = new ArrayList<String>();
										roles.add("default");
										for(String r:roles_list)
											roles.add(r);
										
										Principal principal = new GenericPrincipal(alt.toString(), "pass",
												roles);
										if (principal != null) {
											log.info("DRUMBEAT WEBID cert verified RETURNED principal!");
											request.setAttribute("CertDN", cert.getSubjectDN().getName());
											register(request, response, principal, "DRUMBEAT_AUTHENTICATION",
													alt.toString(), "pass");
											response.setHeader("Cache-Control", "no-cache,must-revalidate");
											response.addHeader("Cache-Control", "post-check=0,pre-check=0");
											response.addHeader("Cache-Control", "proxy-revalidate");
											response.setHeader("Pragma", "no-cache");
											response.setDateHeader("Expires", 0);
											return true;
										}
									} else
										log.info("DRUMBEAT WEBID cert verified RETURNED principal null??! "+alt.toString());
								}
							}
						} catch (CertificateParsingException e) {
							e.printStackTrace();
						}
					}
				} else
					log.info("DRUMBEAT WEBID cert verification is not OK");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, sm.getString("authenticator.certificates"));
		return false; // temporary
	}

	private String server_connect(String alt, String requestURL) {
		log.info("DRUMBEAT .... server connect alt: " + alt);
		log.info("DRUMBEAT .... server connect requestURL: " + requestURL);
		JSONObject obj = new JSONObject();
		obj.put("alt_name", alt);
		obj.put("requestURL", requestURL);
		try {
			// DO NOT CHANGE THIS
			// HTTPS if not a local connection!
			final String httpsURL = "http://127.0.0.1/security/";
			URL myurl = new URL(httpsURL);
			HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(obj.toJSONString().length()));
			conn.setUseCaches(false);
			try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write(obj.toJSONString().getBytes());
			}
			InputStream ins = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(isr);

			String inputLine;

			String response="";
			while ((inputLine = in.readLine()) != null) {
				response+=inputLine;
			}
			in.close();

			JSONParser parser = new JSONParser();
			try {
				JSONObject response_obj = (JSONObject)parser.parse(response);
				String status=(String) response_obj.get("status");
				String roles=(String) response_obj.get("roles");
				return roles;
			} catch (ParseException e) {
				e.printStackTrace();
			}

			log.info("DRUMBEAT .... server connect passed");
		} catch (IOException e) {

			e.printStackTrace();
		}
		return ""; // No roles
	}

	@Override
	protected String getAuthMethod() {
		log.info("DRUMBEAT getAuthMethod called");
		return "DRUMBEAT";
	}

	@Override
	protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
		return authenticate(request, response);
	}

}
