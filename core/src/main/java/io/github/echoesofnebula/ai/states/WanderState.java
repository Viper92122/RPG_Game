package io.github.echoesofnebula.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.ai.BrainOwner;

public class WanderState extends AIState {
    @Override
    public void update(BrainOwner owner, float deltaTime) {
        // If we don't have a destination, pick a random one nearby
        if (!owner.hasDestination()) {
            Vector2 current = owner.getEntity().getPosition();
            Vector2 randomOffset = new Vector2(
                MathUtils.random(-3f, 3f),
                MathUtils.random(-3f, 3f)
            );
            owner.setDestination(current.cpy().add(randomOffset));
        }

        // Move toward destination
        Vector2 dest = owner.getDestination();
        Vector2 pos = owner.getEntity().getPosition();
        Vector2 direction = dest.cpy().sub(pos).nor();

        float distance = pos.dst(dest);
        if (distance < 0.1f) {
            // Arrived — clear destination so we pick a new one next frame
            owner.clearDestination();
            owner.getEntity().getVelocity().setZero();
        } else {
            owner.getEntity().getVelocity().set(direction).scl(owner.getStats().getWalkingSpeed());
        }
    }

    @Override
    public void handleWallImpact(BrainOwner owner) {

    }

    @Override
    public void enter() {

    }

    @Override
    public void exit() {

    }

    @Override
    public AIState checkTransitions(BrainOwner owner) {
        return null;
    }
}
