package se233.arkanoid.model;

/** Checked exception for expected game-domain failures. */
public class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
