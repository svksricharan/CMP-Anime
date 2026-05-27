# Anime Movies KMP App

A production-oriented Compose Multiplatform app in Kotlin that fetches top anime movies from the Jikan API and runs on Android, iOS, and WebAssembly.

## Stack

- Kotlin Multiplatform
- Compose Multiplatform shared UI
- Ktor Client
- kotlinx.serialization
- Coroutines + StateFlow
- MVVM + Clean Architecture
- Repository pattern
- Manual dependency injection

## Project Layout

```text
androidApp/   Android launcher only
composeApp/   Shared code + iOS bridge + Wasm entrypoint
iosApp/       Xcode wrapper app that consumes the ComposeApp framework
```

Inside `composeApp/src/commonMain/kotlin/com/svksri/animemovies/`:

- `core/` shared result, errors, constants, dispatchers
- `network/` Ktor client, API service, DTOs
- `data/` repository implementation and mappers
- `domain/` models, repository contract, use case
- `validation/` movie validation rules
- `presentation/` shared UI state and ViewModel
- `ui/` shared Compose UI
- `di/` manual dependency container

## API

- Base URL: `https://api.jikan.moe`
- Endpoint: `/v4/top/anime?type=movie`

## Run

Android:

```bash
./gradlew :androidApp:assembleDebug
```

iOS simulator framework:

```bash
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

WebAssembly distribution:

```bash
./gradlew :composeApp:wasmJsBrowserDistribution
```

## Verified

- `./gradlew :androidApp:assembleDebug`
- `./gradlew :composeApp:compileKotlinIosSimulatorArm64`
- `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`
- `./gradlew :composeApp:compileTestKotlinWasmJs`
- `./gradlew :composeApp:wasmJsBrowserDistribution`
