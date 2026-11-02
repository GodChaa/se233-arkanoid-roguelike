package se233.arkanoid.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Ball;

import java.util.List;

final class BallRenderer {
    private final Canvas canvas;
    private boolean piercingBallActive;

    BallRenderer(Canvas canvas) {
        this.canvas = canvas;
    }

    void draw(GraphicsContext graphics, Ball ball) {
        if (ball == null) return;
        graphics.setFill(piercingBallActive ? Color.CYAN : Color.GOLD);
        graphics.fillOval(ball.getX() - GameView.BALL_RADIUS,
                ball.getY() - GameView.BALL_RADIUS,
                GameView.BALL_RADIUS * 2, GameView.BALL_RADIUS * 2);
        if (piercingBallActive) {
            graphics.setStroke(Color.WHITE);
            graphics.setLineWidth(2);
            graphics.strokeOval(ball.getX() - GameView.BALL_RADIUS - 3,
                    ball.getY() - GameView.BALL_RADIUS - 3,
                    GameView.BALL_RADIUS * 2 + 6,
                    GameView.BALL_RADIUS * 2 + 6);
        }
    }

    void renderExtraBalls(List<Ball> balls) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(piercingBallActive ? Color.CYAN : Color.LIGHTSKYBLUE);
        for (Ball ball : balls) {
            graphics.fillOval(ball.getX() - GameView.BALL_RADIUS,
                    ball.getY() - GameView.BALL_RADIUS,
                    GameView.BALL_RADIUS * 2, GameView.BALL_RADIUS * 2);
        }
    }

    void setPiercingBallActive(boolean active) {
        piercingBallActive = active;
    }
}
