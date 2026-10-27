package io.github.echoesofnebula.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker; // Ensure this is imported!
import io.github.echoesofnebula.App;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    private static final boolean IS_DEVELOPMENT_MODE = true;

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        // Only run the heavy packing pipeline if we are actively building the game in our IDE
        if (IS_DEVELOPMENT_MODE) {
            TexturePacker.Settings settings = new TexturePacker.Settings();
            settings.useIndexes = true;
            settings.maxWidth = 2048;
            settings.maxHeight = 2048;
            settings.combineSubdirectories = true;

            // FIX: Use "../" to step out of the overridden assets/ directory
            // and return to your true project root!
            java.io.File inputDir = new java.io.File("../assets-raw/sprites");

            // The output needs to write into the assets/ folder we are currently standing in
            java.io.File outputDir = new java.io.File("sprites");

            System.out.println("📦 Packing atlas assets from raw staging directories...");
            try {
                TexturePacker.process(
                    settings,
                    inputDir.getCanonicalPath(),
                    outputDir.getCanonicalPath(),
                    "game_assets"
                );
                System.out.println("✅ Atlas compiled successfully inside: " + outputDir.getCanonicalPath());
            } catch (Exception e) {
                System.err.println("❌ ERROR: Failed to pack textures.");
                try {
                    System.err.println("Looked inside: " + inputDir.getCanonicalPath());
                } catch (Exception ignored) {}
                e.printStackTrace();
            }
        }
        return new Lwjgl3Application(new App(), getDefaultConfiguration());
    }
        private static Lwjgl3ApplicationConfiguration getDefaultConfiguration () {
            Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
            //configuration.setTitle("Echoes Of Nebula");

            configuration.useVsync(true);
            configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);

            configuration.setWindowedMode(1280, 720);
            configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

            return configuration;
        }

}

