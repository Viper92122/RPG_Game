package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;

public class BuffEffect implements ItemEffect {

    private float intensity = 5f;
    @Override
    public void apply(ItemUser target) {

    }

    @Override
    public EffectType getEffectType() {
        return EffectType.BUFF;
    }

    @Override
    public float getIntensity() {
        return 0;
    }
}
