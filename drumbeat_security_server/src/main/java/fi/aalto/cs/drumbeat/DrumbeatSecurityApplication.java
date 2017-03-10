package fi.aalto.cs.drumbeat;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;


public class DrumbeatSecurityApplication extends ResourceConfig {
	
	
	public static class DrumbeatAuthFilter implements ContainerRequestFilter {

		private static final Log log = LogFactory.getLog(DrumbeatAuthFilter.class);

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            String authentication = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            SecurityContext sc=requestContext.getSecurityContext();
            if(sc==null)
            	return;
            System.out.println(sc.getUserPrincipal().getName());
            log.info("DrumbeatAuthFilter webid: "+sc.getUserPrincipal().getName());
            requestContext.setSecurityContext(new DrumbeatSecurityContext("webid"));
        }
    }

    static class DrumbeatSecurityContext implements SecurityContext {
    	private final String webid;

        public DrumbeatSecurityContext(String webid) {
            this.webid=webid;
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
            return role.equals("possible");  // TODO add listed roles
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
		packages("fi.aalto.drumbeat.rest");
		register(LoggingFeature.class);
        register(JspMvcFeature.class);
        property("jersey.config.server.mvc.templateBasePath", "/WEB-INF/jsp");
        register(DrumbeatAuthFilter.class);
        register(RolesAllowedDynamicFeature.class);
        register(MvcFeature.class);
	}

	
}