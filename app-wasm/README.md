## Usage

### Generate Dependency Information

```bash
./gradlew :app-wasm:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/composeResources/files/
```

### Run Wasm app

```bash
./gradlew :app-wasm:wasmJsRun
```