/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;


@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER =
            Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Called BEFORE the request reaches any resource method.
     * Logs the HTTP method and URI of every incoming request.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info("=== Incoming Request ===");
        LOGGER.info("Method : " + requestContext.getMethod());
        LOGGER.info("URI    : " + requestContext.getUriInfo().getAbsolutePath());
    }

    /**
     * Called AFTER the resource method has produced a response.
     * Logs the HTTP status code of every outgoing response.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOGGER.info("=== Outgoing Response ===");
        LOGGER.info("Status : " + responseContext.getStatus());
    }
}
