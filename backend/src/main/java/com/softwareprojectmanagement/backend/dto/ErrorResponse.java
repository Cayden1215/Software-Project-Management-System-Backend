package com.softwareprojectmanagement.backend.dto;

import java.time.LocalDateTime;

/**
 * Error response DTO that holds error information to be returned to the client.
 * This class is used to standardize error responses across the application.
 */
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;

    /**
     * Constructs an ErrorResponse with message, status, and timestamp.
     *
     * @param message the error message
     * @param status the HTTP status code
     */
    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructs an ErrorResponse with message, status, timestamp, and path.
     *
     * @param message the error message
     * @param status the HTTP status code
     * @param path the request path where the error occurred
     */
    public ErrorResponse(String message, int status, String path) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
