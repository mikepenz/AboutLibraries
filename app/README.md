# Usage

## Generate Dependency Information

```bash
./gradlew :app:exportLibraryDefinitions
```

## Android

### Generate Dependency Information

```bash
./gradlew :app:exportLibraryDefinitionsDebug
```

## Desktop

### Generate Dependency Information

```bash
./gradlew :app:exportLibraryDefinitionsDesktop
```

### Run desktop app

```bash
./gradlew :app:run
```

## Wasm app

### Generate Dependency Information

```bash
./gradlew :app:exportLibraryDefinitionsWasmJs
```

### Run wasm app

```bash
./gradlew :app:wasmJsBrowserDevelopmentRun
```

## iOS app

### Generate Dependency Information

```bash
./gradlew :app:exportLibraryDefinitions -PaboutLibraries.exportPath=src/iosMain/composeResources/files/ -PaboutLibraries.exportVariant=metadata
```