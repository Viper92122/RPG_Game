package io.github.echoesofnebula.ai;

import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.entities.Entity;

public class ChasePlayerBehavior implements AIBehavior {
    @Override
    public void update(BrainOwner owner, float deltaTime) {
        Entity self = owner.getEntity();
        Entity target = owner.getTarget();

        if (target == null) return;

        // Now this works for ANY BrainOwner, not just Enemy!
        Vector2 direction = target.getPosition().sub(self.getPosition()).nor();
        self.getVelocity().set(direction).scl(owner.getStats().getWalkingSpeed());
    }

    @Override
    public void handleWallImpact(BrainOwner owner) {
        owner.getEntity().getVelocity().scl(-1);
    }
}
