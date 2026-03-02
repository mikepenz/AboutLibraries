const unhandledError = (event, error) => {
    if (error instanceof WebAssembly.CompileError) {
        document.getElementById("warning").style.display = "initial";

        // Hide a Scary Webpack Overlay which is less informative in this case.
        const webpackOverlay = document.getElementById("webpack-dev-server-client-overlay");
        if (webpackOverlay != null) {
            webpackOverlay.style.display = "none";
        }
    }
};

addEventListener("error", (event) => unhandledError(event, event.error));
addEventListener("unhandledrejection", (event) => unhandledError(event, event.reason));
