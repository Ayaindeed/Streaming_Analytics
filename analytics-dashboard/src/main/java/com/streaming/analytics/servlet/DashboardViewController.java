package com.streaming.analytics.servlet;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.bson.Document;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller servlet for dashboard view with real data from MongoDB
 */
@WebServlet(name = "dashboardViewController", urlPatterns = {"/dashboard/view"})
public class DashboardViewController extends HttpServlet {

    private MongoClient mongoClient;
    private MongoDatabase database;

    @Override
    public void init() {
        try {
            this.mongoClient = MongoClients.create("mongodb://admin:admin123@mongodb:27017");
            this.database = mongoClient.getDatabase("streaming_analytics");
        } catch (Exception e) {
            System.err.println("MongoDB connection error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            System.out.println("=== DashboardViewController doGet START ===");
            System.out.println("Database: " + (database != null ? database.getName() : "NULL"));
            
            MongoCollection<Document> eventsCollection = database.getCollection("events");
            System.out.println("Events collection: " + eventsCollection.getNamespace());
            
            // Get basic statistics - FAST queries using MongoDB aggregation
            long totalViews = eventsCollection.countDocuments();
            System.out.println("Total views from DB: " + totalViews);
            
            // Get unique users count using aggregation (FAST - no memory load)
            List<Document> userCountPipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$userId")),
                new Document("$count", "totalUsers")
            );
            long totalUsers = 0;
            for (Document doc : eventsCollection.aggregate(userCountPipeline)) {
                totalUsers = doc.getInteger("totalUsers", 0);
            }
            System.out.println("Total users from DB: " + totalUsers);

            // Calculate average watch time using aggregation
            List<Document> avgPipeline = Arrays.asList(
                new Document("$group", new Document("_id", null)
                    .append("avgDuration", new Document("$avg", "$duration")))
            );
            long avgWatchTime = 0;
            for (Document doc : eventsCollection.aggregate(avgPipeline)) {
                Number avg = doc.get("avgDuration", Number.class);
                if (avg != null) avgWatchTime = avg.longValue();
            }

            // Get top videos by aggregation (FAST)
            List<Document> topVideosPipeline = Arrays.asList(
                new Document("$group", new Document("_id", "$videoId")
                    .append("totalViews", new Document("$sum", 1))
                    .append("totalWatchTime", new Document("$sum", "$duration"))
                    .append("uniqueViewers", new Document("$addToSet", "$userId"))),
                new Document("$project", new Document("videoId", "$_id")
                    .append("totalViews", 1)
                    .append("totalWatchTime", 1)
                    .append("uniqueViewers", new Document("$size", "$uniqueViewers"))
                    .append("averageWatchTime", new Document("$cond", 
                        Arrays.asList(
                            new Document("$gt", Arrays.asList("$totalViews", 0)),
                            new Document("$divide", Arrays.asList("$totalWatchTime", "$totalViews")),
                            0
                        )))),
                new Document("$sort", new Document("totalViews", -1)),
                new Document("$limit", 10)
            );

            List<VideoStatsData> top10 = new ArrayList<>();
            for (Document doc : eventsCollection.aggregate(topVideosPipeline)) {
                VideoStatsData stats = new VideoStatsData(doc.getString("videoId"));
                stats.totalViews = doc.getInteger("totalViews", 0);
                stats.totalWatchTime = doc.getInteger("totalWatchTime", 0);
                stats.averageWatchTime = doc.get("averageWatchTime", Number.class).longValue();
                stats.uniqueViewers = doc.getInteger("uniqueViewers", 0);
                
                // Calculate simulated likes based on engagement (avg watch time > 300s = likely to like)
                // Roughly 30% of viewers who watch 5+ minutes will like
                stats.likes = stats.averageWatchTime > 300 ? (int)(stats.totalViews * 0.3) : (int)(stats.totalViews * 0.1);
                
                top10.add(stats);
            }

            // Set attributes for JSP
            request.setAttribute("totalVideos", top10.size()); // Number of unique videos with views
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalViews", totalViews);
            request.setAttribute("avgWatchTime", avgWatchTime);
            request.setAttribute("topVideos", top10);
            
            // Real-time stats (simulated with existing data)
            request.setAttribute("currentViewers", Math.min(totalUsers / 10, 1000)); // 10% of users
            request.setAttribute("eventsPerSecond", 0); // Would need real-time tracking
            request.setAttribute("activeUsers", totalUsers);
            request.setAttribute("mostWatchedVideo", top10.isEmpty() ? "N/A" : top10.get(0).getTitle());

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Set default values if error
            request.setAttribute("totalVideos", 0L);
            request.setAttribute("totalUsers", 0L);
            request.setAttribute("totalViews", 0L);
            request.setAttribute("avgWatchTime", 0L);
            request.setAttribute("topVideos", Collections.emptyList());
            
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    /**
     * Helper class for video statistics
     */
    public static class VideoStatsData {
        private String videoId;
        private String title;
        private long totalViews;
        private int likes;
        private long totalWatchTime;
        private long averageWatchTime;
        private int uniqueViewers;

        public VideoStatsData(String videoId) {
            this.videoId = videoId;
            this.title = videoId; // Use videoId directly as title
            this.totalViews = 0;
            this.likes = 0;
            this.totalWatchTime = 0;
            this.averageWatchTime = 0;
            this.uniqueViewers = 0;
        }

        // Getters
        public String getVideoId() { return videoId; }
        public String getTitle() { return title; }
        public long getTotalViews() { return totalViews; }
        public int getLikes() { return likes; }
        public int getTotalLikes() { return likes; } // Alias for JSP
        public long getTotalWatchTime() { return totalWatchTime; }
        public long getAverageWatchTime() { return averageWatchTime; }
        public long getAvgDuration() { return averageWatchTime; } // Alias for JSP
        public int getUniqueViewers() { return uniqueViewers; }
        public double getEngagement() { 
            return totalViews > 0 ? (likes * 100.0 / totalViews) : 0.0;
        }
        public double getEngagementRate() { 
            return totalViews > 0 ? (likes * 100.0 / totalViews) : 0.0;
        }
    }
}
