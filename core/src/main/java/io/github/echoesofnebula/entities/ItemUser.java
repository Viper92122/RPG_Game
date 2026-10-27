package io.github.echoesofnebula.entities;

public interface ItemUser {
    void heal(int amount);
    void restoreMana(int amount);

    // FIXME: Shouldn't be a String, StatusEffect class?
    void applyStatusEffect(String status);
}
