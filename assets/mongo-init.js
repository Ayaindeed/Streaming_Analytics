// Script d'initialisation MongoDB
// Ce fichier sera exécuté automatiquement au démarrage de MongoDB

// Créer la base de données
db = db.getSiblingDB('streaming_analytics');

// Créer les collections
db.createCollection('events');
db.createCollection('video_stats');
db.createCollection('user_profiles');
db.createCollection('videos');

// Créer les index pour optimiser les performances
print('📊 Création des index...');

// Index sur events
db.events.createIndex({ "userId": 1 });
db.events.createIndex({ "videoId": 1 });
db.events.createIndex({ "timestamp": -1 });
db.events.createIndex({ "userId": 1, "timestamp": -1 });
db.events.createIndex({ "videoId": 1, "timestamp": -1 });

// Index sur video_stats
db.video_stats.createIndex({ "videoId": 1 }, { unique: true });
db.video_stats.createIndex({ "totalViews": -1 });
db.video_stats.createIndex({ "lastUpdated": -1 });

// Index sur user_profiles
db.user_profiles.createIndex({ "userId": 1 }, { unique: true });

// Index sur videos
db.videos.createIndex({ "videoId": 1 }, { unique: true });
db.videos.createIndex({ "category": 1 });
db.videos.createIndex({ "views": -1 });

print('✅ Index créés avec succès');

// Insérer quelques données de test
print('📝 Insertion de données de test...');

db.videos.insertMany([
  {
    videoId: "video_1",
    title: "Introduction au Big Data",
    category: "Documentary",
    duration: 1800,
    uploadDate: new Date(),
    views: 15000,
    likes: 1200
  },
  {
    videoId: "video_2",
    title: "Jakarta EE en action",
    category: "Educational",
    duration: 2400,
    uploadDate: new Date(),
    views: 8500,
    likes: 750
  },
  {
    videoId: "video_3",
    title: "MongoDB pour débutants",
    category: "Educational",
    duration: 1500,
    uploadDate: new Date(),
    views: 12000,
    likes: 950
  }
]);

db.video_stats.insertMany([
  {
    videoId: "video_1",
    totalViews: 15000,
    avgDuration: 1350.5,
    uniqueViewers: 12500,
    lastUpdated: new Date()
  },
  {
    videoId: "video_2",
    totalViews: 8500,
    avgDuration: 1800.2,
    uniqueViewers: 7200,
    lastUpdated: new Date()
  },
  {
    videoId: "video_3",
    totalViews: 12000,
    avgDuration: 980.7,
    uniqueViewers: 9800,
    lastUpdated: new Date()
  }
]);

print('✅ Données de test insérées');

// Créer un utilisateur pour l'application
db.createUser({
  user: "streaming_app",
  pwd: "app_password_123",
  roles: [
    { role: "readWrite", db: "streaming_analytics" }
  ]
});

print('✅ Utilisateur application créé');

// Afficher les statistiques
print('\n📈 Statistiques de la base de données :');
print('Collections : ' + db.getCollectionNames().length);
print('Événements : ' + db.events.countDocuments());
print('Vidéos : ' + db.videos.countDocuments());
print('Stats vidéos : ' + db.video_stats.countDocuments());

print('\n✅ Initialisation MongoDB terminée avec succès !');
