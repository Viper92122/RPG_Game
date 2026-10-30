package io.github.echoesofnebula.status;

public class StatusEffectBlueprint {
    public String displayName;
    public StatusEffectType effectType;  // ✅ Changed from String to enum
    public float damagePerSecond;
    public float duration;
    public String particleEffect;
    public boolean stackable;
    public boolean refreshDuration;

    // Stat modifiers
    public float speedModifier;
    public float defenseModifier;
    public float strengthModifier;

    public StatusEffectBlueprint() {}
}
