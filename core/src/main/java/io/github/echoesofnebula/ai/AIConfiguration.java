package io.github.echoesofnebula.ai;


/**
 * Configuration for an entity's AI behavior.
 * Passed to the entity so it can build its own AIController.
 */
public class AIConfiguration {
    public enum AIType {
        WANDER_ONLY,           // Just wanders, no combat
        WANDER_AND_CHASE,      // Wanders, chases player when spotted
        AGGRESSIVE,            // Always chasing/attacking
        PATROL,                // Follows waypoints
        BOSS                   // Complex multi-phase behavior
    }

    public final AIType aiType;
    public final float visionRange;
    public final float attackRange;
    public final float fleeHealthThreshold;  // Flee when HP below this %

    public AIConfiguration(AIType aiType, float visionRange, float attackRange, float fleeHealthThreshold) {
        this.aiType = aiType;
        this.visionRange = visionRange;
        this.attackRange = attackRange;
        this.fleeHealthThreshold = fleeHealthThreshold;
    }

    // Convenience factory methods for common configs
    public static AIConfiguration wanderer() {
        return new AIConfiguration(AIType.WANDER_ONLY, 0f, 0f, 0f);
    }

    public static AIConfiguration standardEnemy(float visionRange, float attackRange) {
        return new AIConfiguration(AIType.WANDER_AND_CHASE, visionRange, attackRange, 0.2f);
    }

    public static AIConfiguration boss(float visionRange, float attackRange) {
        return new AIConfiguration(AIType.BOSS, visionRange, attackRange, 0.1f);
    }
}
