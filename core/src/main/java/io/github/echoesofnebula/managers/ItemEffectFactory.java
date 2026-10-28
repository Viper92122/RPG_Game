package io.github.echoesofnebula.managers;

import com.badlogic.gdx.utils.ObjectMap;
import io.github.echoesofnebula.items.ItemEffect;
import io.github.echoesofnebula.items.effects.*;

public class ItemEffectFactory {
    private final ObjectMap<String, Class<? extends ItemEffect>> effectClasses = new ObjectMap<>();

    public ItemEffectFactory() {
        // Register effect types (not instances - we'll create them with specific intensity)
        effectClasses.put("HEAL", HealEffect.class);
        effectClasses.put("MANA", ManaEffect.class);
        effectClasses.put("BUFF", BuffEffect.class);
        effectClasses.put("NONE", NoOpEffect.class);
    }

    /**
     * Creates a fully-configured effect with the specified intensity.
     * This is the proper Factory pattern - returns a complete, ready-to-use object.
     */
    public ItemEffect createEffect(String effectType, float intensity) {
        if (effectType == null || effectType.isEmpty()) {
            return new NoOpEffect();
        }

        Class<? extends ItemEffect> effectClass = effectClasses.get(effectType.toUpperCase().trim());
        if (effectClass == null) {
            System.err.println("⚠️ Unknown effect type: '" + effectType + "'. Using NoOp.");
            return new NoOpEffect();
        }

        try {
            // Create a new instance with the specific intensity
            return effectClass.getConstructor(float.class).newInstance(intensity);
        } catch (Exception e) {
            System.err.println("❌ Failed to create effect: " + effectType);
            return new NoOpEffect();
        }
    }

    public boolean hasEffect(String effectType) {
        return effectClasses.containsKey(effectType.toUpperCase().trim());
    }
}
