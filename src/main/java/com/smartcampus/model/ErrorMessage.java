/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;


public class ErrorMessage {

    private String message;   // human-readable description of the error
    private int status;       // HTTP status code (e.g. 404, 409, 422)
    private String error;     // short error category name

    public ErrorMessage() {}

    public ErrorMessage(String message, int status, String error) {
        this.message = message;
        this.status = status;
        this.error = error;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
