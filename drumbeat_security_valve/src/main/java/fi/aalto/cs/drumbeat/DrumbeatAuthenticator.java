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
import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;



/**
 *
 * Extended from the original work of  Craig R. McClanahan
 */

public class DrumbeatAuthenticator extends AuthenticatorBase {
    private static final Log log = LogFactory.getLog(DrumbeatAuthenticator.class);

    @Override
	protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
    	return authenticate(request,response);
	}

   

    @Override
    public boolean authenticate(Request request, HttpServletResponse response)
            throws IOException {

        if (checkForCachedAuthentication(request, response, true)) {
            return true;
        }

        // Validate any credentials already included with this request
        MessageBytes authorization =
            request.getCoyoteRequest().getMimeHeaders()
            .getValue("authorization");

        if (authorization != null) {
            authorization.toBytes();
            ByteChunk authorizationBC = authorization.getByteChunk();
            BasicCredentials credentials = null;
            try {
                credentials = new BasicCredentials(authorizationBC);
                String username = credentials.getUsername();
                String password = credentials.getPassword();

                log.info("DrunbeatToken received. User: "+username);
                log.info("DrunbeatToken received. Password: "+password);

                Principal principal = context.getRealm().authenticate(username, password);
                if (principal != null) {
                    register(request, response, principal,
                        HttpServletRequest.BASIC_AUTH, username, password);
                    String avalue=authorizationBC.toString().substring(BasicCredentials.METHOD.length());
                    log.info("Drunbeat authorization token: "+avalue);
                    Cookie cookie = new Cookie("DrunbeatToken", avalue);
                    cookie.setPath("/");
                    cookie.setSecure(false);
                    cookie.setDomain("drb.cs.hut.fi");                    
                    cookie.setMaxAge(31536000);
                    response.addCookie(cookie);                    
                    log.info("DrunbeatToken accepted. User: "+username+" Cookie set: "+cookie.getValue());
                    return true;
                }
            }
            catch (Exception iae) {
                    log.info("Drumbeat Invalid Authorization" + iae.getMessage());
            }
        }
        else
        {
        	 Cookie[] cookies = request.getCookies();
             Cookie drunbeat_cookie = null;
             for(int i=0;cookies!=null && i<cookies.length;i++){
                 if (cookies[i].getName().equals("DrunbeatToken")){
                	 drunbeat_cookie = cookies[i];
                     break;
                 }
             }
             if(drunbeat_cookie == null){
                 log.info("DrunbeatToken not found");
             }else{
            	    log.info("DrunbeatToken found");
            	    BasicCredentials credentials = null;
                     try {
                         credentials = new BasicCredentials(BasicCredentials.METHOD+drunbeat_cookie.getValue());
                         String username = credentials.getUsername();
                         String password = credentials.getPassword();

                         Principal principal = context.getRealm().authenticate(username, password);
                         if (principal != null) {
                             register(request, response, principal,
                                 HttpServletRequest.BASIC_AUTH, username, password);
                             log.info("DrunbeatToken accepted. User: "+username);
                             return true;
                         }
                     }
                     catch (Exception iae) {
                    	 log.info("Drumbeat Invalid Authorization" + iae.getMessage());
                     }
                 
             }
        }

        // the request could not be authenticated, so reissue the challenge
        StringBuilder value = new StringBuilder(16);
        value.append("Basic realm=\"");
        value.append(getRealmName(context));
        value.append('\"');
        response.setHeader(AUTH_HEADER_NAME, value.toString());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        log.info("Drumbeat: authorization not accepted");
        return false;

    }

    @Override
    protected String getAuthMethod() {
        return "DRUMBEAT";
    }

	
	


}
