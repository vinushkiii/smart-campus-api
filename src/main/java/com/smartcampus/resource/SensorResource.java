/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.ErrorMessage;
import com.smartcampus.model.Sensor;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part 3 - Sensor Operations
 * Handles all operations at /api/v1/sensors
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    /**
     * Part 3.1 - POST /api/v1/sensors
     * Registers a new sensor.
     * Validates that the roomId in the request body actually exists.
     * If not → throws LinkedResourceNotFoundException → 422 Unprocessable Entity.
     */
    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Sensor ID is required.", 400, "Bad Request"))
                    .build();
        }
        if (DataStore.sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorMessage(
                            "A sensor with ID '" + sensor.getId() + "' already exists.",
                            409, "Conflict"))
                    .build();
        }
        // Part 3.1 - validate the roomId foreign key exists
        if (sensor.getRoomId() == null || !DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Cannot register sensor: the specified roomId '"
                + sensor.getRoomId() + "' does not exist in the system."
            );
        }
        // Save sensor
        DataStore.sensors.put(sensor.getId(), sensor);

        // Link this sensor to its room (add sensor ID to the room's sensorIds list)
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        // Initialise an empty readings history for this sensor
        DataStore.readings.put(sensor.getId(), new ArrayList<>());

        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }

    /**
     * Part 3.2 - GET /api/v1/sensors
     * Returns all sensors. Supports optional ?type=CO2 query parameter for filtering.
     * Example: GET /api/v1/sensors?type=Temperature
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(DataStore.sensors.values());

        // If the 'type' query parameter was provided, filter the list
        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                    .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        return Response.ok(sensorList).build();
    }

    /**
     * GET /api/v1/sensors/{sensorId}
     * Returns a single sensor by ID.
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(
                            "Sensor with ID '" + sensorId + "' was not found.",
                            404, "Not Found"))
                    .build();
        }
        return Response.ok(sensor).build();
    }

    /**
     * Part 4.1 - Sub-Resource Locator
     * This method has NO HTTP verb annotation (@GET, @POST etc).
     * When JAX-RS sees /sensors/{sensorId}/readings, it calls this method
     * which returns a SensorReadingResource instance. JAX-RS then forwards
     * the request to that object to handle the actual GET or POST.
     *
     * This is the Sub-Resource Locator pattern from the spec and Week 5 slides.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
