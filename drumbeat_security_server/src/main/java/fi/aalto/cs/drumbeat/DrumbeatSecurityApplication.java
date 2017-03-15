package fi.aalto.cs.drumbeat;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

import fi.aalto.cs.drumbeat.controllers.AuthenticationController;


public class DrumbeatSecurityApplication extends ResourceConfig {
	
	
	public static class DrumbeatAuthFilter implements ContainerRequestFilter {

		private static final Log log = LogFactory.getLog(DrumbeatAuthFilter.class);

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            String authentication = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            SecurityContext sc=requestContext.getSecurityContext();
            if(sc==null)
            	return;
            if(sc.getUserPrincipal()==null)
            	return;
            log.info("DrumbeatAuthFilter webid: "+sc.getUserPrincipal().getName());
            UriInfo uriInfo = requestContext.getUriInfo();
            URI requestUri = uriInfo.getRequestUri();
            log.info("DrumbeatAuthFilter req url: "+requestUri.toString());

            AuthenticationController ds=AuthenticationController.getAuthenticationController(requestUri.toString());
            final List<String> roles  = ds.autenticate(sc.getUserPrincipal().getName(), requestUri.toString());
			roles.add("default");
			log.info("DrumbeatAuthFilter Tomcat ROLES are:"+roles.stream()
				     .collect(Collectors.joining(",")));
			
            requestContext.setSecurityContext(new DrumbeatSecurityContext(sc.getUserPrincipal().getName(),roles));
        }
    }

    static class DrumbeatSecurityContext implements SecurityContext {
    	private final String webid;
    	final List<String> roles;
        public DrumbeatSecurityContext(String webid,List<String> roles ) {
            this.webid=webid;
            this.roles=roles;
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
        public boolean isSecure() { return true; }

        @Override
        public String getAuthenticationScheme() {
            return "DRUMBEAT_SECURITY";
        }

    }

    static class AuthenticationException extends WebApplicationException {

        public AuthenticationException(String message) {
            super(Response
                    .status(Status.UNAUTHORIZED)
                    .header("Drumbeat Authenticate", "Basic realm=\"" + "Dummy Realm" + "\"")
                    .type("text/plain")
                    .entity(message)
                    .build());
        }
    }
	
	public DrumbeatSecurityApplication() {
		packages("fi.aalto.cs.drumbeat");
		register(LoggingFeature.class);
        register(JspMvcFeature.class);
        property("jersey.config.server.mvc.templateBasePath", "/WEB-INF/jsp");
        register(DrumbeatAuthFilter.class);
        register(RolesAllowedDynamicFeature.class);
        register(MvcFeature.class);
	}

	
}