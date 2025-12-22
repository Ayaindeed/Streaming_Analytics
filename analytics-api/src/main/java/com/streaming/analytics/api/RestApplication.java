package com.streaming.analytics.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration JAX-RS pour l'API REST
 */
@ApplicationPath("/")
public class RestApplication extends Application {
    // La configuration est automatique avec les annotations @Path
}