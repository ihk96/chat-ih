import { startTransition, StrictMode } from "react";
import { hydrateRoot } from "react-dom/client";
import { HydratedRouter } from "react-router/dom";

async function enableMocking() {
    const enable = import.meta.env.VITE_ENABLE_MSW === "true";
    if (!enable) {
        return;
    }

    const { worker } = await import("./mocks/browser");
    return worker.start();
}

enableMocking().then(() => {
    startTransition(() => {
        hydrateRoot(
            document,
            // <StrictMode>
                <HydratedRouter/>
            // </StrictMode>,
        );
    });
})




