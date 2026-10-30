package io.github.echoesofnebula.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class ConfigLoader {
    private static GameConfig config;

    public static GameConfig load() {
        if (config != null) return config; // Already loaded

        Json json = new Json();
        json.setIgnoreUnknownFields(true);

        if (Gdx.files.internal("config.json").exists()) {
            config = json.fromJson(GameConfig.class, Gdx.files.internal("config.json"));
        } else {
            System.out.println("config.json not found, using defaults.");
            config = new GameConfig(); // Your default fallback
        }
        return config;
    }
}
