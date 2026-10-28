package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.items.ItemEffect;

public class BuffEffect implements ItemEffect {

    private float intensity = 5f;
    @Override
    public void apply(ItemUser target) {
        target.applyConsumableEffect(this);
    }

    @Override
    public String getEffectType() {
        return "BUFF";
    }

    @Override
    public float getIntensity() {
        return 0;
    }
}
