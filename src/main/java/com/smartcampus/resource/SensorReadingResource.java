/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorMessage;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Part 4 - Sub-Resource for Sensor Readings
 * This class handles: GET /api/v1/sensors/{sensorId}/readings
 *                 and: POST /api/v1/sensors/{sensorId}/readings
 *
 * It does NOT have @Path on the class - the path comes from the
 * sub-resource locator method in SensorResource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    // Constructor receives the sensorId from the sub-resource locator
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * Part 4.2 - GET /api/v1/sensors/{sensorId}/readings
     * Returns the full reading history for a specific sensor.
     */
    @GET
    public Response getReadings() {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(
                            "Sensor with ID '" + sensorId + "' was not found.",
                            404, "Not Found"))
                    .build();
        }
        List<SensorReading> history = DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(history).build();
    }

    /**
     * Part 4.2 - POST /api/v1/sensors/{sensorId}/readings
     * Appends a new reading to this sensor's history.
     *
     * Business rules:
     * 1. Sensor must exist
     * 2. Sensor must NOT be in "MAINTENANCE" status → 403 Forbidden
     * 3. After saving, update the sensor's currentValue (side effect)
     */
    @POST
    public Response addReading(SensorReading reading, @Context UriInfo uriInfo) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(
                            "Sensor with ID '" + sensorId + "' was not found.",
                            404, "Not Found"))
                    .build();
        }

        // Part 5.3 - block readings for sensors under maintenance
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is currently under MAINTENANCE. "
                + "It is physically disconnected and cannot accept new readings."
            );
        }

        // Auto-generate ID if not provided in the request body
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }
        // Auto-set timestamp if not provided
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Store the reading
        DataStore.readings
                .computeIfAbsent(sensorId, k -> new ArrayList<>())
                .add(reading);

        // Part 4.2 Side effect: update the parent sensor's currentValue
        sensor.setCurrentValue(reading.getValue());

        URI location = uriInfo.getAbsolutePathBuilder().path(reading.getId()).build();
        return Response.created(location).entity(reading).build();
    }
}
