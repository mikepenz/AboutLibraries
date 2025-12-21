import { instantiate } from './markdown.uninstantiated.mjs';

await wasmSetup;

instantiate({ skia: Module['asm'] });
