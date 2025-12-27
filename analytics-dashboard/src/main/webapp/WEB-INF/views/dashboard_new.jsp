<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Streaming Analytics Dashboard - Real-time Monitoring</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;
            background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #312e81 100%);
            color: #e2e8f0; min-height: 100vh; padding: 20px; position: relative;
        }
        body::before {
            content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0;
            background: radial-gradient(circle at 30% 70%, rgba(14, 165, 233, 0.1) 0%, transparent 50%),
                        radial-gradient(circle at 70% 30%, rgba(168, 85, 247, 0.1) 0%, transparent 50%);
            pointer-events: none;
        }
        .dashboard-container {
            max-width: 1400px; margin: 0 auto; background: rgba(30, 27, 75, 0.85);
            border-radius: 24px; box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3), 0 0 80px rgba(168, 85, 247, 0.1),
                        inset 0 1px 0 rgba(255, 255, 255, 0.1);
            overflow: hidden; backdrop-filter: blur(30px); border: 1px solid rgba(168, 85, 247, 0.2);
            position: relative; z-index: 1;
        }
        .dashboard-header {
            background: linear-gradient(135deg, #6366f1 0%, #a855f7 50%, #ec4899 100%);
            color: white; padding: 40px; text-align: center; position: relative; overflow: hidden;
        }
        .dashboard-header::before {
            content: ''; position: absolute; top: -50%; left: -50%; width: 200%; height: 200%;
            background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
            animation: pulse 8s ease-in-out infinite;
        }
        @keyframes pulse {
            0%, 100% { transform: translate(0, 0); }
            50% { transform: translate(5%, 5%); }
        }
        .dashboard-header h1 {
            font-size: 2.8em; font-weight: 800; margin-bottom: 10px; position: relative;
            z-index: 1; letter-spacing: -1px;
        }
        .dashboard-header p { font-size: 1.2em; opacity: 0.95; position: relative; z-index: 1; }
        .stats-grid {
            display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 25px; padding: 40px;
        }
        .stat-card {
            background: linear-gradient(135deg, rgba(88, 28, 135, 0.3) 0%, rgba(49, 46, 129, 0.3) 100%);
            border-radius: 16px; padding: 28px; text-align: center;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2); border: 1px solid rgba(168, 85, 247, 0.2);
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); backdrop-filter: blur(10px);
        }
        .stat-card:hover {
            transform: translateY(-8px) scale(1.03); box-shadow: 0 20px 40px rgba(168, 85, 247, 0.3);
            border-color: rgba(168, 85, 247, 0.5);
            background: linear-gradient(135deg, rgba(88, 28, 135, 0.5) 0%, rgba(49, 46, 129, 0.5) 100%);
        }
        .stat-number {
            font-size: 3.2em; font-weight: 800; background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
            -webkit-background-clip: text; -webkit-text-fill-color: transparent;
            background-clip: text; margin-bottom: 10px;
        }
        .stat-label { font-size: 1em; color: #c4b5fd; font-weight: 600; letter-spacing: 0.5px; }
        .content-section {
            padding: 40px; border-bottom: 1px solid rgba(168, 85, 247, 0.1);
        }
        .content-section:last-child { border-bottom: none; }
        .section-title {
            font-size: 2em; background: linear-gradient(135deg, #e9d5ff 0%, #fae8ff 100%);
            -webkit-background-clip: text; -webkit-text-fill-color: transparent;
            background-clip: text; margin-bottom: 30px; font-weight: 700;
        }
        .data-table {
            width: 100%; border-collapse: collapse; margin-top: 20px;
            border-radius: 12px; overflow: hidden; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        }
        .data-table th, .data-table td {
            padding: 16px 20px; text-align: left; border-bottom: 1px solid rgba(168, 85, 247, 0.1);
        }
        .data-table th {
            background: linear-gradient(135deg, #6366f1 0%, #a855f7 100%);
            color: white; font-weight: 700; font-size: 0.95em;
            text-transform: uppercase; letter-spacing: 0.5px;
        }
        .data-table tr:nth-child(even) { background-color: rgba(88, 28, 135, 0.15); }
        .data-table tr:hover {
            background-color: rgba(168, 85, 247, 0.2);
            transform: scale(1.01); transition: all 0.3s ease;
        }
        .data-table td { color: #e2e8f0; }
        .recommendation-card {
            background: linear-gradient(135deg, rgba(88, 28, 135, 0.3) 0%, rgba(49, 46, 129, 0.3) 100%);
            border-radius: 12px; padding: 20px; margin: 15px 0;
            border-left: 4px solid #a855f7; transition: all 0.3s ease;
        }
        .recommendation-card:hover {
            transform: translateX(5px); box-shadow: 0 8px 16px rgba(168, 85, 247, 0.3);
            border-left-color: #ec4899;
        }
        .recommendation-card h3 { color: #e9d5ff; margin-bottom: 10px; font-size: 1.1em; }
        .recommendation-card p { color: #c4b5fd; font-size: 0.95em; margin: 5px 0; }
        .recommendation-card em { color: #9f7aea; display: block; margin-top: 8px; font-style: italic; }
        .recommendations-grid {
            display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px; margin-top: 20px;
        }
        .input-group {
            margin-bottom: 30px; display: flex; gap: 10px;
            align-items: center; flex-wrap: wrap;
        }
        .input-group input {
            padding: 12px 18px; border-radius: 8px;
            border: 1px solid rgba(168, 85, 247, 0.3);
            background: rgba(168, 85, 247, 0.1); color: #e2e8f0;
            flex: 1; min-width: 250px; max-width: 400px; font-size: 1em;
        }
        .input-group input:focus {
            outline: none; border-color: #a855f7; box-shadow: 0 0 15px rgba(168, 85, 247, 0.3);
        }
        .btn {
            padding: 12px 28px; background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
            color: white; border: none; border-radius: 8px; cursor: pointer;
            font-weight: 600; font-size: 1em; transition: all 0.3s ease;
        }
        .btn:hover { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(168, 85, 247, 0.4); }
        .back-link {
            display: inline-block; margin: 20px 40px; color: #c4b5fd;
            text-decoration: none; font-weight: 600; padding: 12px 28px;
            border-radius: 12px; border: 2px solid rgba(168, 85, 247, 0.5);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            background: rgba(168, 85, 247, 0.1); backdrop-filter: blur(10px);
            position: relative; z-index: 2;
        }
        .back-link:hover {
            background: linear-gradient(135deg, #a855f7 0%, #ec4899 100%);
            color: white; transform: translateX(-5px); box-shadow: 0 8px 20px rgba(168, 85, 247, 0.4);
            border-color: transparent;
        }
        @media (max-width: 768px) {
            .stats-grid { grid-template-columns: 1fr; padding: 20px; }
            .content-section { padding: 20px; }
            .dashboard-header { padding: 30px 20px; }
            .dashboard-header h1 { font-size: 2em; }
            .input-group { flex-direction: column; align-items: stretch; }
            .input-group input { max-width: 100%; }
        }
    </style>
</head>
