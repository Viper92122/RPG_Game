package io.github.echoesofnebula.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class AssetHub {
    private final AssetManager manager = new AssetManager();
    private static final String ATLAS_PATH = "sprites/game_assets.atlas";
    private TextureAtlas atlas;

    public void queueLoading() {
        manager.load(ATLAS_PATH, TextureAtlas.class);
    }

    public void finishLoading() {
        manager.finishLoading();
        if (this.atlas == null) {
            this.atlas = manager.get(ATLAS_PATH, TextureAtlas.class);
        }
    }

    public TextureAtlas.AtlasRegion getRegion(String name) {
        if (atlas == null) {
            System.out.println("❌ ERROR: AssetHub.getRegion() called before atlas was assigned!");
            return null;
        }
        return atlas.findRegion(name);
    }

    public Array<TextureAtlas.AtlasRegion> getRegions(String name) {
        if (atlas == null) {
            System.out.println("❌ ERROR: AssetHub.getRegions() called before atlas was assigned!");
            return null;
        }
        return atlas.findRegions(name);
    }

    public void dispose() {
        manager.dispose();
    }
}
