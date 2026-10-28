package io.github.echoesofnebula.status;

import io.github.echoesofnebula.entities.ItemUser;

/**
 * Base interface for all status effects.
 * Status effects are applied over time and have duration.
 */
public interface StatusEffect {
    /**
     * Called every frame while the effect is active.
     * @param target The entity affected
     * @param deltaTime Time since last frame
     */
    void update(ItemUser target, float deltaTime);

    /**
     * Called when the effect is first applied.
     */
    void onApply(ItemUser target);

    /**
     * Called when the effect expires or is removed.
     */
    void onRemove(ItemUser target);

    /**
     * Check if the effect has expired.
     */
    boolean isExpired();

    /**
     * Get remaining duration.
     */
    float getRemainingDuration();

    /**
     * Get the blueprint (for UI display, etc.)
     */
    StatusEffectBlueprint getBlueprint();

    /**
     * Refresh the duration (if stackable/refreshable).
     */
    void refresh();
}
