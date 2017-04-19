package fi.aalto.cs.drumbeat;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

import fi.aalto.cs.drumbeat.controllers.AccessController;

public class DrumbeatSecureApplication extends ResourceConfig {

	public static class DrumbeatAuthFilter implements ContainerRequestFilter {

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
								
								
								/*if (sc == null)
								{
									//TODO Test the PK
									return;
								}
								if (sc.getUserPrincipal() == null)
									return;
								log.info("DrumbeatAuthFilter webid: " + sc.getUserPrincipal().getName());*/
								UriInfo uriInfo = requestContext.getUriInfo();
								URI requestUri = uriInfo.getRequestUri();
								log.info("DrumbeatAuthFilter req url: " + requestUri.toString());

								AccessController ds = AccessController.getAuthenticationController(requestUri.toString());
								//final List<String> roles = ds.autenticate(sc.getUserPrincipal().getName(), requestUri.toString());
								final List<String> roles = ds.grantPermissions(alt.toString(), requestUri.toString());
								roles.add("default");
								log.info("DrumbeatAuthFilter Tomcat ROLES are:" + roles.stream().collect(Collectors.joining(",")));

								//requestContext.setSecurityContext(new DrumbeatSecurityContext(sc.getUserPrincipal().getName(), roles));
								requestContext.setSecurityContext(new DrumbeatSecurityContext(alt.toString(), roles));
								return;
							}
						}
					}
				} catch (CertificateParsingException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("filter no certs");
			}
			
 
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("User cannot access the resource.")
                    .build());
        
	
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