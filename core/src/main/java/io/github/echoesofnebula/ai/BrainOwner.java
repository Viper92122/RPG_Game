package io.github.echoesofnebula.ai;

import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.entities.CharacterStats;

/**
 * Anything that can have AI behavior.
 * This is what AI actually needs to make decisions.
 */
public interface BrainOwner {
    Entity getEntity();          // For position/velocity
    CharacterStats getStats();            // For health, speed, etc.
    void setTarget(Entity target);
    Entity getTarget();

    Vector2 getDestination();
    void setDestination(Vector2 destination);
    void clearDestination();
    boolean hasDestination();

    float getVisionRange();
    float getAttackRange();
    float getFleeHealthThreshold();
}
