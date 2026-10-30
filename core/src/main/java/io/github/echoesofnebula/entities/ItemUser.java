package io.github.echoesofnebula.entities;

import io.github.echoesofnebula.items.effects.BuffEffect;
import io.github.echoesofnebula.status.StatusEffect;

public interface ItemUser {
    void heal(int amount);
    void restoreMana(int amount);
    void takeDamage(int amount);

    void applyStatusEffect(StatusEffect effect);

    void clearStatusEffects();

}
