package io.github.echoesofnebula.status;

/**
 * All status effect types in the game.
 * Type-safe, auto-complete, no string typos.
 * The actual configuration (damage, duration) lives in JSON.
 */
public enum StatusEffectType {
    BURN,
    POISON,
    BLEED,
    SLOW,
    WEAKNESS,
    FROZEN,
    STRENGTH_BUFF,
    DEFENSE_BUFF,

    REGENERATION,
    INVULNERABILITY,

    NONE;
}
