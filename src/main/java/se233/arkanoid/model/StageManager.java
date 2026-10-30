package se233.arkanoid.model;

import java.util.List;
import java.util.Objects;

public class StageManager {
    private final List<Stage> stages;
    private int currentStageIndex;

    public StageManager(List<Stage> stages) {
        Objects.requireNonNull(stages, "Stages cannot be null.");
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("At least one stage is required.");
        }
        if (stages.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Stages cannot contain null entries.");
        }
        this.stages = List.copyOf(stages);
    }

    public Stage getCurrentStage() {
        return stages.get(currentStageIndex);
    }

    public boolean advanceToNextStage() {
        if (currentStageIndex + 1 >= stages.size()) {
            return false;
        }
        currentStageIndex++;
        return true;
    }

    public boolean hasNextStage() {
        return currentStageIndex + 1 < stages.size();
    }

    public int getCurrentStageNumber() {
        return currentStageIndex + 1;
    }

    public int getStageCount() {
        return stages.size();
    }
}
