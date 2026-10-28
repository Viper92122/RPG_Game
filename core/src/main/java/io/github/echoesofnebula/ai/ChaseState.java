package io.github.echoesofnebula.ai;

import io.github.echoesofnebula.entities.Enemy;

public class ChaseState implements EnemyState {
    private final float speed = 0.5f;

    @Override
    public void update(Enemy enemy, EnemyBrain brain, float deltaTime) {
        if (enemy.getPosition().dst(brain.getPlayerTarget().getPosition()) > enemy.getStats().getRange()) {
            brain.changeState(new RoamState());
            return;
        }

        enemy.getVelocity().set(brain.getPlayerTarget().getPosition())
            .sub(enemy.getPosition()).nor().scl(speed);
    }

    @Override
    public void handleWallImpact(Enemy enemy) {
    }
}
