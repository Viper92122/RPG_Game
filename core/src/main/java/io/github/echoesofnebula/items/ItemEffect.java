package io.github.echoesofnebula.items;

import io.github.echoesofnebula.entities.ItemUser;

public interface ItemEffect {
    void apply(ItemUser target);
    String getEffectType();
    float getIntensity();
}
