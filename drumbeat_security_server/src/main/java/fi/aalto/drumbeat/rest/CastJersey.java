package fi.aalto.drumbeat.rest;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;


@SuppressWarnings("serial")
@WebServlet(
        urlPatterns = "/resource/*",
        loadOnStartup = 1,
        asyncSupported = true,
        initParams =
        {
            @WebInitParam(name = "jersey.config.server.provider.packages", value = "fi.aalto.drumbeat.rest"),
        }
)

public class CastJersey extends com.sun.jersey.spi.container.servlet.ServletContainer {
    
}
