package se233.arkanoid.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import se233.arkanoid.model.Boss;
import se233.arkanoid.model.BossProjectile;

import java.util.List;

final class BossRenderer {
    private final Image bossImage;
    private List<BossProjectile> projectiles = List.of();

    BossRenderer(Image bossImage) {
        this.bossImage = bossImage;
    }

    void setProjectiles(List<BossProjectile> projectiles) {
        this.projectiles = List.copyOf(projectiles);
    }

    void drawBoss(GraphicsContext graphics, Boss boss) {
        if (boss == null || boss.isDefeated()) return;

        if (GameCanvasRenderer.isUsable(bossImage)) {
            graphics.drawImage(bossImage, boss.getX(), boss.getY(),
                    boss.getWidth(), boss.getHeight());
        } else {
            graphics.setFill(Color.MEDIUMPURPLE);
            graphics.fillRoundRect(boss.getX(), boss.getY(),
                    boss.getWidth(), boss.getHeight(), 18, 18);
            graphics.setFill(Color.WHITE);
            graphics.fillText("BOSS", boss.getX() + boss.getWidth() / 2 - 16,
                    boss.getY() + boss.getHeight() / 2 + 5);
        }

        double segmentGap = 3;
        double segmentWidth = (boss.getWidth()
                - segmentGap * (boss.getMaxHealth() - 1)) / boss.getMaxHealth();
        for (int segment = 0; segment < boss.getMaxHealth(); segment++) {
            graphics.setFill(segment < boss.getHealth()
                    ? Color.LIGHTGREEN : Color.DARKSLATEGRAY);
            graphics.fillRoundRect(boss.getX() + segment * (segmentWidth + segmentGap),
                    boss.getY() - 14, segmentWidth, 8, 4, 4);
        }
    }

    void drawProjectiles(GraphicsContext graphics) {
        for (BossProjectile projectile : projectiles) {
            double diameter = BossProjectile.RADIUS * 2;
            graphics.setFill(Color.DARKRED);
            graphics.fillOval(projectile.getX() - BossProjectile.RADIUS,
                    projectile.getY() - BossProjectile.RADIUS, diameter, diameter);
            graphics.setFill(Color.ORANGERED);
            graphics.fillOval(projectile.getX() - BossProjectile.RADIUS + 2,
                    projectile.getY() - BossProjectile.RADIUS + 2,
                    diameter - 4, diameter - 4);
            graphics.setFill(Color.YELLOW);
            graphics.fillOval(projectile.getX() - 2, projectile.getY() - 3, 4, 4);
        }
    }
}
