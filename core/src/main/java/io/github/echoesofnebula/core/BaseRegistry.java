package io.github.echoesofnebula.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.Gdx;

/**
 * Generic base class for all JSON-backed registries.
 * Handles loading, caching, and lookup of blueprint data.
 *
 * @param <T> The type of blueprint this registry holds
 */
public abstract class BaseRegistry<T> {
    private final String filePath;
    private final Class<T> type;
    private final ObjectMap<String, T> entries = new ObjectMap<>();
    private boolean initialized = false;

    protected BaseRegistry(String filePath, Class<T> type) {
        this.filePath = filePath;
        this.type = type;
    }

    /**
     * Loads all entries from the JSON file.
     * Call this once during game initialization.
     */
    public void initialize() {
        if (initialized) return;

        try {
            Json json = new Json();
            ObjectMap<String, T> loaded = json.fromJson(
                com.badlogic.gdx.utils.ObjectMap.class,
                type,
                Gdx.files.internal(filePath)
            );
            entries.putAll(loaded);
            initialized = true;
            System.out.println("✅ Loaded " + entries.size + " entries from " + filePath);
        } catch (Exception e) {
            System.err.println("❌ Failed to load registry from " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Gets a blueprint by its key.
     * @return The blueprint, or null if not found
     */
    public T get(String key) {
        if (key == null) return null;
        return entries.get(key);
    }

    /**
     * Gets ALL blueprints in this registry.
     * Useful for iteration, searching by non-key fields, UI display, etc.
     */
    public Array<T> getAll() {
        Array<T> result = new Array<>(entries.size);
        for (T value : entries.values()) {
            result.add(value);
        }
        return result;
    }

    /**
     * Gets all keys in this registry.
     */
    public Array<String> getKeys() {
        Array<String> result = new Array<>(entries.size);
        for (String key : entries.keys()) {
            result.add(key);
        }
        return result;
    }

    /**
     * Check if a key exists in this registry.
     */
    public boolean contains(String key) {
        return key != null && entries.containsKey(key);
    }

    /**
     * Get the total number of entries.
     */
    public int size() {
        return entries.size;
    }

    /**
     * Check if the registry is empty.
     */
    public boolean isEmpty() {
        return entries.size == 0;
    }

    /**
     * Get the file path this registry loads from.
     */
    public String getFilePath() {
        return filePath;
    }
}
