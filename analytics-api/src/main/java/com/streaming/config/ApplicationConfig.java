package com.streaming.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application configuration
 */
@ApplicationPath("/")
public class ApplicationConfig extends Application {
    // The resources will be auto-discovered by the container
}
