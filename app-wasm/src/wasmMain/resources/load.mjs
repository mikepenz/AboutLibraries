import { instantiate } from './imageviewer.uninstantiated.mjs';

await wasmSetup;

let te = null;
try {
    await instantiate({ skia: Module['asm'] });
} catch (e) {
    te = e;
}

if (te == null) {
    document.getElementById("warning").style.display="none";
} else {
    throw te;
}