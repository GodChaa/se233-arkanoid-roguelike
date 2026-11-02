package se233.arkanoid.view;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class FxTestSupport {
    private static boolean started;

    private FxTestSupport() { }

    public static void runAndWait(Runnable action) {
        ensureStarted();
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                finished.countDown();
            }
        });
        await(finished);
        if (failure.get() != null) {
            throw new AssertionError("JavaFX test action failed.", failure.get());
        }
    }

    private static synchronized void ensureStarted() {
        if (started) return;
        CountDownLatch ready = new CountDownLatch(1);
        try {
            Platform.startup(() -> {
                Platform.setImplicitExit(false);
                ready.countDown();
            });
        } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(ready::countDown);
        }
        await(ready);
        started = true;
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for the JavaFX application thread.");
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for the JavaFX application thread.", exception);
        }
    }
}
