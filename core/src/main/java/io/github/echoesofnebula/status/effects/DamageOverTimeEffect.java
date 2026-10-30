package io.github.echoesofnebula.status.effects;

import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.status.StatusEffect;
import io.github.echoesofnebula.status.StatusEffectBlueprint;


public class DamageOverTimeEffect implements StatusEffect {
    protected final StatusEffectBlueprint blueprint;
    protected float remainingDuration;
    protected float damageAccumulator = 0f;

    public DamageOverTimeEffect(StatusEffectBlueprint blueprint) {
        this.blueprint = blueprint;
        this.remainingDuration = blueprint.duration;
    }

    @Override
    public void update(ItemUser target, float deltaTime) {
        if (isExpired()) return;

        remainingDuration -= deltaTime;

        if (blueprint.damagePerSecond > 0) {
            damageAccumulator += blueprint.damagePerSecond * deltaTime;

            if (damageAccumulator >= 0.5f) {
                int damage = (int) damageAccumulator;
                target.takeDamage(damage);
                damageAccumulator -= damage;
                System.out.println("💥 " + blueprint.displayName + " dealt " + damage + " damage");
            }
        }
    }

    @Override
    public void onApply(ItemUser target) {
        System.out.println("⚠️ Applied status: " + blueprint.displayName);
    }

    @Override
    public void onRemove(ItemUser target) {
        System.out.println("✅ Removed status: " + blueprint.displayName);
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
