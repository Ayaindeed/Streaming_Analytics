package com.streaming.model;

/**
 * Résultat du traitement d'un événement unique
 */
public class EventProcessingResult {

    private String eventId;
    private boolean success;
    private String message;
    private long processingTimeMs;
    private String videoId;
    private String userId;
    private VideoStats updatedStats;

    // Constructors
    public EventProcessingResult() {}

    public EventProcessingResult(String eventId, boolean success, String message) {
        this.eventId = eventId;
        this.success = success;
        this.message = message;
    }

    public EventProcessingResult(String eventId, boolean success, String message, 
                                 long processingTimeMs, String videoId, String userId) {
        this.eventId = eventId;
        this.success = success;
        this.message = message;
        this.processingTimeMs = processingTimeMs;
        this.videoId = videoId;
        this.userId = userId;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public VideoStats getUpdatedStats() { return updatedStats; }
    public void setUpdatedStats(VideoStats updatedStats) { this.updatedStats = updatedStats; }
}
