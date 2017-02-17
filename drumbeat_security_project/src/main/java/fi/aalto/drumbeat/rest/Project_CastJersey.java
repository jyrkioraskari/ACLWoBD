package fi.aalto.drumbeat.rest;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;


@SuppressWarnings("serial")
@WebServlet(
        urlPatterns = "/security/*",
        loadOnStartup = 1,
        asyncSupported = true,
        initParams =
        {
            @WebInitParam(name = "jersey.config.server.provider.packages", value = "fi.aalto.drumbeat.Project_RESTfulAPI"),
        }
)

public class Project_CastJersey extends com.sun.jersey.spi.container.servlet.ServletContainer {
    
}
