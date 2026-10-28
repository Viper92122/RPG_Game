package io.github.echoesofnebula.core;

import io.github.echoesofnebula.entities.Player;
import io.github.echoesofnebula.managers.ItemEffectFactory;
import io.github.echoesofnebula.managers.DialogRegistry;
import io.github.echoesofnebula.managers.ItemRegistry;
import io.github.echoesofnebula.managers.SpellRegistry;

/**
 * The Single Source of Truth for the active game session.
 * Pass this to systems (AI, Camera, UI) instead of passing multiple separate objects.
 *
 * This replaces the old MasterDataManager with proper dependency injection.
 */
public class GameContext {
    private Player currentPlayer;
    private GameWorld currentWorld;

    // Session registries - hold data loaded from JSON files
    private final DialogRegistry dialogRegistry;
    private final SpellRegistry spellRegistry;
    private final ItemRegistry itemRegistry;

    // Factory for creating item effects (non-static, injectable)
    private final ItemEffectFactory effectFactory;

    /**
     * Creates a new GameContext with all required registries and factories.
     * All dependencies are injected - no static state, fully testable.
     */
    public GameContext(DialogRegistry dialogRegistry, SpellRegistry spellRegistry,
                       ItemRegistry itemRegistry, ItemEffectFactory effectFactory) {
        this.dialogRegistry = dialogRegistry;
        this.spellRegistry = spellRegistry;
        this.itemRegistry = itemRegistry;
        this.effectFactory = effectFactory;
    }

    // --- Core Game State ---

    public Player getPlayer() {
        return currentPlayer;
    }

    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }

    public GameWorld getWorld() {
        return currentWorld;
    }

    public void setWorld(GameWorld world) {
        this.currentWorld = world;
    }

    // --- Session Registries ---

    public DialogRegistry getDialogs() {
        return dialogRegistry;
    }

    public SpellRegistry getSpells() {
        return spellRegistry;
    }

    public ItemRegistry getItems() {
        return itemRegistry;
    }

    public ItemEffectFactory getEffectFactory() {
        return effectFactory;
    }
}
