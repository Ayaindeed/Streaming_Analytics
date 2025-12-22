<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Streaming Analytics Dashboard - Accueil</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;
            background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
            color: #e8eaf6;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            position: relative;
            overflow-x: hidden;
        }

        body::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: radial-gradient(circle at 20% 80%, rgba(168, 85, 247, 0.15) 0%, transparent 50%),
                        radial-gradient(circle at 80% 20%, rgba(59, 130, 246, 0.15) 0%, transparent 50%);
            pointer-events: none;
        }

        .modern-container {
            max-width: 900px;
            width: 100%;
            background: rgba(30, 27, 75, 0.85);
            border-radius: 24px;
            padding: 50px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3),
                        0 0 80px rgba(168, 85, 247, 0.1),
                        inset 0 1px 0 rgba(255, 255, 255, 0.1);
            text-align: center;
            backdrop-filter: blur(30px);
            border: 1px solid rgba(168, 85, 247, 0.2);
            position: relative;
            z-index: 1;
        }

        h1 {
            color: #a855f7;
            font-size: 3.5em;
            font-weight: 800;
            margin-bottom: 20px;
            background: linear-gradient(135deg, #a855f7 0%, #ec4899 50%, #06b6d4 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            letter-spacing: -1px;
        }

        .subtitle {
            color: #c4b5fd;
            font-size: 1.3em;
            font-weight: 400;
            margin-bottom: 40px;
            letter-spacing: 0.5px;
        }

        .welcome-text {
            font-size: 1.1em;
            color: #d4d4d4;
            line-height: 1.7;
            margin: 15px 0;
            font-weight: 400;
        }

        .feature-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 30px;
            margin: 40px 0;
        }

        .feature-card {
            background: linear-gradient(135deg, rgba(88, 28, 135, 0.3) 0%, rgba(49, 46, 129, 0.3) 100%);
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
            border: 1px solid rgba(168, 85, 247, 0.2);
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            backdrop-filter: blur(10px);
        }

        .feature-card:hover {
            transform: translateY(-8px) scale(1.02);
            box-shadow: 0 20px 40px rgba(168, 85, 247, 0.3),
                        0 0 60px rgba(236, 72, 153, 0.2);
            border-color: rgba(168, 85, 247, 0.5);
            background: linear-gradient(135deg, rgba(88, 28, 135, 0.5) 0%, rgba(49, 46, 129, 0.5) 100%);
        }

        .feature-icon {
            font-size: 2.5em;
            margin-bottom: 15px;
            display: block;
        }

        .feature-title {
            color: #e9d5ff;
            font-size: 1.2em;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .feature-description {
            color: #b8b8b8;
            font-size: 0.95em;
            line-height: 1.5;
        }

        .modern-button {
            display: inline-block;
            background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
            color: white;
            padding: 18px 45px;
            text-decoration: none;
            border-radius: 12px;
            font-size: 1.1em;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 1.5px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 10px 30px rgba(168, 85, 247, 0.4);
            margin-top: 30px;
            border: none;
            cursor: pointer;
            position: relative;
            overflow: hidden;
        }

        .modern-button::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
            transition: left 0.5s;
        }

        .modern-button:hover::before {
            left: 100%;
        }

        .modern-button:hover {
            transform: translateY(-3px) scale(1.05);
            box-shadow: 0 15px 40px rgba(168, 85, 247, 0.5);
            background: linear-gradient(135deg, #ec4899 0%, #a855f7 100%);
        }

        .modern-footer {
            margin-top: 50px;
            padding-top: 30px;
            border-top: 1px solid rgba(168, 85, 247, 0.2);
            color: #a5b4fc;
            font-size: 0.9em;
        }

        .tech-stack {
            display: flex;
            justify-content: center;
            flex-wrap: wrap;
            gap: 12px;
            margin-top: 20px;
        }

        .tech-item {
            background: rgba(168, 85, 247, 0.1);
            padding: 8px 18px;
            border-radius: 20px;
            font-size: 0.85em;
            color: #c4b5fd;
            border: 1px solid rgba(168, 85, 247, 0.3);
            transition: all 0.3s ease;
        }

        .tech-item:hover {
            background: rgba(168, 85, 247, 0.2);
            border-color: rgba(168, 85, 247, 0.5);
            transform: scale(1.05);
        }

        @media (max-width: 768px) {
            .modern-container {
                padding: 30px 20px;
            }

            h1 {
                font-size: 2.5em;
            }

            .feature-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="modern-container">
        <h1>Streaming Analytics Dashboard</h1>
        <p class="subtitle">Advanced Big Data Analytics Platform for Video Streaming</p>

        <p class="welcome-text">Welcome to your comprehensive streaming analytics platform. Monitor real-time performance, analyze user behavior, and gain insights from your video content.</p>

        <div class="feature-grid">
            <div class="feature-card">
                <span class="feature-icon">📊</span>
                <div class="feature-title">Real-time Analytics</div>
                <div class="feature-description">Monitor live streaming metrics, user engagement, and content performance with instant updates.</div>
            </div>

            <div class="feature-card">
                <span class="feature-icon">🎯</span>
                <div class="feature-title">Smart Recommendations</div>
                <div class="feature-description">AI-powered content suggestions based on user preferences and viewing patterns.</div>
            </div>

            <div class="feature-card">
                <span class="feature-icon">📈</span>
                <div class="feature-title">Performance Insights</div>
                <div class="feature-description">Detailed analytics on video popularity, user demographics, and engagement metrics.</div>
            </div>
        </div>

        <div>
            <a href="dashboard" class="modern-button">Access Dashboard</a>
        </div>

        <div class="modern-footer">
            <p>Advanced Streaming Analytics Platform - 2025</p>
            <div class="tech-stack">
                <span class="tech-item">Jakarta EE</span>
                <span class="tech-item">MongoDB</span>
                <span class="tech-item">Docker</span>
                <span class="tech-item">Big Data</span>
            </div>
        </div>
    </div>
</body>
</html>