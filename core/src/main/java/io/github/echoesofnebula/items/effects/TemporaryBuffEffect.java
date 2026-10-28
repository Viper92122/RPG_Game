package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.items.ItemEffect;

public class TemporaryBuffEffect implements ItemEffect {
    private float duration;

    private float intensity;

    @Override
    public void apply(ItemUser target) {
        // target.applyStatusEffect("BUFF", intensity);
    }

    @Override
    public String getEffectType() {
        return "";
    }

    @Override
    public float getIntensity() {
        return intensity;
    }
}
