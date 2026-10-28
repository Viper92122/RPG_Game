package io.github.echoesofnebula.managers;

import io.github.echoesofnebula.status.StatusEffectBlueprint;

public class StatusEffectRegistry extends BaseRegistry<StatusEffectBlueprint> {
    public StatusEffectRegistry() {
        super("status/status_effects.json", StatusEffectBlueprint.class);
    }
}
