package com.streaming.model;

/**
 * Réponse d'erreur standard pour les appels API
 */
public class ErrorResponse {

    private String error;
    private String message;
    private long timestamp;
    private int code;

    // Constructors
    public ErrorResponse() {}

    public ErrorResponse(String message) {
        this.error = "ERROR";
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.code = 500;
    }

    public ErrorResponse(String error, String message, int code) {
        this.error = error;
        this.message = message;
        this.code = code;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
}
