package se233.arkanoid.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameExceptionTest {
    @Test
    void messageConstructorPreservesItsMessage() {
        GameException exception = new GameException("Invalid stage.");
        assertEquals("Invalid stage.", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void causeConstructorPreservesMessageAndCause() {
        RuntimeException cause = new RuntimeException("bad layout");
        GameException exception = new GameException("Could not load stage.", cause);
        assertEquals("Could not load stage.", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
