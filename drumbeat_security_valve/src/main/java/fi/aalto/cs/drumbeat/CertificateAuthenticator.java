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

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;


public class CertificateAuthenticator extends AuthenticatorBase {
    private static final Log log = LogFactory.getLog(CertificateAuthenticator.class);

    public boolean authenticate(Request request, HttpServletResponse response)
            throws IOException {

    	if (checkForCachedAuthentication(request, response, false)) {
            return true;
        }
    	 X509Certificate certs[] = getRequestCertificates(request);

         if ((certs == null) || (certs.length < 1)) {
        	 log.info("DRUM cert  No certificates included with this request");
             if (containerLog.isDebugEnabled()) {
                 containerLog.debug("  No certificates included with this request");
             }
             response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                     sm.getString("authenticator.certificates"));
             return false;
         }
        for(X509Certificate cert: certs)
        {
        	try {
				for(Object alt:cert.getSubjectAlternativeNames().toArray()){        	
					log.info("DRUM cert call getRequestURI was:"+request.getRequestURI()+" webid:"+alt);
					log.info("DRUM cert getRequestURI:"+request.getRequestURL()+" webid:"+alt);
				    log.info("DRUM cert getLocalAddr was:"+request.getLocalAddr()+" webid:"+alt);
				    log.info("DRUM cert getPathTranslated was:"+request.getPathTranslated()+" webid:"+alt);
				}
			} catch (CertificateParsingException e) {
				e.printStackTrace();
			}
        	
        }
    	
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                 sm.getString("authenticator.certificates"));    	
    	return false; // temporary
    }

    @Override
    protected String getAuthMethod() {
    	log.info("DRUMBEAT getAuthMethod called");
        return "DRUMBEAT";
    }

	@Override
	protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
		return authenticate(request,response);
	}



}
