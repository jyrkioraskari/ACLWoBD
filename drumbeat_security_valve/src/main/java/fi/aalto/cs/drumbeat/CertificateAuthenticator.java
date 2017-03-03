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

import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import fi.aalto.drumbeat.DataServer;
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
							for (Object alt : cert.getSubjectAlternativeNames().toArray()) {
								DataServer dataserver = DataServer.getDataServer(request.getRequestURL().toString());
								dataserver.connect((String) alt, request.getRequestURL().toString());
								response.getOutputStream().write("WEBID foaf+ssl certificate OK".getBytes());
								
							}
						} catch (CertificateParsingException e) {
							e.printStackTrace();
						}
					}
				} else
					log.info("DRUM WEBID cert verification is not OK");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, sm.getString("authenticator.certificates"));
		return false; // temporary
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
