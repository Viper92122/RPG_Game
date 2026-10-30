package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;

public class NoOpEffect implements ItemEffect {
    @Override
    public void apply(ItemUser target) {

    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NONE;
    }

    @Override
    public float getIntensity() {
        return 0;
    }
}
