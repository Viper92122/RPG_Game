package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.core.BaseRegistry;
import io.github.echoesofnebula.status.StatusEffectBlueprint;

public class StatusEffectRegistry extends BaseRegistry<StatusEffectBlueprint> {
    public StatusEffectRegistry() {
        super("status/status_effects.json", StatusEffectBlueprint.class);
    }
}
