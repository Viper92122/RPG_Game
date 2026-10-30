package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;

public class ManaEffect implements ItemEffect {
    private float intensity;
    @Override
    public void apply(ItemUser target) {
        target.restoreMana((int) intensity);
        System.out.println("🔮 Restored " + (int) intensity + " mana");
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.MANA;
    }

    @Override
    public float getIntensity() {
        return intensity;
    }
}
