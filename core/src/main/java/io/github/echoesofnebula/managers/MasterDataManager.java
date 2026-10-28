package io.github.echoesofnebula.managers;


public class MasterDataManager {
    private static DialogRegistry dialogRegistry;
    private static SpellRegistry spellRegistry;

    public static void loadAllRegistries() {
        // Initialize read/write application settings config first
        ConfigManager.initialize();

        // Instantiate and load your generic file caches
        dialogRegistry = new DialogRegistry();
        spellRegistry = new SpellRegistry();

        dialogRegistry.initialize();
        spellRegistry.initialize();
    }

    public static DialogRegistry getDialogs() { return dialogRegistry; }
    public static SpellRegistry getSpells() { return spellRegistry; }
}
