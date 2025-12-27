package com.streaming.model;

/**
 * Réponse de succès standard pour les appels API
 */
public class SuccessResponse {

    private String status;
    private String message;
    private Object data;
    private long timestamp;

    // Constructors
    public SuccessResponse() {}

    public SuccessResponse(String message, Object data) {
        this.status = "SUCCESS";
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public SuccessResponse(String message) {
        this.status = "SUCCESS";
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
