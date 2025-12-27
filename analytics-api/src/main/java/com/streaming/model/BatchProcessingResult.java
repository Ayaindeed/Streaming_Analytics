package com.streaming.model;

import java.util.List;

/**
 * Résultat du traitement d'un lot d'événements
 */
public class BatchProcessingResult {

    private int totalEventsProcessed;
    private int successfulEvents;
    private int failedEvents;
    private long totalProcessingTimeMs;
    private double averageProcessingTimeMs;
    private List<String> failedEventIds;
    private String message;
    private String timestamp;

    // Constructors
    public BatchProcessingResult() {}

    public BatchProcessingResult(int totalEventsProcessed, int successfulEvents, 
                                int failedEvents, long totalProcessingTimeMs) {
        this.totalEventsProcessed = totalEventsProcessed;
        this.successfulEvents = successfulEvents;
        this.failedEvents = failedEvents;
        this.totalProcessingTimeMs = totalProcessingTimeMs;
        this.averageProcessingTimeMs = (double) totalProcessingTimeMs / totalEventsProcessed;
    }

    // Getters and Setters
    public int getTotalEventsProcessed() { return totalEventsProcessed; }
    public void setTotalEventsProcessed(int totalEventsProcessed) { this.totalEventsProcessed = totalEventsProcessed; }

    public int getSuccessfulEvents() { return successfulEvents; }
    public void setSuccessfulEvents(int successfulEvents) { this.successfulEvents = successfulEvents; }

    public int getFailedEvents() { return failedEvents; }
    public void setFailedEvents(int failedEvents) { this.failedEvents = failedEvents; }

    public long getTotalProcessingTimeMs() { return totalProcessingTimeMs; }
    public void setTotalProcessingTimeMs(long totalProcessingTimeMs) { this.totalProcessingTimeMs = totalProcessingTimeMs; }

    public double getAverageProcessingTimeMs() { return averageProcessingTimeMs; }
    public void setAverageProcessingTimeMs(double averageProcessingTimeMs) { this.averageProcessingTimeMs = averageProcessingTimeMs; }

    public List<String> getFailedEventIds() { return failedEventIds; }
    public void setFailedEventIds(List<String> failedEventIds) { this.failedEventIds = failedEventIds; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
