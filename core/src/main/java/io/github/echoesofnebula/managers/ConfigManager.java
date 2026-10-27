package io.github.echoesofnebula.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.echoesofnebula.config.GameConfig;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";
    private static GameConfig currentConfig;
    private static Json json;
    public static void initialize() {
        json = new Json();
        json.setOutputType(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        json.setIgnoreUnknownFields(true);
        json.setTypeName(null);

        FileHandle assetFile = Gdx.files.internal(CONFIG_FILE);

        if (assetFile.exists()) {
            try {
                String rawJsonText = assetFile.readString();
                System.out.println("--- RAW JSON CONTENT DETECTED INSIDE FILE ---");
                System.out.println(rawJsonText);
                System.out.println("--------------------------------------------");

                currentConfig = json.fromJson(GameConfig.class, rawJsonText);

                if (currentConfig == null || currentConfig.gameTitle == null) {
                    System.err.println("JSON values bypassed! Forcing manual field verification.");
                    loadDefaults();
                } else {
                    System.out.println("Config assigned perfectly! Active title from JSON: " + currentConfig.gameTitle);
                }
            } catch (Exception e) {
                System.err.println("Error parsing config.json asset: " + e.getMessage());
                loadDefaults();
            }
        } else {
            System.err.println("Could not find config.json in assets!");
            loadDefaults();
        }
    }

    private static void loadDefaults() {
        System.out.println("Loading defaults");
        currentConfig = new GameConfig();
    }

    public static GameConfig get() {
        if (currentConfig == null) {
            initialize();
        }
        return currentConfig;
    }
}
