package com.streaming.model;

/**
 * Entité représentant un événement de visualisation
 */
public class ViewEvent {

    private String eventId;
    private String userId;
    private String videoId;
    private String timestamp;
    private String action;
    private int duration;
    private String quality;
    private String deviceType;

    // Constructors
    public ViewEvent() {}

    public ViewEvent(String eventId, String userId, String videoId, String timestamp,
                    String action, int duration, String quality, String deviceType) {
        this.eventId = eventId;
        this.userId = userId;
        this.videoId = videoId;
        this.timestamp = timestamp;
        this.action = action;
        this.duration = duration;
        this.quality = quality;
        this.deviceType = deviceType;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    @Override
    public String toString() {
        return "ViewEvent{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", action='" + action + '\'' +
                ", duration=" + duration +
                ", quality='" + quality + '\'' +
                ", deviceType='" + deviceType + '\'' +
                '}';
    }
}
