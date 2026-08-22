# DriveGuard

DriveGuard is an Android driving-behaviour monitoring application built with Kotlin and Jetpack Compose. It records trip data locally, applies rule-based detection to driving events, calculates a safety score, and lets users review completed journeys and incidents.

## Features

- Start and stop monitored trips
- Read GPS speed and route points using Android location APIs
- Detect speeding, harsh braking, sudden acceleration, and sharp turns using configurable rules
- Calculate a 0-100 safety score from recorded incidents
- Store trips, incidents, and route points locally with Room
- Review trip history, summaries, route details, and incident details
- Keep trip data on-device with no cloud synchronisation
- Continue trip monitoring through an Android foreground service notification

## Architecture

The application uses a layered Android architecture:

- **UI:** Jetpack Compose and Material 3 screens
- **State:** `TripViewModel` with `StateFlow`
- **Business logic:** `DetectionRules` and `ScoringCalculator`
- **Persistence:** Room entities and `TripDao`
- **Device integration:** Android GPS, accelerometer, gyroscope, and foreground-service APIs

Detection is currently rule-based. Computer-vision features such as lane-departure and traffic-light detection are possible future extensions and are not implemented in this version.

## Tech stack

- Kotlin
- Android SDK
- Jetpack Compose
- Material 3
- Room / SQLite
- Kotlin Coroutines and StateFlow
- JUnit 4
- Gradle with Kotlin DSL

## Project structure

```text
app/src/main/java/com/example/driveguard/
├── MainActivity.kt
├── TripScreen.kt
├── TripMonitoringService.kt
├── TripViewModel.kt
├── DetectionRules.kt
├── ScoringCalculator.kt
├── data/
└── ui/theme/
```

## Getting started

### Requirements

- Android Studio with a compatible JDK
- Android SDK 36
- An Android device or emulator running Android 8.0 (API 26) or later

### Run the app

1. Clone the repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronise dependencies.
4. Run the `app` configuration on a device or emulator.
5. Grant location and notification permissions when prompted.

For realistic trip monitoring, use a physical Android device with GPS and motion sensors. Do not interact with the application while driving.

## Tests

The repository includes local unit tests for detection boundaries and weighted safety-score calculations:

```bash
./gradlew testDebugUnitTest
```

Automated tests have not been re-run in the publishing environment because a Java runtime was unavailable. The test sources are included for reproducibility.

## Privacy and safety

Trip and incident data is stored locally in the app's Room database. The application requests location access and uses a foreground service while a trip is active. DriveGuard is an educational project and must not be treated as an insurance, emergency-response, or road-safety decision system.

## Author

Muhammad Nawal Ahmed - [LinkedIn](https://www.linkedin.com/in/muhammadnawalahmed)
