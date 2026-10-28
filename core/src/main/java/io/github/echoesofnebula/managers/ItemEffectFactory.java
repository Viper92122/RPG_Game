package io.github.echoesofnebula.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.github.echoesofnebula.items.ItemEffect;

public class ItemEffectFactory {
    private static final Map<String, Function<Float, ItemEffect>> REGISTRY = new HashMap<>();

    static {
        // ✨ Self-Aware Healing Effect Setup
        REGISTRY.put("HEAL", intensity -> target -> {
            System.out.println("✨ Strategy Executed: Healing target by " + intensity + " HP!");
            // target.heal(intensity);
        });

        // 🔮 Self-Aware Mana Effect Setup
        REGISTRY.put("MANA", intensity -> target -> {
            System.out.println("🔮 Strategy Executed: Restoring target mana by " + intensity);
            // target.restoreMana(intensity);
        });

        // 🛡️ Example expansion: Status Buff Setup
        REGISTRY.put("BUFF", intensity -> target -> {
            System.out.println("🛡️ Strategy Executed: Boosting defense attributes by " + intensity);
        });
    }

    /**
     * Resolves an ItemEffect closure based on properties set up in Tiled maps.
     * Safely falls back to a do-nothing effect if an unmapped action is requested.
     */
    public static ItemEffect createEffect(String actionType, float intensity) {
        if (actionType == null) return target -> {};

        Function<Float, ItemEffect> factory = REGISTRY.get(actionType.toUpperCase().trim());
        if (factory != null) {
            return factory.apply(intensity);
        }

        System.err.println("⚠️ Unknown action type: '" + actionType + "'. Item will execute a neutral fallback.");
        return target -> {};
    }
}
