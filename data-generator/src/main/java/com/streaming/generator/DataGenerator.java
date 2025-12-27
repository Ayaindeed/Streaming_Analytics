package com.streaming.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Data generator for streaming events
 */
public class DataGenerator {

    private static final String[] ACTIONS = {"WATCH", "PAUSE", "LIKE", "SHARE", "COMMENT"};
    private static final String[] QUALITIES = {"240p", "360p", "480p", "720p", "1080p", "4K"};
    private static final String[] DEVICE_TYPES = {"mobile", "tablet", "desktop", "smart_tv", "console"};
    private static final String[] VIDEO_CATEGORIES = {"sports", "music", "news", "gaming", "comedy", "education", "technology", "travel"};
    
    private final int numUsers;
    private final int numVideos;
    private final int numEvents;
    private final Random random = new Random();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final List<String> userIds = new ArrayList<>();
    private final List<String> videoIds = new ArrayList<>();
    private final List<ObjectNode> videos = new ArrayList<>();
    
    public DataGenerator(int numUsers, int numVideos, int numEvents) {
        this.numUsers = numUsers;
        this.numVideos = numVideos;
        this.numEvents = numEvents;
        
        // Generate user IDs
        for (int i = 0; i < numUsers; i++) {
            userIds.add("user_" + UUID.randomUUID().toString().substring(0, 8));
        }
        
        // Generate video metadata
        for (int i = 0; i < numVideos; i++) {
            String videoId = "video_" + UUID.randomUUID().toString().substring(0, 8);
            videoIds.add(videoId);
            
            ObjectNode video = mapper.createObjectNode()
                    .put("videoId", videoId)
                    .put("title", "Video " + i)
                    .put("category", VIDEO_CATEGORIES[random.nextInt(VIDEO_CATEGORIES.length)])
                    .put("duration", 30 + random.nextInt(570)) // 30 seconds to 10 minutes
                    .put("uploadDate", Instant.now().minus(random.nextInt(365), ChronoUnit.DAYS).toString())
                    .put("creator", "creator_" + random.nextInt(100));
            
            videos.add(video);
        }
    }
    
    /**
     * Generates a random streaming event
     * @return Event as a JSON ObjectNode
     */
    public ObjectNode generateRandomEvent() {
        String userId = userIds.get(random.nextInt(userIds.size()));
        String videoId = videoIds.get(random.nextInt(videoIds.size()));
        String action = ACTIONS[getWeightedRandomIndex(new int[]{70, 10, 10, 5, 5})]; // 70% watch events
        
        int duration = 0;
        if (action.equals("WATCH")) {
            // Most users watch 30-70% of a video
            int videoDuration = findVideoDuration(videoId);
            duration = (int)(videoDuration * (0.3 + random.nextDouble() * 0.4));
        }
        
        // Generate slightly older timestamps for some events (up to 7 days ago)
        long randomOffset = random.nextInt(7 * 24 * 60 * 60);
        Instant timestamp = Instant.now().minusSeconds(randomOffset);
        
        return mapper.createObjectNode()
                .put("eventId", "evt_" + UUID.randomUUID().toString().substring(0, 8))
                .put("userId", userId)
                .put("videoId", videoId)
                .put("timestamp", timestamp.toString())
                .put("action", action)
                .put("duration", duration)
                .put("quality", QUALITIES[random.nextInt(QUALITIES.length)])
                .put("deviceType", DEVICE_TYPES[random.nextInt(DEVICE_TYPES.length)]);
    }
    
    /**
     * Finds the duration of a video by ID
     * @param videoId Video ID to find
     * @return Duration in seconds
     */
    private int findVideoDuration(String videoId) {
        for (ObjectNode video : videos) {
            if (video.get("videoId").asText().equals(videoId)) {
                return video.get("duration").asInt();
            }
        }
        return 300; // Default 5 minutes
    }
    
    /**
     * Gets a random index based on weights
     * @param weights Array of weights
     * @return Selected index
     */
    private int getWeightedRandomIndex(int[] weights) {
        int totalWeight = Arrays.stream(weights).sum();
        int randomValue = random.nextInt(totalWeight);
        
        int weightSum = 0;
        for (int i = 0; i < weights.length; i++) {
            weightSum += weights[i];
            if (randomValue < weightSum) {
                return i;
            }
        }
        
        return 0;
    }
    
    /**
     * Generates events and saves them to JSON files
     * @param eventsPerFile Number of events per file
     * @param outputDir Output directory
     * @return Number of generated files
     */
    public int generateEventsToFiles(int eventsPerFile, String outputDir) throws Exception {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Save video metadata
        try (FileWriter writer = new FileWriter(new File(dir, "videos.json"))) {
            writer.write(mapper.writeValueAsString(videos));
        }
        
        // Generate events in batches
        int fileCount = 0;
        int totalEvents = 0;
        List<ObjectNode> eventBatch = new ArrayList<>();
        
        while (totalEvents < numEvents) {
            eventBatch.add(generateRandomEvent());
            totalEvents++;
            
            if (eventBatch.size() >= eventsPerFile || totalEvents >= numEvents) {
                String filename = String.format("events_%d.json", fileCount);
                try (FileWriter writer = new FileWriter(new File(dir, filename))) {
                    writer.write(mapper.writeValueAsString(eventBatch));
                }
                
                System.out.printf("Generated %s with %d events (total: %d/%d)%n", 
                        filename, eventBatch.size(), totalEvents, numEvents);
                
                eventBatch.clear();
                fileCount++;
            }
        }
        
        return fileCount;
    }
    
    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Parse parameters
            int numUsers = getArgValue(args, "--users", 1000);
            int numVideos = getArgValue(args, "--videos", 100);
            int numEvents = getArgValue(args, "--events", 100000);
            int eventsPerFile = getArgValue(args, "--batch-size", 10000);
            String outputDir = getArgValue(args, "--output", "./data");
            
            System.out.println("Streaming Data Generator");
            System.out.println("------------------------");
            System.out.printf("Users: %d, Videos: %d, Events: %d%n", numUsers, numVideos, numEvents);
            System.out.printf("Output directory: %s%n", outputDir);
            
            // Generate data
            DataGenerator generator = new DataGenerator(numUsers, numVideos, numEvents);
            long startTime = System.currentTimeMillis();
            int fileCount = generator.generateEventsToFiles(eventsPerFile, outputDir);
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.println("\nGeneration completed!");
            System.out.printf("Generated %d events in %d files%n", numEvents, fileCount);
            System.out.printf("Time taken: %.2f seconds%n", duration / 1000.0);
            
        } catch (Exception e) {
            System.err.println("Error generating data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets an argument value from command line
     * @param args Command line arguments
     * @param name Argument name
     * @param defaultValue Default value if not specified
     * @return Argument value
     */
    private static int getArgValue(String[] args, String name, int defaultValue) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(name)) {
                try {
                    return Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    // Ignore and use default
                }
            }
        }
        return defaultValue;
    }
    
    /**
     * Gets a string argument value from command line
     * @param args Command line arguments
     * @param name Argument name
     * @param defaultValue Default value if not specified
     * @return Argument value
     */
    private static String getArgValue(String[] args, String name, String defaultValue) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(name)) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }
}
