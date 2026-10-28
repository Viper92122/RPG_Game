package io.github.echoesofnebula.items.effects;

import com.badlogic.gdx.utils.Array;
import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.items.ItemEffect;
import io.github.echoesofnebula.managers.StatusEffectFactory;
import io.github.echoesofnebula.status.StatusEffect;
import io.github.echoesofnebula.status.effects.StatusEffectType;


public class ApplyStatusEffect implements ItemEffect {
    private final float intensity;
    private final Array<StatusEffectType> statusTypes;  // ✅ Enum array
    private final StatusEffectFactory statusEffectFactory;

    public ApplyStatusEffect(float intensity, Array<StatusEffectType> statusTypes,
                             StatusEffectFactory statusEffectFactory) {
        this.intensity = intensity;
        this.statusTypes = statusTypes;
        this.statusEffectFactory = statusEffectFactory;
    }

    @Override
    public void apply(ItemUser target) {
        for (StatusEffectType type : statusTypes) {
            StatusEffect effect = statusEffectFactory.createEffect(type);
            if (effect != null) {
                target.applyStatusEffect(effect);
            }
        }
    }

    @Override
    public EffectType getEffectType() {
        return null;
    }


    @Override
    public float getIntensity() { return intensity; }
}
