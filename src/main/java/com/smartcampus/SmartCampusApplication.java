package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application entry point.
 * @ApplicationPath sets the base URL for all resources to /api/v1
 * The empty body causes Payara to auto-scan for @Path and @Provider classes.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Empty - Payara scans the WAR automatically for all JAX-RS components
}