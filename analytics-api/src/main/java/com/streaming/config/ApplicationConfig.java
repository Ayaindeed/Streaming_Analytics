package com.streaming.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;

/**
 * JAX-RS Application configuration with CDI support
 */
@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {
    
    public ApplicationConfig() {
        // Register packages for resource scanning
        packages("com.streaming.api");
        
        // Enable CDI integration
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
    }
}
