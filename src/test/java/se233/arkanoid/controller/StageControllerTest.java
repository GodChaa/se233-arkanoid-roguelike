package se233.arkanoid.controller;

import org.junit.jupiter.api.Test;
import se233.arkanoid.model.BrickType;

import static org.junit.jupiter.api.Assertions.*;

class StageControllerTest {
    @Test
    void startNewRunCreatesFreshFirstStageAndCanResetProgress() throws Exception {
        StageController controller = new StageController();
        controller.startNewRun();
        controller.advanceToNextStage();
        controller.startNewRun();
        assertEquals(1, controller.getCurrentStageNumber());
        assertEquals(26, controller.getCurrentStage().getBricks().size());
    }

    @Test
    void startNewRunStartsAtStageOneWithNormalBricks() throws Exception {
        StageController controller = new StageController();
        controller.startNewRun();
        assertEquals(1, controller.getCurrentStageNumber());
        assertTrue(controller.getCurrentStage().getBricks().stream()
                .anyMatch(brick -> brick.getType() == BrickType.NORMAL));
    }

    @Test
    void getCurrentStageReturnsNullBeforeRunAndStageAfterStart() throws Exception {
        StageController controller = new StageController();
        assertNull(controller.getCurrentStage());
        controller.startNewRun();
        assertNotNull(controller.getCurrentStage());
    }

    @Test
    void getCurrentStageNumberDefaultsToOneAndTracksProgress() throws Exception {
        StageController controller = new StageController();
        assertEquals(1, controller.getCurrentStageNumber());
        controller.startNewRun();
        controller.advanceToNextStage();
        assertEquals(2, controller.getCurrentStageNumber());
    }

    @Test
    void hasNextStageIsTrueForInitialStagesAndFalseAtBossStage() throws Exception {
        StageController controller = new StageController();
        controller.startNewRun();
        assertTrue(controller.hasNextStage());
        controller.advanceToStage(5);
        assertFalse(controller.hasNextStage());
    }

    @Test
    void advanceToNextStageMovesForwardAndStopsAtCurrentEnd() throws Exception {
        StageController controller = new StageController();
        controller.startNewRun();
        assertTrue(controller.advanceToNextStage());
        assertEquals(2, controller.getCurrentStageNumber());
        controller.advanceToStage(5);
        assertFalse(controller.advanceToNextStage());
    }

    @Test
    void advanceToStageMovesToRequestedStageAndAddsFutureStages() throws Exception {
        StageController controller = new StageController();
        controller.startNewRun();
        assertTrue(controller.advanceToStage(5));
        assertEquals(5, controller.getCurrentStageNumber());
        assertTrue(controller.advanceToStage(6));
        assertEquals(6, controller.getCurrentStageNumber());
    }

    @Test
    void advanceToStageRejectsEarlierOrInvalidStageNumbers() throws Exception {
        StageController controller = new StageController();
        assertFalse(controller.advanceToStage(1));
        controller.startNewRun();
        assertFalse(controller.advanceToStage(0));
        assertEquals(1, controller.getCurrentStageNumber());
    }

    @Test
    void appendNextStageAppendsOnlyWhenNoNextStageExists() throws Exception {
        StageController controller = new StageController();
        assertThrows(IllegalStateException.class, controller::appendNextStage);
        controller.startNewRun();
        controller.advanceToStage(5);
        controller.appendNextStage();
        assertTrue(controller.hasNextStage());
        controller.advanceToNextStage();
        assertEquals(40, controller.getCurrentStage().getBricks().size());
    }

    @Test
    void appendNextStageDoesNotReplaceAnExistingStage() throws Exception {
        StageController controller = new StageController();
        controller.startNewRun();
        controller.appendNextStage();
        assertTrue(controller.advanceToNextStage());
        assertEquals(40, controller.getCurrentStage().getBricks().size());
    }

    @Test
    void isCurrentStageBossRecognizesStagesFiveAndTen() throws Exception {
        StageController controller = new StageController();
        controller.startNewRun();
        assertFalse(controller.isCurrentStageBoss());
        controller.advanceToStage(5);
        assertTrue(controller.isCurrentStageBoss());
        controller.advanceToStage(10);
        assertTrue(controller.isCurrentStageBoss());
    }
}
