/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getApiInfo() {
        // Build response as a plain String to guarantee clean JSON output
        String json = "{"
            + "\"version\": \"1.0\","
            + "\"name\": \"Smart Campus Sensor and Room Management API - vinushki fernando\","
            + "\"description\": \"RESTful API for managing campus rooms and IoT sensors\","
            + "\"contact\": \"w2120303@westminster.ac.uk\","
            + "\"resources\": {"
            +     "\"rooms\": \"/api/v1/rooms\","
            +     "\"sensors\": \"/api/v1/sensors\""
            + "}"
            + "}";
        return Response.ok(json).build();
    }
}
