import { instantiate } from './aboutlibraries.uninstantiated.mjs';

await wasmSetup;

instantiate({ skia: Module['asm'] });