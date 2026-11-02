package se233.arkanoid.controller;

import se233.arkanoid.model.Brick;
import se233.arkanoid.model.BrickType;
import se233.arkanoid.model.GameException;
import se233.arkanoid.model.Stage;
import se233.arkanoid.model.StageManager;
import se233.arkanoid.view.GameView;

import java.util.ArrayList;
import java.util.List;

public final class StageController {
    private static final double FIELD_WIDTH = GameView.WIDTH;
    private StageManager stageManager;

    public void startNewRun() throws GameException {
        try {
            stageManager = new StageManager(createStages());
        } catch (IllegalArgumentException exception) {
            throw new GameException("The stage configuration is invalid.", exception);
        }
    }

    public Stage getCurrentStage() {
        return stageManager == null ? null : stageManager.getCurrentStage();
    }

    public int getCurrentStageNumber() {
        return stageManager == null ? 1 : stageManager.getCurrentStageNumber();
    }

    public boolean hasNextStage() {
        return stageManager != null && stageManager.hasNextStage();
    }

    public boolean advanceToNextStage() {
        return stageManager != null && stageManager.advanceToNextStage();
    }

    public boolean advanceToStage(int stageNumber) {
        if (stageManager == null || stageNumber < stageManager.getCurrentStageNumber()) {
            return false;
        }
        while (stageManager.getCurrentStageNumber() < stageNumber) {
            if (!stageManager.hasNextStage()) {
                appendNextStage();
            }
            if (!stageManager.advanceToNextStage()) {
                return false;
            }
        }
        return true;
    }

    public void appendNextStage() {
        if (stageManager == null) {
            throw new IllegalStateException("A run must be started before adding a stage.");
        }
        if (!stageManager.hasNextStage()) {
            stageManager.appendStage(
                    createStageForNumber(stageManager.getCurrentStageNumber() + 1)
            );
        }
    }

    public boolean isCurrentStageBoss() {
        return isBossStage(getCurrentStageNumber());
    }

    private static boolean isBossStage(int stageNumber) {
        return stageNumber % 5 == 0;
    }

    private static Stage createStageForNumber(int stageNumber) {
        if (isBossStage(stageNumber)) return new Stage(List.of());
        int layoutNumber = ((stageNumber - 1) % 4) + 1;
        return switch (layoutNumber) {
            case 1 -> createStage("...NN...", "..NNNN..", ".NNBBNN.", "NNBNNBNN", ".NNNNNN.");
            case 2 -> createStage(".SSSSSS.", "NBNNNNBN", ".SSNXNS.", "NBNNNNBN", ".SSNNSS.", ".SSSSSS.");
            case 3 -> createStage("...SS...", "..NBBN..", ".NSSSSN.", "NBSXSNBN",
                    ".NSSSSN.", "..NBBN..", "...SS...");
            default -> createStage(".SSSSSS.", "NBNNNNBN", "NSSNNSSN", ".NBNXBN.",
                    "NSSNNSSN", ".SSSSSS.");
        };
    }

    private static List<Stage> createStages() {
        List<Stage> result = new ArrayList<>();
        result.add(createStage(
                "...NN...",
                "..NNNN..",
                ".NNBBNN.",
                "NNBNNBNN",
                ".NNNNNN."));
        result.add(createStage(
                ".SSSSSS.",
                "NBNNNNBN",
                ".SSNXNS.",
                "NBNNNNBN",
                ".SSNNSS.",
                ".SSSSSS."));
        result.add(createStage(
                "...SS...",
                "..NBBN..",
                ".NSSSSN.",
                "NBSXSNBN",
                ".NSSSSN.",
                "..NBBN..",
                "...SS..."));
        result.add(createStage(
                ".SSSSSS.",
                "NBNNNNBN",
                "NSSNNSSN",
                ".NBNXBN.",
                "NSSNNSSN",
                ".SSSSSS."));
        result.add(new Stage(List.of()));
        return result;
    }

    private static Stage createStage(String... layout) {
        List<Brick> bricks = new ArrayList<>();
        int columns = 8;
        double brickWidth = 70;
        double gap = 6;
        double left = (FIELD_WIDTH - columns * (brickWidth + gap) + gap) / 2;
        for (int row = 0; row < layout.length; row++) {
            for (int column = 0; column < columns; column++) {
                BrickType type = switch (layout[row].charAt(column)) {
                    case 'N' -> BrickType.NORMAL;
                    case 'S' -> BrickType.STRONG;
                    case 'B' -> BrickType.BONUS;
                    case 'X' -> BrickType.SPECIAL;
                    case '.' -> null;
                    default -> throw new IllegalArgumentException("Unknown brick in stage layout.");
                };
                if (type != null) {
                    bricks.add(new Brick(left + column * (brickWidth + gap), 65 + row * 32,
                            brickWidth, 28, type));
                }
            }
        }
        return new Stage(bricks);
    }
}
