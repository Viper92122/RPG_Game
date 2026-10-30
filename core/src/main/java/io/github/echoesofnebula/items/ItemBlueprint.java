package io.github.echoesofnebula.items;

import com.badlogic.gdx.utils.Array;

import io.github.echoesofnebula.status.StatusEffectType;

public class ItemBlueprint {
    public String displayName;
    public String atlasKey;
    public String effectType;
    public String statusKey;                          // single status (backward compatible)
    public Array<String> statusKeys;                  // multiple statuses (backward compatible)

    // ✅ NEW: Type-safe enum versions
    public StatusEffectType statusType;               // single status
    public Array<StatusEffectType> statusTypes;       // multiple statuses
    public float intensity;
    public float renderWidth;
    public float renderHeight;
    public int frameWidth;
    public int frameHeight;
    public float frameDuration;
    public boolean isAnimated;
    public boolean isConsumable;

    public ItemBlueprint() {}
}
