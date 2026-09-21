MOVA App: Progress & Gamification Feature (feature/progress-gamification)

This branch contains the core implementation of the Progress & Gamification module for the Mova application. It bridges the Android Jetpack Compose frontend with the Node.js/Express backend to deliver an engaging fitness tracking experience complete with challenges, streak counters, milestone rewards, and personal analytics.

# Architecture & Component Structure
1. Android Frontend (Jetpack Compose)
Located under app/src/main/java/com/example/mova/:

UI Screens (ui/screens/):

analytics/AnalyticsScreen.kt & AnalyticsViewModel.kt - Displays user progress metrics, workout history insights, and performance graphs.

challenges/ChallengesScreen.kt - Lists active fitness challenges, allowing users to browse, select, and enroll.

rewards/RewardsScreen.kt - Showcases badges, unlocked milestones, and achievement tracking.

Data Layer (data/):

ChallengeApiService.kt - Retrofit API service interface connecting the Android client to backend challenge and progress endpoints.

2. Node.js Backend (src/)
Located under the root backend source directory:

Models (src/models/):

Challenge.js - MongoDB schema defining challenge parameters (title, description, duration days, difficulty levels, and active status).

Controllers (src/controllers/):

challengeController.js - Handles business logic for fetching active challenges (getAllChallenges), enrolling users (joinChallenge), and updating progress metrics, streaks, and custom notes (updateChallengeProgress).

Routes (src/routes/):

challengeRoutes.js - Exposes RESTful endpoints (GET /api/challenges, POST /api/challenges/join, PUT /api/challenges/:userChallengeId).

# Key Features
Interactive Challenges: Users can join challenges of varying durations (7, 10, or 30 days) and difficulty levels.

Streak & Milestone Tracking: Real-time updates record consecutive participation days and award badges upon completion.

Custom Reflections: Support for attaching custom user notes to daily challenge milestones.

Analytics Dashboard: Visual representation of user fitness consistency and milestone achievements.

# Configuration & Setup Notes
Android Dependencies: Requires AndroidX configuration flags in gradle.properties:

Properties- 
android.useAndroidX=true
android.enableJetifier=true

Backend Integration: Ensure the environment variables (MongoDB connection string, port configurations) are properly configured in your .env file before launching the Node.js server
