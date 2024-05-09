## Usage

### Generate Dependency Information

```bash
./gradlew :app-wasm:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/composeResources/files/
```

### Run Wasm app

```
./gradlew :app-wasm:wasmJsRun   
```