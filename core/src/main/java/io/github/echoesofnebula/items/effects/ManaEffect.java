package io.github.echoesofnebula.items.effects;

import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.items.ItemEffect;

public class ManaEffect implements ItemEffect {
    private float intensity;
    @Override
    public void apply(ItemUser target) {
        target.restoreMana((int) intensity);
        System.out.println("🔮 Restored " + (int) intensity + " mana");
    }

    @Override
    public String getEffectType() {
        return "MANA";
    }

    @Override
    public float getIntensity() {
        return intensity;
    }
}
