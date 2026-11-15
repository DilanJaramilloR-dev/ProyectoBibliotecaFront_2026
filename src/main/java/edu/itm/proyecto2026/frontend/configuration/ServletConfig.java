package edu.itm.proyecto2026.frontend.configuration;

import edu.itm.proyecto2026.frontend.servlets.CatalogoServlet;
import edu.itm.proyecto2026.frontend.webservicesclients.CatalogoApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<CatalogoServlet> catalogoServletRegistration(
            CatalogoApiClient catalogoClient,
            @Value("${api.base-url}") String urlBackend) {
                ServletRegistrationBean<CatalogoServlet> registration = new ServletRegistrationBean<>(new CatalogoServlet(catalogoClient, urlBackend), "/front/*");
                registration.setLoadOnStartup(1);
            return registration;
    }
}