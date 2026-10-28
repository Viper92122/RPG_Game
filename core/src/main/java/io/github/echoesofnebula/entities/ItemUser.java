package io.github.echoesofnebula.entities;

import io.github.echoesofnebula.items.ItemEffect;

public interface ItemUser {
    void heal(int amount);
    void restoreMana(int amount);

    public void applyConsumableEffect(ItemEffect effect);
}
