package io.github.echoesofnebula.ai;

import io.github.echoesofnebula.entities.Enemy;
public interface AIBehavior {
    void update(Enemy enemy, float deltaTime);
    void handleWallImpact(Enemy enemy);
}

