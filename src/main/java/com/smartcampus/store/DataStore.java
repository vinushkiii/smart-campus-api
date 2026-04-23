/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DataStore {

    // All rooms keyed by room ID
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    // All sensors keyed by sensor ID
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    // Sensor readings keyed by sensor ID → list of readings for that sensor
    public static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

}
