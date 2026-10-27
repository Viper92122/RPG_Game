package io.github.echoesofnebula.ai;

import io.github.echoesofnebula.entities.Enemy;
import io.github.echoesofnebula.entities.Player;

public class EnemyBrain implements AIBehavior {
    private EnemyState currentState;
    private final Player playerTarget;

    public EnemyBrain(Player playerTarget) {
        this.playerTarget = playerTarget;
        this.currentState = new RoamState();
    }

    public void changeState(EnemyState newState) {
        this.currentState = newState;
    }

    public Player getPlayerTarget() {
        return playerTarget;
    }

    @Override
    public void update(Enemy enemy, float deltaTime) {
        currentState.update(enemy, this, deltaTime);
    }

    @Override
    public void handleWallImpact(Enemy enemy) {
        currentState.handleWallImpact(enemy);
    }
}
