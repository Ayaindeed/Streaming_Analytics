package com.streaming.analytics.api;

import com.streaming.analytics.model.ViewEvent;
import com.streaming.analytics.service.EventProcessorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * API REST pour l'ingestion et l'analyse des événements de visualisation
 * 
 * EXEMPLE DE CODE FONCTIONNEL - À ADAPTER ET COMPLÉTER
 * 
 * Ce fichier montre la structure de base de l'API REST.
 * Les étudiants doivent :
 * 1. Implémenter les méthodes manquantes
 * 2. Ajouter la gestion d'erreurs
 * 3. Optimiser les performances
 * 4. Ajouter la validation des données
 */
@Path("/api/v1/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalyticsResource {

    // Temporairement commenté pour tester JAX-RS
    // @Inject
    // private EventProcessorService eventProcessor;

    /**
     * Test endpoint simple
     * GET /api/v1/analytics/test
     */
    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("API is working!").build();
    }

    /**
     * Health check de l'API
     * GET /api/v1/analytics/health
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_HTML)
    public Response healthCheck() {
        String html = "<!DOCTYPE html>" +
            "<html lang='fr'>" +
            "<head>" +
            "    <meta charset='UTF-8'>" +
            "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "    <title>API Health Check</title>" +
            "    <style>" +
            "        * { margin: 0; padding: 0; box-sizing: border-box; }" +
            "        body {" +
            "            font-family: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;" +
            "            background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #312e81 100%);" +
            "            color: #e2e8f0;" +
            "            min-height: 100vh;" +
            "            display: flex;" +
            "            align-items: center;" +
            "            justify-content: center;" +
            "            padding: 20px;" +
            "            position: relative;" +
            "        }" +
            "        body::before {" +
            "            content: '';" +
            "            position: absolute;" +
            "            top: 0; left: 0; right: 0; bottom: 0;" +
            "            background: radial-gradient(circle at 30% 70%, rgba(14, 165, 233, 0.1) 0%, transparent 50%)," +
            "                        radial-gradient(circle at 70% 30%, rgba(168, 85, 247, 0.1) 0%, transparent 50%);" +
            "            pointer-events: none;" +
            "        }" +
            "        .health-container {" +
            "            max-width: 600px;" +
            "            width: 100%;" +
            "            background: rgba(30, 27, 75, 0.85);" +
            "            border: 1px solid rgba(168, 85, 247, 0.2);" +
            "            border-radius: 24px;" +
            "            padding: 50px;" +
            "            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3), 0 0 80px rgba(168, 85, 247, 0.1)," +
            "                        inset 0 1px 0 rgba(255, 255, 255, 0.1);" +
            "            backdrop-filter: blur(30px);" +
            "            text-align: center;" +
            "            position: relative;" +
            "            z-index: 1;" +
            "        }" +
            "        h1 {" +
            "            font-size: 2.5em;" +
            "            font-weight: 800;" +
            "            margin-bottom: 30px;" +
            "            background: linear-gradient(135deg, #10b981 0%, #34d399 100%);" +
            "            -webkit-background-clip: text;" +
            "            -webkit-text-fill-color: transparent;" +
            "            background-clip: text;" +
            "        }" +
            "        .status-indicator {" +
            "            width: 80px;" +
            "            height: 80px;" +
            "            background: linear-gradient(135deg, #10b981 0%, #34d399 100%);" +
            "            border-radius: 50%;" +
            "            margin: 0 auto 30px;" +
            "            display: flex;" +
            "            align-items: center;" +
            "            justify-content: center;" +
            "            font-size: 3em;" +
            "            box-shadow: 0 0 30px rgba(16, 185, 129, 0.5);" +
            "            animation: pulse 2s infinite;" +
            "        }" +
            "        @keyframes pulse {" +
            "            0%, 100% { box-shadow: 0 0 30px rgba(16, 185, 129, 0.5); transform: scale(1); }" +
            "            50% { box-shadow: 0 0 50px rgba(16, 185, 129, 0.7); transform: scale(1.05); }" +
            "        }" +
            "        .info-grid {" +
            "            display: grid;" +
            "            gap: 20px;" +
            "            margin: 30px 0;" +
            "        }" +
            "        .info-item {" +
            "            background: linear-gradient(135deg, rgba(88, 28, 135, 0.3) 0%, rgba(49, 46, 129, 0.3) 100%);" +
            "            border: 1px solid rgba(168, 85, 247, 0.2);" +
            "            border-radius: 16px;" +
            "            padding: 20px;" +
            "            backdrop-filter: blur(10px);" +
            "        }" +
            "        .info-label {" +
            "            color: #a5b4fc;" +
            "            font-size: 0.9em;" +
            "            margin-bottom: 8px;" +
            "            text-transform: uppercase;" +
            "            letter-spacing: 1px;" +
            "        }" +
            "        .info-value {" +
            "            color: #e9d5ff;" +
            "            font-size: 1.3em;" +
            "            font-weight: 600;" +
            "        }" +
            "        .back-button {" +
            "            display: inline-block;" +
            "            background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);" +
            "            color: white;" +
            "            padding: 15px 35px;" +
            "            text-decoration: none;" +
            "            border-radius: 12px;" +
            "            font-weight: 700;" +
            "            text-transform: uppercase;" +
            "            letter-spacing: 1.5px;" +
            "            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" +
            "            box-shadow: 0 10px 30px rgba(168, 85, 247, 0.4);" +
            "            margin-top: 20px;" +
            "        }" +
            "        .back-button:hover {" +
            "            background: linear-gradient(135deg, #ec4899 0%, #a855f7 100%);" +
            "            box-shadow: 0 15px 40px rgba(168, 85, 247, 0.5);" +
            "            transform: translateY(-3px);" +
            "        }" +
            "    </style>" +
            "</head>" +
            "<body>" +
            "    <div class='health-container'>" +
            "        <div class='status-indicator'>✓</div>" +
            "        <h1>System Operational</h1>" +
            "        <div class='info-grid'>" +
            "            <div class='info-item'>" +
            "                <div class='info-label'>Status</div>" +
            "                <div class='info-value'>UP & Running</div>" +
            "            </div>" +
            "            <div class='info-item'>" +
            "                <div class='info-label'>Service</div>" +
            "                <div class='info-value'>Analytics API</div>" +
            "            </div>" +
            "            <div class='info-item'>" +
            "                <div class='info-label'>Timestamp</div>" +
            "                <div class='info-value'>" + java.time.LocalDateTime.now().toString() + "</div>" +
            "            </div>" +
            "        </div>" +
            "        <a href='/analytics-dashboard/stats' class='back-button'>← Back to Dashboard</a>" +
            "    </div>" +
            "</body>" +
            "</html>";
        
        return Response.ok()
            .entity(html)
            .build();
    }

    /**
     * ENDPOINT 1 : Ingestion d'un événement unique
     * POST /api/v1/analytics/events
     *
     * À COMPLÉTER PAR LES ÉTUDIANTS
     *
     * Objectifs :
     * - Accepter un événement ViewEvent en JSON
     * - Le valider et l'insérer dans MongoDB
     * - Retourner un accusé de réception
     */
    @POST
    @Path("/events")
    public Response ingestEvent(ViewEvent event) {
        try {
            // TODO: Implémenter l'ingestion
            // Utiliser eventProcessor.processEvent(event)

            return Response.status(Response.Status.CREATED)
                .entity("{\"message\":\"Event ingested successfully\"}")
                .build();

        } catch (Exception e) {
            // TODO: Améliorer la gestion d'erreurs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"" + e.getMessage() + "\"}")
                .build();
        }
    }
    
    /**
     * ENDPOINT 2 : Ingestion batch (lot d'événements)
     * POST /api/v1/analytics/events/batch
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     * 
     * Objectifs :
     * - Accepter une liste d'événements
     * - Insérer en batch pour optimiser les performances
     * - Retourner le nombre d'événements traités
     */
    @POST
    @Path("/events/batch")
    public Response ingestBatch(List<ViewEvent> events) {
        // TODO: Implémenter l'ingestion batch
        // Utiliser eventProcessor.processBatch(events)
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * ENDPOINT 3 : Top vidéos les plus vues
     * GET /api/v1/analytics/videos/top?limit=10
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     */
    @GET
    @Path("/videos/top")
    public Response getTopVideos(@QueryParam("limit") @DefaultValue("10") int limit) {
        // TODO: Récupérer les top vidéos
        // Utiliser eventProcessor.getTopVideos(limit)
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * ENDPOINT 4 : Statistiques pour une vidéo spécifique
     * GET /api/v1/analytics/videos/{videoId}/stats
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     */
    @GET
    @Path("/videos/{videoId}/stats")
    public Response getVideoStats(@PathParam("videoId") String videoId) {
        // TODO: Récupérer les stats pour cette vidéo
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * ENDPOINT 5 : Recommandations personnalisées
     * GET /api/v1/analytics/users/{userId}/recommendations?limit=5
     * 
     * À COMPLÉTER PAR LES ÉTUDIANTS
     */
    @GET
    @Path("/users/{userId}/recommendations")
    public Response getRecommendations(
            @PathParam("userId") String userId,
            @QueryParam("limit") @DefaultValue("5") int limit) {
        // TODO: Générer des recommandations
        
        return Response.status(Response.Status.NOT_IMPLEMENTED)
            .entity("{\"error\":\"À implémenter\"}")
            .build();
    }
    
    /**
     * BONUS : Streaming temps réel avec Server-Sent Events
     * GET /api/v1/analytics/realtime/stream
     *
     * AVANCÉ - Optionnel
     */
    // @GET
    // @Path("/realtime/stream")
    // @Produces(MediaType.SERVER_SENT_EVENTS)
    // public void streamRealtime(@Context SseEventSink sseEventSink, @Context Sse sse) {
    //     // TODO: Implémenter le streaming SSE
    //     // Envoyer des stats toutes les 2 secondes
    // }
}


/**
 * NOTES POUR LES ÉTUDIANTS :
 * 
 * 1. VALIDATION
 *    - Valider tous les paramètres d'entrée
 *    - Vérifier les IDs ne sont pas vides
 *    - Valider les limites (ex: limit entre 1 et 100)
 * 
 * 2. GESTION D'ERREURS
 *    - Utiliser try-catch appropriés
 *    - Retourner des codes HTTP corrects (200, 201, 400, 404, 500)
 *    - Messages d'erreur clairs et utiles
 * 
 * 3. PERFORMANCE
 *    - Éviter les requêtes N+1
 *    - Utiliser la pagination pour les résultats volumineux
 *    - Considérer le cache pour les données fréquemment accédées
 * 
 * 4. SÉCURITÉ
 *    - Valider et nettoyer les entrées
 *    - Limiter les tailles de batch
 *    - Rate limiting (bonus)
 * 
 * 5. TESTS
 *    - Tester chaque endpoint avec Postman ou curl
 *    - Vérifier les cas limites
 *    - Mesurer les performances
 */