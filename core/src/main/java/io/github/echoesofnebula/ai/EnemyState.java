package io.github.echoesofnebula.ai;

import io.github.echoesofnebula.entities.Enemy;

public interface EnemyState {
    void update(Enemy enemy, EnemyBrain brain, float deltaTime);
    void handleWallImpact(Enemy enemy);
}
