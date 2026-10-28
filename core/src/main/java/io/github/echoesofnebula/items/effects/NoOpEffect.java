package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.items.ItemEffect;

public class NoOpEffect implements ItemEffect {
    @Override
    public void apply(ItemUser target) {

    }

    @Override
    public String getEffectType() {
        return "NONE";
    }

    @Override
    public float getIntensity() {
        return 0;
    }
}
