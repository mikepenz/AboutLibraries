## Usage

### Generate Dependency Information

```bash
./gradlew app-wasm:exportLibraryDefinitions -PaboutLibraries.exportPath=src/wasmJsMain/resources/
```

### Run Wasm app

```
./gradlew :app-wasm:wasmJsRun   
```