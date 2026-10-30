package io.github.echoesofnebula.rendering;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.github.echoesofnebula.entities.CharacterAnimationSet;
import io.github.echoesofnebula.items.ItemBlueprint;

public class AssetHub {
    private final AssetManager manager;
    private static final String ATLAS_PATH = "sprites/game_assets.atlas";
    private TextureAtlas atlas;
    private static Texture whitePixel;
    public AssetHub() {
        this.manager = new AssetManager();

        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }
    public Object getItemVisual(ItemBlueprint blueprint) {
        if (blueprint.isAnimated) {
            Animation<TextureRegion> anim = getAnimation(blueprint.atlasKey, blueprint.frameDuration);
            if (anim != null) return anim;
        }
        return getRegion(blueprint.atlasKey);
    }

    public static TextureRegion getWhitePixel() {
        if (whitePixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return new TextureRegion(whitePixel);
    }
    public void queueInitialAssets() {
        manager.load(ATLAS_PATH, TextureAtlas.class);
    }

    public void queueMap(String mapPath) {
        if (!manager.isLoaded(mapPath, TiledMap.class)) {
            manager.load(mapPath, TiledMap.class);
        }
    }

    public boolean update() {
        return manager.update();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    public void finishLoading() {
        manager.finishLoading();
        if (this.atlas == null && manager.isLoaded(ATLAS_PATH, TextureAtlas.class)) {
            this.atlas = manager.get(ATLAS_PATH, TextureAtlas.class);
        }
    }

    public TextureAtlas.AtlasRegion getRegion(String name) {
        if (atlas == null) {
            if (manager.isLoaded(ATLAS_PATH, TextureAtlas.class)) {
                this.atlas = manager.get(ATLAS_PATH, TextureAtlas.class);
            } else {
                System.err.println("❌ ERROR: AssetHub.getRegion() called before atlas was loaded!");
                return null;
            }
        }
        return atlas.findRegion(name);
    }
    private final ObjectMap<String, BitmapFont> fonts = new ObjectMap<>();

    public void addFont(String name, BitmapFont font) { fonts.put(name, font); }
    public Array<TextureAtlas.AtlasRegion> getRegions(String name) {
        if (atlas == null) {
            if (manager.isLoaded(ATLAS_PATH, TextureAtlas.class)) {
                this.atlas = manager.get(ATLAS_PATH, TextureAtlas.class);
            } else {
                System.err.println("❌ ERROR: AssetHub.getRegions() called before atlas was loaded!");
                return null;
            }
        }
        return atlas.findRegions(name);
    }

    public TiledMap getMap(String mapPath) {
        if (manager.isLoaded(mapPath, TiledMap.class)) {
            return manager.get(mapPath, TiledMap.class);
        }
        return null;
    }

    public void dispose() {
        manager.dispose();
    }

    public BitmapFont getFont(String name) { return fonts.get(name); }

    public Animation<TextureRegion> getAnimation(String atlasKey, float frameDuration) {
        Array<TextureRegion> frames = getAnimationFrames(atlasKey);
        if (frames == null || frames.size == 0) return null;
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public Array<TextureRegion> getAnimationFrames(String atlasKey) {
        TextureAtlas.AtlasRegion primaryRegion = getRegion(atlasKey);
        if (primaryRegion == null) return null;

        Array<TextureAtlas.AtlasRegion> indexedRegions = getRegions(atlasKey);
        boolean isIndexed = indexedRegions != null && indexedRegions.size > 1;
        boolean isStrip = primaryRegion.originalWidth > primaryRegion.originalHeight;

        if (!isIndexed && !isStrip) {
            return null; // Not an animation
        }

        Array<TextureRegion> frames = new Array<>();

        if (isIndexed) {
            // Multi-file indexed sequence (e.g., frame_0, frame_1, frame_2)
            for (TextureAtlas.AtlasRegion r : indexedRegions) {
                frames.add(r);
            }
        } else {
            // Single horizontal strip (e.g., 288x39 healthPotion)
            int frameWidth = primaryRegion.originalHeight; // Assume square frames
            int frameHeight = primaryRegion.originalHeight;
            int totalFrames = primaryRegion.getRegionWidth() / frameWidth;

            for (int i = 0; i < totalFrames; i++) {
                int frameX = primaryRegion.getRegionX() + (i * frameWidth);
                int frameY = primaryRegion.getRegionY();

                TextureRegion individualFrame = new TextureRegion(
                    primaryRegion.getTexture(),
                    frameX, frameY,
                    frameWidth, frameHeight
                );
                frames.add(individualFrame);
            }
        }

        return frames;
    }

    public Array<TextureRegion> getAnimationFrames(String atlasKey, int frameWidth, int frameHeight) {
        TextureAtlas.AtlasRegion primaryRegion = getRegion(atlasKey);
        if (primaryRegion == null) return null;

        int totalFrames = primaryRegion.getRegionWidth() / frameWidth;
        Array<TextureRegion> frames = new Array<>();

        for (int i = 0; i < totalFrames; i++) {
            int frameX = primaryRegion.getRegionX() + (i * frameWidth);
            int frameY = primaryRegion.getRegionY();

            frames.add(new TextureRegion(
                primaryRegion.getTexture(),
                frameX, frameY,
                frameWidth, frameHeight
            ));
        }

        return frames;
    }

    public CharacterAnimationSet loadCharacterAnimations(String atlasKey, float frameDuration) {
        TextureRegion masterSheet = getRegion(atlasKey);
        if (masterSheet == null) {
            System.err.println("❌ ERROR: Could not find character sprite sheet: " + atlasKey);
            return null;
        }

        int totalRows = 5;
        int totalCols = 4;

        int frameWidth = Math.max(1, masterSheet.getRegionWidth() / totalCols);
        int frameHeight = Math.max(1, masterSheet.getRegionHeight() / totalRows);

        TextureRegion[][] tmp = masterSheet.split(frameWidth, frameHeight);

        Animation<TextureRegion> walkDown = createAnimation(tmp[0], totalCols, frameDuration);

        Animation<TextureRegion> sideDownRight = createAnimation(tmp[1], totalCols, frameDuration);
        Animation<TextureRegion> sideDownLeft = createFlippedAnimation(tmp[1], totalCols, frameDuration);

        Animation<TextureRegion> right = createAnimation(tmp[2], totalCols, frameDuration);
        Animation<TextureRegion> left = createFlippedAnimation(tmp[2], totalCols, frameDuration);

        Animation<TextureRegion> sideUpRight = createAnimation(tmp[3], totalCols, frameDuration);
        Animation<TextureRegion> sideUpLeft = createFlippedAnimation(tmp[3], totalCols, frameDuration);

        Animation<TextureRegion> walkUp = createAnimation(tmp[4], totalCols, frameDuration);

        return new CharacterAnimationSet(
            walkDown, sideDownRight, sideDownLeft,
            right, left, sideUpRight, sideUpLeft, walkUp
        );
    }


    private Animation<TextureRegion> createAnimation(TextureRegion[] rowFrames, int count, float duration) {
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);
        for (int i = 0; i < count; i++) {
            frames.add(rowFrames[i]);
        }
        return new Animation<>(duration, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> createFlippedAnimation(TextureRegion[] rowFrames, int count, float duration) {
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);
        for (int i = 0; i < count; i++) {
            // Create a copy of the frame and flip it horizontally
            TextureRegion frame = new TextureRegion(rowFrames[i]);
            frame.flip(true, false);
            frames.add(frame);
        }
        return new Animation<>(duration, frames, Animation.PlayMode.LOOP);
    }
}
