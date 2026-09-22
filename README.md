# Byte Club — MOVA Fitness Tracking Application (PROG7314 POE Part 2) 

Youtube link: https://www.youtube.com/watch?v=n7WB0g379pA

**Hello from Byte Club!**

We are **Byte Club**, a group of developers who worked together to create **MOVA**, a fitness tracking application designed to help users manage their workouts, track their fitness progress, participate in challenges, and stay motivated through rewards and achievements.

MOVA is a native Android fitness tracking application built with **Kotlin** and **Jetpack Compose**, powered by a custom **Node.js / Express REST API** with a **MongoDB** database. The project was developed as a group POE for PROG7314, following a Research → Plan → Design → Build → Evaluate cycle.

The application enables users to register and log in (with single sign-on and biometric authentication planned for the final POE), build custom workouts from a library of exercises, log completed workouts with automatic calorie calculation, join time-based fitness challenges, earn achievement badges, and track personal fitness progress through a dedicated analytics dashboard.

Our team split the work across three feature areas:

* **Member 1 — Alwande Gwala:** Authentication, Profile & Settings
* **Member 2 — Samkelisiwe Gamede:** Foundation, Backend & Workout/Exercise Module *(Group Leader)*
* **Member 3 — Njabulo Mhlangu:** Progress & Gamification

---

## Table of Contents

* [Member Contributions](#member-contributions)

  * [Member 1 — Alwande Gwala: Authentication, Profile & Settings](#member-1--alwande-gwala-authentication-profile--settings)
  * [Member 2 — Samkelisiwe Gamede: Foundation & Workout/Exercise Module](#member-2--samkelisiwe-gamede-foundation--workoutexercise-module)
  * [Member 3 — Njabulo Mhlangu: Progress & Gamification](#member-3--njabulo-mhlangu-progress--gamification)
* [Build Instructions](#build-instructions)

  * [Prerequisites](#prerequisites)
  * [Clone the Repository](#clone-the-repository)
  * [Start the Backend](#start-the-backend)
  * [Configure the App's API Base URL](#configure-the-apps-api-base-url)
  * [Build and Run the Android App](#build-and-run-the-android-app)
  * [Run Unit Tests](#run-unit-tests)
  * [Environment Variables](#environment-variables-mova-apienv)
* [AI Write-Up Report](#ai-write-up-report)
* [References](#references)


## Member Contributions

## Member 1 — Alwande Gwala: Authentication, Profile & Settings

### Overview

Responsible for user authentication, session persistence, profile management,
and app settings.

### Features Delivered

#### 1. Authentication

* **Register** — new users sign up with email + password. Passwords hashed
  with bcrypt on the backend.
* **Login** — existing users sign in; user ID persisted across restarts.
* **SSO (partial)** — Google/Apple sign-in UI present; full SDK integration
  planned for the final POE.

#### 2. Session Management

* Persistent session via **DataStore Preferences**.
* App restarts skip authentication if a session is active.
* Logout clears all session keys and returns to login.

#### 3. Post-Registration Onboarding

* New users land on an onboarding screen to capture height, weight, age,
  gender, fitness level, and fitness goals.
* Data is PUT to `/api/users/:userId` and used to personalise workouts.

#### 4. Profile Management

* View profile: name, avatar (initials), height, weight, age, fitness level.
* Edit Profile: all fields editable with validation.
* Fitness Objectives: user can add/remove multiple short-term objectives
  (stored as an array on the backend).

#### 5. Settings

* Dark mode toggle (persisted, applied app-wide)
* Larger text toggle
* High contrast toggle
* Biometric authentication toggle (persisted)
* Multi-language selection: English, isiZulu, Sesotho
* All persisted via `PUT /api/users/:userId/settings`

### Technical Highlights

* **MVVM architecture** with Hilt DI
* **Offline-first** — RoomDB caches user data; repository prefers local first
* **Retrofit + Gson** for REST API
* **DataStore** for session persistence
* **StateFlow** for reactive UI state

---

## Member 2 — Samkelisiwe Gamede: Foundation & Workout/Exercise Module

### Overview

Responsible for establishing the project foundation — repository structure, Git workflow, backend API, and the complete Workout & Exercise module. Also set up and maintained the shared infrastructure that all other team members built upon: the Node.js/Express REST API, MongoDB database, GitHub Actions CI, and the shared Jetpack Compose navigation, theme, and component system.

### Features Delivered

#### 1. Project Foundation

* Initialised the GitHub repository with branch strategy, `.gitignore` rules, and commit conventions.
* Configured Gradle (Compose, Hilt, Retrofit, Room, Biometric, DataStore) and resolved version conflicts across the team's Android projects.
* Established the Jetpack Compose navigation graph (`NavGraph.kt`) with bottom navigation connecting all member screens.
* Built the shared theme (`Color.kt`, `Theme.kt`, `Type.kt`) and reusable components (`MovaTopBar`) used across every screen.

#### 2. Backend & REST API

* Designed and built the Node.js/Express backend with MongoDB (Mongoose).
* Configured database connection, environment variables, and error handling middleware.
* Authored most models and route modules tes`.
* Implemented the MET-based calorie calculator (`calorieCalculator.js`) that powers workout logging.

#### 3. Workout Dashboard

* Loads system workouts and user-created workouts from the REST API.
* Four sections: **Jump Back In** (recently viewed), **Challenges**, **Exercises**, and **My Workouts**.
* Custom workouts render with selectable icons; system workouts use bundled thumbnail images.
* Jump Back In state persisted across app restarts using SharedPreferences.

#### 4. Workout Builder & Logger

* Single form with **Create Custom Workout**
* Exercise checklist fetched live from the backend.
* Inline sets/reps steppers per selected exercise.
* Icon picker (fitness, fire, star, run).
* Live calorie calculation using exercise MET values and user weight.
* Live workout search with fuzzy matching.
* Configured GitHub Actions CI workflow (`android-ci.yml`) to build and test on every push.
* Authored the seed script (`seed.js`) providing 12 system workouts and 16 exercises for the team to build against.

### Technical Highlights

* **MVVM** architecture with **Repository pattern** for the workout module.
* **Retrofit + Gson** with a logging interceptor for API calls.
* **SharedPreferences** for local recent-workout tracking.
* **HorizontalPager** (Figma-aligned) for the builder/logger form layout.
* **MET-based calorie formula**: `MET × weight(kg) × duration(hours)`.
* **Junction table design** (`WorkoutExercise`) instead of embedded arrays, enabling many-to-many workout/exercise relationships.
* State-driven Compose UI with `LaunchedEffect`, `remember`, and reactive `mutableStateOf`.
* All API endpoints validated via Postman and cross-checked against MongoDB Compass.


## Member 3 — Njabulo Mhlangu: Progress & Gamification

### Overview

Responsible for the Progress & Gamification module — the fitness tracking
dashboard experience that lets users join challenges, build streaks, earn
badges, and view personal analytics. Bridges Jetpack Compose UI with
Node.js / Express backend services.

### Features Delivered

#### 1. Challenges

* Browse active fitness challenges (7, 10, and 30-day durations).
* Join a challenge with a single tap; user enrollment persisted to backend.
* Track progress: current day, completion percentage, streak count.
* Custom notes attached to daily challenge milestones.

#### 2. Rewards

* Unlock badges on milestone completion.
* Track streaks and achievement history.
* Badge catalogue viewable from the Rewards tab.

#### 3. Analytics

* Dashboard showing user fitness consistency.
* Performance graphs and workout history insights.
* Milestone tracking visualised for the user.

### Unit Tests

| Test class          | Coverage                                                          |
| ------------------- | ----------------------------------------------------------------- |
| `AuthViewModelTest` | register success/failure, login success/missing userId, reset     |
| `UserViewModelTest` | load from session, no-session error, add/remove objective, logout |

Run with:

```bash
./gradlew testDebugUnitTest
```

---

## Build Instructions

### Prerequisites

* **Android Studio** Hedgehog (2023.1.1) or newer
* **JDK 17** (bundled with Android Studio)
* **Node.js 18+** and **npm**
* **MongoDB Community Edition** running locally on `mongodb://localhost:27017`
* **Git**

### Clone the Repository

```bash
git clone https://github.com/EMKNDW/prog7314-g2-2026-prog7314-2026-poe-abonjile.git
cd prog7314-g2-2026-prog7314-2026-poe-abonjile
```

### Start the Backend

```bash
cd mova-api
npm install
node seed.js       # seeds 12 workouts + 16 exercises into MongoDB
npm run dev        # starts the API on http://localhost:3000
```

The `seed.js` script only needs to be run **once**. It populates the local MongoDB with:

* 16 exercises (Push-ups, Squats, Plank, Lunges, etc.)
* 12 system workouts (Abs Workout, HIIT Workout, Arm Strength, etc.)
* All workout–exercise junction links

### Configure the App's API Base URL

Open `app/src/main/java/com/example/mova/data/remote/api/ApiService.kt` and confirm `BASE_URL`:

```kotlin
const val BASE_URL = "http://10.0.2.2:3000/"   // Android emulator
```

For a **physical device**, replace `10.0.2.2` with your PC's LAN IP (e.g. `http://192.168.1.42:3000/`) and ensure the device is on the same WiFi network.

### Build and Run the Android App

```bash
./gradlew assembleDebug
```

Or open the project in Android Studio and press **Run**.

### Run Unit Tests

```bash
./gradlew testDebugUnitTest
```

### Environment Variables (`mova-api/.env`)

```text
PORT=3000
MONGODB_URI=mongodb://localhost:27017/mova_db
JWT_SECRET=your_super_secret_jwt_key_here
NODE_ENV=development
```

> **Note:** `android.enableJetifier=true` is set in `gradle.properties` and is required for the current AndroidX dependency configuration.

---

## AI Write-Up Report

AI tools were used in the development of MOVA only for the generation of visual assets used within the application. AI was not used to generate the application's source code, database structure, backend functionality, Android functionality, documentation, or other core development work.

The AI-generated visual assets were created for the workout thumbnails and exercise illustrations used throughout the MOVA application. The workout thumbnails provide visual representations for the different workouts available in the application, while the exercise illustrations provide users with visual guidance for the exercises included in the workout library.

The images were generated specifically for the MOVA project and were created without using photographs or identifiable human features belonging to real individuals. The images do not represent members of the development team or any other identifiable person. No third-party photographs, characters, logos, or other identifiable intellectual property were intentionally used as source material for these generated assets.

AI was therefore limited to supporting the visual design aspect of the application. All application functionality, including the Android application, REST API, MongoDB database, workout functionality, authentication, navigation, calorie calculations, challenges, progress tracking, and other implemented features, was developed as part of the group's software development work.

The AI conversation used to generate the visual assets can be accessed here:

https://chatgpt.com/share/6ab2cb7b-e284-83e9-a938-2421aeae3927


## References

* Android Developers (2023). *Write unit tests for ViewModel | Android Developers*. [online] Android Developers. Available at: https://developer.android.com/codelabs/basic-android-kotlin-compose-test-viewmodel [Accessed 19 Sept. 2026].

* Android Developers (2026a). *App architecture*. [online] Android Developers. Available at: https://developer.android.com/topic/architecture/intro [Accessed 14 Sept. 2026].

* Android Developers (2026b). *App architecture: Data layer - DataStore*. [online] Android Developers. Available at: https://developer.android.com/topic/libraries/architecture/datastore [Accessed 15 Sept. 2026].

* Android Developers (2026c). *Custom design systems in Compose*. [online] Android Developers. Available at: https://developer.android.com/develop/ui/compose/designsystems/custom [Accessed 15 Sept. 2026].

* Android Developers (2026d). *Dependency injection with Hilt (views)*. [online] Android Developers. Available at: https://developer.android.com/topic/architecture/views/dependency-injection/hilt-android-views [Accessed 18 Sept. 2026].

* Android Developers (2026e). *Get started with Jetpack Compose*. [online] Android Developers. Available at: https://developer.android.com/develop/ui/compose/documentation [Accessed 15 Sept. 2026].

* Android Developers (2026f). *Keep rule use cases and examples*. [online] Android Developers. Available at: https://developer.android.com/topic/performance/app-optimization/keep-rule-examples [Accessed 17 Sept. 2026].

* Android Developers (2026g). *Material Design 3 in Compose*. [online] Android Developers. Available at: https://developer.android.com/develop/ui/compose/designsystems/material3 [Accessed 16 Sept. 2026].

* Android Developers (2026h). *Navigation*. [online] Android Developers. Available at: https://developer.android.com/guide/navigation [Accessed 15 Sept. 2026].

* Android Developers (2026i). *Network stacks*. [online] Android Developers. Available at: https://developer.android.com/media/media3/exoplayer/network-stacks [Accessed 14 Sept. 2026].

* Android Developers (2026j). *Recommendations for Android architecture*. [online] Android Developers. Available at: https://developer.android.com/topic/architecture/recommendations [Accessed 14 Sept. 2026].

* CodeSignal (2026). *Defining Mongoose Schemas and Models*. [online] CodeSignal Learn. Available at: https://codesignal.com/learn/courses/managing-data-with-mongodb-and-mongoose/lessons/defining-mongoose-schemas-and-models [Accessed 12 Sept. 2026].

* ExpressJS (2026). *Routing · Express.js*. [online] Express.js. Available at: https://expressjs.com/en/5x/guide/routing/ [Accessed 16 Sept. 2026].

* GeeksforGeeks (2019). *MongoDB create database using Mongo shell*. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/mongodb/mongodb-create-database-using-mongoshell/ [Accessed 16 Sept. 2026].

* GeeksforGeeks (2022). *Mongoose schemas creating a model*. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/mongodb/mongoose-schemas-creating-a-model/ [Accessed 12 Sept. 2026].

* GeeksforGeeks (2024). *Routing in Express.js*. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/node-js/what-is-routing-in-express/ [Accessed 15 Sept. 2026].

* *How to setup routes with Express Router | Node.js & Express tutorials for beginners* (2021). [Video] Directed by Dave Gray. YouTube. Available at: https://www.youtube.com/watch?v=Zh7psmf1KAA [Accessed 15 Sept. 2026].

* JetBrains (2026). *Kotlin Documentation*. [online] Available at: https://kotlinlang.org/docs/home.html [Accessed 1 September 2026].

* Kotlin (2026). *Serialization | Kotlin*. [online] Kotlin Help. Available at: https://kotlinlang.org/docs/serialization.html [Accessed 16 Sept. 2026].

* Mehta, P. (2026). *How to use bcryptjs from NPM safely: A security review*. [online] Safeguard. Available at: https://safeguard.sh/resources/blog/bcryptjs-npm [Accessed 17 Sept. 2026].

* Mohit Khubchandani (2024). *A beginner’s guide to mongoose schema properties with examples*. [online] Medium. Available at: https://medium.com/@mohitkhubchandani88/a-beginners-guide-to-mongoose-schema-properties-with-examples-d8b946dcef86 [Accessed 17 Sept. 2026].

* *Routing in Express.js (Node.js) - ‘app’ & express.Router, route params, query, body, etc.* (2023). [Video] Directed by ZestMade. YouTube. Available at: https://www.youtube.com/watch?v=5lHkMpVD4AM [Accessed 15 Sept. 2026].

* The Independent Institute of Education (2026). *Programming 3D Module Manual*. Unpublished.

* The NodeSource Blog (2025). *How Node.js works: A comprehensive guide in 2025*. [online] The NodeSource Blog - Node.js Tutorials, Guides, and Updates. Available at: https://nodesource.com/blog/how-nodejs-works [Accessed 12 Sept. 2026].

* Traversy Media (2024). *Node.js Crash Course*. [online video] YouTube. Available at: https://www.youtube.com/watch?v=32M1al-Y6Ag [Accessed 13 Sept. 2026].

* W3Schools (2026). *Kotlin Tutorial*. [online] Available at: https://www.w3schools.com/kotlin/index.php [Accessed 1 September 2026].

* lysine-dev (2026). *OkHttp*. [online] GitHub. Available at: https://github.com/lysine-dev/okhttp/tree/main
