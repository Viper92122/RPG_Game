package io.github.echoesofnebula.status.effects;

import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.status.StatusEffect;
import io.github.echoesofnebula.status.StatusEffectBlueprint;

public class StatModifierEffect implements StatusEffect {
    protected final StatusEffectBlueprint blueprint;
    protected float remainingDuration;

    public StatModifierEffect(StatusEffectBlueprint blueprint) {
        this.blueprint = blueprint;
        this.remainingDuration = blueprint.duration;
    }

    @Override
    public void update(ItemUser target, float deltaTime) {
        if (isExpired()) return;
        remainingDuration -= deltaTime;

        // Apply stat modifiers
        // TODO: Integrate with your Stats system
        // target.getStats().addModifier(blueprint.speedModifier, blueprint.defenseModifier, blueprint.strengthModifier);
    }

    @Override
    public void onApply(ItemUser target) {
        System.out.println("✨ Applied buff: " + blueprint.displayName);
    }

    @Override
    public void onRemove(ItemUser target) {
        System.out.println("💨 Buff expired: " + blueprint.displayName);
        // TODO: Remove stat modifiers
        // target.getStats().removeModifier(blueprint.speedModifier, blueprint.defenseModifier, blueprint.strengthModifier);
    }

    @Override
    public boolean isExpired() {
        return remainingDuration <= 0;
    }

    @Override
    public float getRemainingDuration() {
        return remainingDuration;
    }

    @Override
    public StatusEffectBlueprint getBlueprint() {
        return blueprint;
    }

    @Override
    public void refresh() {
        if (blueprint.refreshDuration) {
            remainingDuration = blueprint.duration;
        }
    }
}
