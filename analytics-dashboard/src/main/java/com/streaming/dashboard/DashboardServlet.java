package com.streaming.dashboard;

import com.streaming.model.VideoStats;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet controller for the analytics dashboard
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    private static final String API_BASE_URL = "http://localhost:8080/analytics-api/api/v1/analytics";
    
    private Client client;
    
    @Override
    public void init() throws ServletException {
        client = ClientBuilder.newClient();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Fetch top videos
            List<VideoStats> topVideos = fetchTopVideos(10);
            request.setAttribute("topVideos", topVideos);
            
            // Fetch global stats
            Map<String, Object> globalStats = fetchGlobalStats();
            request.setAttribute("totalVideos", globalStats.getOrDefault("totalVideos", 0));
            request.setAttribute("totalViews", globalStats.getOrDefault("totalViews", 0));
            request.setAttribute("totalUsers", 50000); // Placeholder value
            
            // Fetch category stats
            Map<String, Object> categoryStats = fetchCategoryStats();
            request.setAttribute("categoryStats", categoryStats);
            
            // Forward to JSP view
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard data", e);
            request.setAttribute("error", "Failed to load dashboard data: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Fetches top videos from the API
     * @param limit Maximum number of videos to fetch
     * @return List of top videos
     */
    private List<VideoStats> fetchTopVideos(int limit) {
        return client.target(API_BASE_URL)
                .path("videos/top")
                .queryParam("limit", limit)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<VideoStats>>() {});
    }
    
    /**
     * Fetches global platform statistics
     * @return Map of statistics
     */
    private Map<String, Object> fetchGlobalStats() {
        return client.target(API_BASE_URL)
                .path("health") // Using health endpoint which includes basic stats
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Map<String, Object>>() {});
    }
    
    /**
     * Fetches statistics by category
     * @return Map of category statistics
     */
    private Map<String, Object> fetchCategoryStats() {
        return client.target(API_BASE_URL)
                .path("categories/stats")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<Map<String, Object>>() {});
    }
    
    @Override
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }
}
