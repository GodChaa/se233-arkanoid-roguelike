package se233.arkanoid.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import se233.arkanoid.controller.GameController;
import se233.arkanoid.controller.GameLoop;

public class GameStage extends Application {
    @Override
    public void start(Stage stage) {
        GameView view = new GameView();
        GameController controller = new GameController(view);
        GameLoop loop = new GameLoop(controller);

        Scene scene = new Scene(
                view.getRoot(),
                GameView.WIDTH,
                GameView.HEIGHT + 56
        );

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT
                    || event.getCode() == KeyCode.A) {
                controller.setLeftPressed(true);
            }

            if (event.getCode() == KeyCode.RIGHT
                    || event.getCode() == KeyCode.D) {
                controller.setRightPressed(true);
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.LEFT
                    || event.getCode() == KeyCode.A) {
                controller.setLeftPressed(false);
            }

            if (event.getCode() == KeyCode.RIGHT
                    || event.getCode() == KeyCode.D) {
                controller.setRightPressed(false);
            }
        });

        stage.setTitle("Arkanoid Roguelike");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        loop.start();
        stage.setOnCloseRequest(event -> loop.stop());
    }
}
