# DriveGuard

> An Android driving-behaviour monitoring application built with Kotlin, Jetpack Compose, Room, GPS and device-motion sensors.

[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Room](https://img.shields.io/badge/Data-Room%20%2F%20SQLite-3DDC84?logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

DriveGuard records journey data locally, detects potentially unsafe driving events through deterministic sensor rules, calculates a weighted safety score, and lets a driver review saved trips, incidents and route points. The current implementation is a privacy-first educational Android project: it does not upload journey data to a server and does not make insurance or road-safety decisions.

## What it demonstrates

- Native Android development with Kotlin and Jetpack Compose
- GPS speed and route-point collection with Android location APIs
- Accelerometer and gyroscope event processing
- Rule-based detection of speeding, harsh braking, sudden acceleration and sharp turns
- A weighted 0-100 driver-safety score
- Local relational persistence with Room and SQLite
- Reactive state handling with `StateFlow`
- Foreground-service lifecycle and notification handling
- Unit-testable business rules separated from the UI layer

## Application flow

```mermaid
flowchart LR
    A[Home dashboard] --> B[Start trip]
    B --> C[Request location and notification permissions]
    C --> D[Live monitoring]
    D --> E[Read GPS speed and route points]
    D --> F[Read accelerometer and gyroscope]
    E --> G[DetectionRules]
    F --> G
    G --> H[Record unsafe events]
    H --> I[ScoringCalculator]
    I --> J[Trip summary]
    J --> K[(Room database)]
    K --> L[Trip history]
    L --> M[Saved trip and incident details]
```

## Architecture

```mermaid
flowchart TB
    subgraph Presentation[Presentation layer]
        HS[HomeScreen]
        TS[TripScreen]
        SS[TripSummaryScreen]
        HIS[HistoryScreen]
        SD[SavedTripDetailScreen]
        ID[IncidentDetailScreen]
        SET[SettingsScreen]
    end

    VM[TripViewModel and StateFlow]

    subgraph Device[Device integration]
        GPS[LocationManager and GPS]
        ACC[Accelerometer]
        GYR[Gyroscope]
        FGS[TripMonitoringService]
    end

    subgraph Logic[Business logic]
        DR[DetectionRules]
        SC[ScoringCalculator]
    end

    subgraph Data[Persistence layer]
        DAO[TripDao]
        DB[(Room and SQLite)]
        ENT[Trip, Incident and RoutePoint entities]
    end

    Presentation --> VM
    VM --> Device
    Device --> Logic
    Logic --> VM
    VM --> DAO
    DAO --> DB
    DB --> ENT
```

## Detection and scoring workflow

```mermaid
sequenceDiagram
    participant Driver
    participant UI as TripScreen
    participant Sensors as GPS and motion sensors
    participant Rules as DetectionRules
    participant Score as ScoringCalculator
    participant DB as Room database

    Driver->>UI: Start trip
    UI->>Sensors: Begin location and sensor sampling
    loop While trip is active
        Sensors-->>UI: Speed, coordinates and motion values
        UI->>Rules: Evaluate rule thresholds
        alt Unsafe event detected
            Rules-->>UI: Event type and severity
            UI->>DB: Store incident and route data
        end
    end
    Driver->>UI: Stop trip
    UI->>Score: Calculate weighted safety score
    Score-->>UI: Score from 0 to 100
    UI->>DB: Save completed trip
    UI-->>Driver: Show trip summary
```

### Current rule thresholds

| Event | Rule in the published source | Score penalty |
|---|---:|---:|
| Speeding | Speed above 30 mph | -5 |
| Sudden acceleration | Acceleration above `12 m/s²` | -7 |
| Sharp turn | Absolute gyroscope value above `3` | -8 |
| Harsh braking | Acceleration below `-12 m/s²` | -10 |

Sensor-based incidents are only logged when the device has a valid location and the calculated speed is at least 5 mph. These values are project defaults, not validated safety or insurance standards.

## Screenshots

The gallery combines current driver-side screens with prototype/evaluation screens produced during the project. Screens marked **Prototype** illustrate planned authentication, fleet and administrator concepts; those concepts are not implemented in the published source.

### Current driver experience

| Home dashboard | Trip completed | Drive overview |
|---|---|---|
| <img src="docs/screenshots/12-home-dashboard.jpg" alt="DriveGuard home dashboard" width="250"> | <img src="docs/screenshots/11-trip-complete.jpg" alt="Completed trip safety summary" width="250"> | <img src="docs/screenshots/07-drive-overview.jpg" alt="Drive overview and risk insight" width="250"> |

| Settings | Privacy controls | Recorded safety alerts |
|---|---|---|
| <img src="docs/screenshots/05-settings.jpg" alt="DriveGuard settings" width="250"> | <img src="docs/screenshots/09-privacy-controls.jpg" alt="Local data and privacy controls" width="250"> | <img src="docs/screenshots/10-drive-alerts.jpg" alt="Recorded driving alerts" width="250"> |

| Alert summary |
|---|
| <img src="docs/screenshots/08-safety-alerts.jpg" alt="Safety alert summary" width="250"> |

### Prototype and future-facing concepts

| Sign-in prototype | Administrator dashboard | Driver list |
|---|---|---|
| <img src="docs/screenshots/01-sign-in-prototype.jpg" alt="Prototype sign-in screen" width="250"> | <img src="docs/screenshots/02-admin-dashboard-prototype.jpg" alt="Prototype administrator dashboard" width="250"> | <img src="docs/screenshots/03-driver-list-prototype.jpg" alt="Prototype driver list" width="250"> |

| Driver records | Risk configuration |
|---|---|
| <img src="docs/screenshots/04-driver-records-prototype.jpg" alt="Prototype driver records" width="250"> | <img src="docs/screenshots/06-risk-configuration-prototype.jpg" alt="Prototype risk configuration" width="250"> |

## Data model

```mermaid
erDiagram
    TRIP ||--o{ INCIDENT : contains
    TRIP ||--o{ ROUTE_POINT : records

    TRIP {
        long id PK
        long startTime
        long endTime
        long durationSeconds
        double distanceKm
        int score
    }

    INCIDENT {
        long id PK
        long tripId FK
        string type
        string severity
        long timestamp
        double latitude
        double longitude
    }

    ROUTE_POINT {
        long id PK
        long tripId FK
        double latitude
        double longitude
        float speedMph
        long timestamp
    }
```

## Repository structure

```text
app/src/main/java/com/example/driveguard/
├── MainActivity.kt                 # Navigation and app entry point
├── TripScreen.kt                   # Live GPS and sensor monitoring
├── TripMonitoringService.kt        # Foreground-service notification
├── TripViewModel.kt                # Reactive trip state
├── DetectionRules.kt               # Pure detection thresholds
├── ScoringCalculator.kt            # Weighted safety score
├── HomeScreen.kt
├── TripSummaryScreen.kt
├── HistoryScreen.kt
├── SavedTripDetailScreen.kt
├── IncidentDetailScreen.kt
├── SettingsScreen.kt
├── data/
│   ├── DriveGuardDatabase.kt
│   ├── dao/TripDao.kt
│   └── entities/
└── ui/theme/
```

## Development workflow

```mermaid
flowchart LR
    A[Define behaviour] --> B[Implement UI or business rule]
    B --> C[Keep detection and scoring logic pure]
    C --> D[Add or update JUnit tests]
    D --> E[Run local unit tests]
    E --> F[Exercise trip flow on Android device]
    F --> G[Review stored trips and incidents]
    G --> H[Commit a focused change]
```

## Getting started

### Requirements

- Android Studio with a compatible JDK
- Android SDK 36
- Android 8.0 / API 26 or later
- A physical device for realistic GPS and motion-sensor testing

### Run the application

```bash
git clone https://github.com/Nawaaalae/Ai-dashcam-system.git
cd Ai-dashcam-system
./gradlew assembleDebug
```

Open the project in Android Studio, run the `app` configuration, and grant location and notification permissions when prompted. Do not interact with the application while driving.

## Testing

Local unit tests cover detection boundaries, location/movement validation, weighted scoring and score clamping:

```bash
./gradlew testDebugUnitTest
```

The tests are included in the repository for reproducibility. They were not re-run in the publishing environment because that machine did not have a Java runtime installed.

## Scope and limitations

- Detection is rule-based; the published build does not contain a trained AI or computer-vision model.
- Lane-departure and traffic-light recognition remain future concepts.
- Thresholds require wider calibration across devices, vehicles and road conditions.
- GPS and sensor behaviour should be tested across multiple physical Android devices.
- The project is not an insurance product, emergency system or substitute for attentive driving.

## Roadmap

- Move continuous sampling fully into the foreground service
- Add dependency injection and repository abstractions
- Add instrumented Room and Compose UI tests
- Add configurable thresholds with validation
- Add CI for build, lint and unit tests
- Evaluate TensorFlow Lite only after a suitable dataset and validation plan exist

## Author

**Muhammad Nawal Ahmed**

[LinkedIn](https://www.linkedin.com/in/muhammadnawalahmed) · [GitHub](https://github.com/Nawaaalae)

## License

Licensed under the [MIT License](LICENSE).
