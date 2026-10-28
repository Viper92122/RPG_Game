package io.github.echoesofnebula.items;

import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.items.effects.EffectType;

public interface ItemEffect {
    void apply(ItemUser target);
    EffectType getEffectType();
    float getIntensity();
}
