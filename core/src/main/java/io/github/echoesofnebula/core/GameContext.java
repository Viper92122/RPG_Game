package io.github.echoesofnebula.core;

import io.github.echoesofnebula.entities.Player;
import io.github.echoesofnebula.managers.DialogRegistry;
import io.github.echoesofnebula.managers.SpellRegistry;

/**
 * The Single Source of Truth for the active game session.
 * Pass this to systems (AI, Camera, UI) instead of passing 5 different objects.
 */
public class GameContext {
    private Player currentPlayer;
    private GameWorld currentWorld;

    // Global session registries (No more MasterDataManager!)
    private final DialogRegistry dialogRegistry;
    private final SpellRegistry spellRegistry;

    public GameContext(DialogRegistry dialogRegistry, SpellRegistry spellRegistry) {
        this.dialogRegistry = dialogRegistry;
        this.spellRegistry = spellRegistry;
    }

    // --- Core State ---
    public Player getPlayer() { return currentPlayer; }
    public void setPlayer(Player player) { this.currentPlayer = player; }

    public GameWorld getWorld() { return currentWorld; }
    public void setWorld(GameWorld world) { this.currentWorld = world; }

    // --- Session Registries ---
    public DialogRegistry getDialogs() { return dialogRegistry; }
    public SpellRegistry getSpells() { return spellRegistry; }
}
