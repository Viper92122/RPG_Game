package io.github.echoesofnebula.ai.states;

import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.ai.BrainOwner;
import io.github.echoesofnebula.entities.Entity;

public class ChaseState extends AIState {
    @Override
    public void update(BrainOwner owner, float deltaTime) {
        Entity target = owner.getTarget();
        if (target == null) return;  // No one to chase

        Vector2 direction = target.getPosition().cpy()
            .sub(owner.getEntity().getPosition()).nor();
        owner.getEntity().getVelocity().set(direction)
            .scl(owner.getStats().getWalkingSpeed());
    }

    @Override
    public void handleWallImpact(BrainOwner owner) {

    }

    @Override
    public AIState checkTransitions(BrainOwner owner) {
        // Lost the target? Go back to wandering
        if (owner.getTarget() == null) return new WanderState();

        // Target out of range? Go back to wandering
        float distance = owner.getEntity().getPosition()
            .dst(owner.getTarget().getPosition());
        if (distance > 10f) return new WanderState();

        // Close enough to attack?
        // if (distance < 1.5f) return new AttackState();
        return this;
    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }
}
