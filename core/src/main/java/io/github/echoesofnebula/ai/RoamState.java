package io.github.echoesofnebula.ai;

import com.badlogic.gdx.math.MathUtils;
import io.github.echoesofnebula.entities.Enemy;

public class RoamState implements EnemyState {
    private float roamTimer = 0f;
    private final float roamInterval = 2f;
    private final float roamSpeed = 20f;

    @Override
    public void update(Enemy enemy, EnemyBrain brain, float deltaTime) {
        if (enemy.getPosition().dst(brain.getPlayerTarget().getPosition()) <= enemy.getStats().getRange()) {
            brain.changeState(new ChaseState());
            return;
        }

        roamTimer += deltaTime;
        if (roamTimer >= roamInterval || enemy.getVelocity().isZero()) {
            changeRandomDirection(enemy);
        }
    }

    @Override
    public void handleWallImpact(Enemy enemy) {
        changeRandomDirection(enemy);
    }

    private void changeRandomDirection(Enemy enemy) {
        roamTimer = 0f;
        float angle = MathUtils.random(0f, 360f);
        enemy.getVelocity().set(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).nor().scl(roamSpeed);
    }
}
