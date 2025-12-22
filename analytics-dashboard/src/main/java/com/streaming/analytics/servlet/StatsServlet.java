package com.streaming.analytics.servlet;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "statsServlet", value = "/stats")
public class StatsServlet extends HttpServlet {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> eventsCollection;
    private MongoCollection<Document> videosCollection;

    public void init() {
        try {
            this.mongoClient = MongoClients.create("mongodb://admin:admin123@mongodb:27017");
            this.database = mongoClient.getDatabase("streaming_analytics");
            this.eventsCollection = database.getCollection("viewevents");
            this.videosCollection = database.getCollection("videos");
        } catch (Exception e) {
            System.err.println("Erreur de connexion MongoDB: " + e.getMessage());
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // Statistiques de base
        long totalEvents = 0;
        long totalVideos = 0;
        long totalUsers = 0;

        try {
            totalEvents = eventsCollection.countDocuments();
            totalVideos = videosCollection.countDocuments();

            // Compter les utilisateurs uniques
            List<String> distinctUsers = eventsCollection.distinct("userId", String.class).into(new ArrayList<>());
            totalUsers = distinctUsers.size();

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des statistiques: " + e.getMessage());
        }

        out.println("<!DOCTYPE html>");
        out.println("<html lang='fr'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Statistiques - Streaming Analytics</title>");
        out.println("    <style>");
        out.println("        * {");
        out.println("            margin: 0;");
        out.println("            padding: 0;");
        out.println("            box-sizing: border-box;");
        out.println("        }");
        out.println("        ");
        out.println("        body {");
        out.println("            font-family: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;");
        out.println("            background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #312e81 100%);");
        out.println("            color: #e2e8f0;");
        out.println("            margin: 0;");
        out.println("            padding: 20px;");
        out.println("            min-height: 100vh;");
        out.println("            position: relative;");
        out.println("        }");
        out.println("        ");
        out.println("        body::before {");
        out.println("            content: '';");
        out.println("            position: absolute;");
        out.println("            top: 0;");
        out.println("            left: 0;");
        out.println("            right: 0;");
        out.println("            bottom: 0;");
        out.println("            background: radial-gradient(circle at 30% 70%, rgba(14, 165, 233, 0.1) 0%, transparent 50%),");
        out.println("                        radial-gradient(circle at 70% 30%, rgba(168, 85, 247, 0.1) 0%, transparent 50%);");
        out.println("            pointer-events: none;");
        out.println("        }");
        out.println("        ");
        out.println("        .retro-container {");
        out.println("            max-width: 1200px;");
        out.println("            margin: 0 auto;");
        out.println("            background: rgba(30, 27, 75, 0.85);");
        out.println("            border: 1px solid rgba(168, 85, 247, 0.2);");
        out.println("            border-radius: 24px;");
        out.println("            padding: 50px;");
        out.println("            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3), 0 0 80px rgba(168, 85, 247, 0.1), inset 0 1px 0 rgba(255, 255, 255, 0.1);");
        out.println("            backdrop-filter: blur(30px);");
        out.println("            position: relative;");
        out.println("            z-index: 1;");
        out.println("        }");
        out.println("        ");
        out.println("        h1 {");
        out.println("            text-align: center;");
        out.println("            font-size: 3em;");
        out.println("            font-weight: 800;");
        out.println("            margin-bottom: 40px;");
        out.println("            background: linear-gradient(135deg, #a855f7 0%, #ec4899 50%, #06b6d4 100%);");
        out.println("            -webkit-background-clip: text;");
        out.println("            -webkit-text-fill-color: transparent;");
        out.println("            background-clip: text;");
        out.println("            letter-spacing: -1px;");
        out.println("        }");
        out.println("        ");
        out.println("        h3 {");
        out.println("            color: #e9d5ff;");
        out.println("            font-weight: 700;");
        out.println("        }");
        out.println("        ");
        out.println("        .stats-overview {");
        out.println("            display: grid;");
        out.println("            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));");
        out.println("            gap: 20px;");
        out.println("            margin-bottom: 40px;");
        out.println("        }");
        out.println("        ");
        out.println("        .stat-card {");
        out.println("            background: linear-gradient(135deg, rgba(88, 28, 135, 0.3) 0%, rgba(49, 46, 129, 0.3) 100%);");
        out.println("            border: 1px solid rgba(168, 85, 247, 0.2);");
        out.println("            padding: 35px;");
        out.println("            border-radius: 16px;");
        out.println("            text-align: center;");
        out.println("            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);");
        out.println("            backdrop-filter: blur(10px);");
        out.println("            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);");
        out.println("        }");
        out.println("        ");
        out.println("        .stat-card:hover {");
        out.println("            transform: translateY(-8px) scale(1.03);");
        out.println("            box-shadow: 0 20px 40px rgba(168, 85, 247, 0.3);");
        out.println("            border-color: rgba(168, 85, 247, 0.5);");
        out.println("            background: linear-gradient(135deg, rgba(88, 28, 135, 0.5) 0%, rgba(49, 46, 129, 0.5) 100%);");
        out.println("        }");
        out.println("        ");
        out.println("        .stat-number {");
        out.println("            font-size: 3.5em;");
        out.println("            font-weight: 800;");
        out.println("            background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);");
        out.println("            -webkit-background-clip: text;");
        out.println("            -webkit-text-fill-color: transparent;");
        out.println("            background-clip: text;");
        out.println("            margin-bottom: 10px;");
        out.println("        }");
        out.println("        ");
        out.println("        .stat-label {");
        out.println("            color: #c4b5fd;");
        out.println("            font-size: 1.1em;");
        out.println("            font-weight: 600;");
        out.println("            text-transform: uppercase;");
        out.println("            letter-spacing: 1px;");
        out.println("        }");
        out.println("        ");
        out.println("        .retro-button {");
        out.println("            display: inline-block;");
        out.println("            background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);");
        out.println("            color: white;");
        out.println("            padding: 15px 35px;");
        out.println("            text-decoration: none;");
        out.println("            border: none;");
        out.println("            border-radius: 12px;");
        out.println("            font-size: 1em;");
        out.println("            font-weight: 700;");
        out.println("            text-transform: uppercase;");
        out.println("            letter-spacing: 1.5px;");
        out.println("            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);");
        out.println("            box-shadow: 0 10px 30px rgba(168, 85, 247, 0.4);");
        out.println("            margin: 10px;");
        out.println("        }");
        out.println("        ");
        out.println("        .retro-button:hover {");
        out.println("            background: linear-gradient(135deg, #ec4899 0%, #a855f7 100%);");
        out.println("            box-shadow: 0 15px 40px rgba(168, 85, 247, 0.5);");
        out.println("            transform: translateY(-3px);");
        out.println("        }");
        out.println("        ");
        out.println("        .navigation {");
        out.println("            text-align: center;");
        out.println("            margin-top: 30px;");
        out.println("        }");
        out.println("        ");
        out.println("        .status-indicator {");
        out.println("            display: inline-block;");
        out.println("            width: 12px;");
        out.println("            height: 12px;");
        out.println("            background: #10b981;");
        out.println("            border-radius: 50%;");
        out.println("            box-shadow: 0 0 10px #10b981;");
        out.println("            margin-right: 8px;");
        out.println("            animation: pulse 2s infinite;");
        out.println("        }");
        out.println("        ");
        out.println("        @keyframes pulse {");
        out.println("            0% { box-shadow: 0 0 10px #10b981; }");
        out.println("            50% { box-shadow: 0 0 20px #10b981, 0 0 30px #10b981; }");
        out.println("            100% { box-shadow: 0 0 10px #10b981; }");
        out.println("        }");
        out.println("        ");
        out.println("        .info-box {");
        out.println("            background: rgba(168, 85, 247, 0.15);");
        out.println("            border: 1px solid rgba(168, 85, 247, 0.3);");
        out.println("            padding: 25px;");
        out.println("            margin: 30px 0;");
        out.println("            border-radius: 16px;");
        out.println("            backdrop-filter: blur(10px);");
        out.println("        }");
        out.println("        ");
        out.println("        .info-box p {");
        out.println("            color: #c4b5fd;");
        out.println("            margin: 8px 0;");
        out.println("        }");
        out.println("        ");
        out.println("        .retro-footer {");
        out.println("            text-align: center;");
        out.println("            margin-top: 50px;");
        out.println("            padding-top: 30px;");
        out.println("            border-top: 1px solid rgba(168, 85, 247, 0.2);");
        out.println("            color: #a5b4fc;");
        out.println("            font-size: 0.9em;");
        out.println("        }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='retro-container'>");
        out.println("        <h1><span class='status-indicator'></span>📊 Statistiques Temps Réel 📊</h1>");
        out.println("        ");
        out.println("        <div class='stats-overview'>");
        out.println("            <div class='stat-card'>");
        out.println("                <div class='stat-number'>" + String.format("%,d", totalEvents) + "</div>");
        out.println("                <div class='stat-label'>Événements de Visionnage</div>");
        out.println("            </div>");
        out.println("            <div class='stat-card'>");
        out.println("                <div class='stat-number'>" + String.format("%,d", totalVideos) + "</div>");
        out.println("                <div class='stat-label'>Vidéos Cataloguées</div>");
        out.println("            </div>");
        out.println("            <div class='stat-card'>");
        out.println("                <div class='stat-number'>" + String.format("%,d", totalUsers) + "</div>");
        out.println("                <div class='stat-label'>Utilisateurs Actifs</div>");
        out.println("            </div>");
        out.println("        </div>");
        out.println("        ");
        out.println("        <div class='info-box'>");
        out.println("            <h3>💡 Informations Système</h3>");
        out.println("            <p>Base de données : MongoDB connectée</p>");
        out.println("            <p>Technologie : Jakarta EE + JAX-RS</p>");
        out.println("            <p>Infrastructure : Docker Compose</p>");
        out.println("        </div>");
        out.println("        ");
        out.println("        <div class='navigation'>");
        out.println("            <a href='dashboard' class='retro-button'>🏠 Retour au Dashboard</a>");
        out.println("            <a href='/analytics-api/api/v1/analytics/health' class='retro-button' target='_blank'>🔍 API Health Check</a>");
        out.println("        </div>");
        out.println("        ");
        out.println("        <div class='retro-footer'>");
        out.println("            <p>Données mises à jour en temps réel • Système d'Analyse Streaming - 2025</p>");
        out.println("            <p style='margin-top: 10px; opacity: 0.7;'>Propulsé par Jakarta EE & MongoDB</p>");
        out.println("        </div>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");
    }

    public void destroy() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}