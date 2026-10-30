package io.github.echoesofnebula.items.effects;

import com.badlogic.gdx.utils.Array;

import io.github.echoesofnebula.entities.ItemUser;

public class CompositeEffect implements ItemEffect {
    private Array<ItemEffect> effects;

    private float intensity = 5f;
    public void apply(ItemUser target) {
        for (ItemEffect effect : effects) {
            effect.apply(target);
        }
    }



    @Override
    public EffectType getEffectType() {
        return EffectType.MULTIPLE;
    }

    @Override
    public float getIntensity() {
        return 0;
    }
}
