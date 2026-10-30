package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;

public class TemporaryBuffEffect implements ItemEffect {
    private float duration;

    private float intensity;

    @Override
    public void apply(ItemUser target) {
        // target.applyStatusEffect("BUFF", intensity);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.BUFF;
    }

    @Override
    public float getIntensity() {
        return intensity;
    }
}
