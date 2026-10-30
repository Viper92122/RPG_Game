package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;

public class HealEffect implements ItemEffect {
    private float intensity = 10f;


    @Override
    public void apply(ItemUser target) {
        target.heal((int) intensity);
        System.out.println("✨ Healed for " + (int) intensity + " HP");
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.HEAL;
    }

    @Override
    public float getIntensity() {
        return intensity;
    }
}
