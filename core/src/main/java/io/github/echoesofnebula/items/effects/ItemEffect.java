package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;

public interface ItemEffect {
    void apply(ItemUser target);
    EffectType getEffectType();
    float getIntensity();
}
