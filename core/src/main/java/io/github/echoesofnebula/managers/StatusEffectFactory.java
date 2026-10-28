package io.github.echoesofnebula.managers;

import io.github.echoesofnebula.status.StatusEffect;
import io.github.echoesofnebula.status.StatusEffectBlueprint;
import io.github.echoesofnebula.status.effects.StatusEffectType;
import io.github.echoesofnebula.status.effects.*;

public class StatusEffectFactory {
    private final StatusEffectRegistry registry;

    public StatusEffectFactory(StatusEffectRegistry registry) {
        this.registry = registry;
    }

    /**
     * Creates a status effect by enum type.
     * Type-safe, no string typos possible.
     */
    public StatusEffect createEffect(StatusEffectType type) {
        // Find the blueprint for this type
        StatusEffectBlueprint blueprint = findBlueprintByType(type);
        if (blueprint == null) {
            System.err.println("❌ No blueprint found for status effect: " + type);
            return null;
        }

        return createFromBlueprint(blueprint);
    }

    /**
     * Creates a status effect by string key (for JSON/external data).
     * Converts string to enum internally.
     */
    public StatusEffect createEffect(String effectName) {
        StatusEffectBlueprint blueprint = registry.get(effectName);
        if (blueprint == null) {
            System.err.println("❌ Unknown status effect: " + effectName);
            return null;
        }

        return createFromBlueprint(blueprint);
    }

    private StatusEffect createFromBlueprint(StatusEffectBlueprint blueprint) {
        if (blueprint.damagePerSecond > 0) {
            return new DamageOverTimeEffect(blueprint);
        } else if (hasStatModifiers(blueprint)) {
            return new StatModifierEffect(blueprint);
        } else {
            System.err.println("⚠️ Status effect has no behavior: " + blueprint.effectType);
            return null;
        }
    }

    private StatusEffectBlueprint findBlueprintByType(StatusEffectType type) {

        for (StatusEffectBlueprint bp : registry.getAll()) {
            if (bp.effectType == type) {
                return bp;
            }
        }
        return null;
    }

    private boolean hasStatModifiers(StatusEffectBlueprint blueprint) {
        return blueprint.speedModifier != 0 ||
            blueprint.defenseModifier != 0 ||
            blueprint.strengthModifier != 0;
    }
}
