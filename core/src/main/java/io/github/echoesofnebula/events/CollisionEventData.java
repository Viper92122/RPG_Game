package io.github.echoesofnebula.events;

import io.github.echoesofnebula.entities.Entity;

public class CollisionEventData {
    public final Entity initiator;
    public final Entity target;
    public final float safeX;
    public final float safeY;
    public final boolean isXAxis;

    public CollisionEventData(Entity initiator, Entity target, float safeX, float safeY, boolean isXAxis) {
        this.initiator = initiator;
        this.target = target;
        this.safeX = safeX;
        this.safeY = safeY;
        this.isXAxis = isXAxis;
    }
}
