/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.ErrorMessage;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Part 2 - Room Management
 * Handles all operations at /api/v1/rooms
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    /**
     * Part 2.1 - GET /api/v1/rooms
     * Returns all rooms in the system.
     */
    @GET
    public Response getAllRooms() {
        List<Room> roomList = new ArrayList<>(DataStore.rooms.values());
        return Response.ok(roomList).build();
    }

    /**
     * Part 2.1 - POST /api/v1/rooms
     * Creates a new room. Returns 201 Created with a Location header.
     */
    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        // Validate that the incoming JSON has an ID
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Room ID is required.", 400, "Bad Request"))
                    .build();
        }
        // Check for duplicate IDs
        if (DataStore.rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorMessage(
                            "A room with ID '" + room.getId() + "' already exists.",
                            409, "Conflict"))
                    .build();
        }
        DataStore.rooms.put(room.getId(), room);

        // Build a URI pointing to the new resource: /api/v1/rooms/{id}
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();

        // 201 Created with Location header and the new room in the body
        return Response.created(location).entity(room).build();
    }

    /**
     * Part 2.1 - GET /api/v1/rooms/{roomId}
     * Returns a single room by its ID.
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(
                            "Room with ID '" + roomId + "' was not found.",
                            404, "Not Found"))
                    .build();
        }
        return Response.ok(room).build();
    }

    /**
     * Part 2.2 - DELETE /api/v1/rooms/{roomId}
     * Deletes a room. Business rule: cannot delete if sensors are still assigned.
     * Throws RoomNotEmptyException → caught by RoomNotEmptyExceptionMapper → 409 Conflict.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            // Idempotent: deleting a non-existent room returns 404
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(
                            "Room with ID '" + roomId + "' was not found.",
                            404, "Not Found"))
                    .build();
        }
        // Business rule: block deletion if sensors are still assigned
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room '" + roomId + "' cannot be deleted. It still has "
                + room.getSensorIds().size() + " active sensor(s) assigned to it. "
                + "Please remove or reassign all sensors first."
            );
        }
        DataStore.rooms.remove(roomId);
        return Response.noContent().build(); // 204 No Content - success, nothing to return
    }
}
