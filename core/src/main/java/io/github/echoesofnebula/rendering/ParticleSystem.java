package io.github.echoesofnebula.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ParticleSystem {
    public enum ParticleType {
        FIRE("particles/fire.p");
        final String filePath;
        ParticleType(String filePath) {
            this.filePath = filePath;
        }
    }

    // Map holding individual memory recycling pipelines for each type
    private static final ObjectMap<ParticleType, ParticleEffectPool> pools = new ObjectMap<>();
    private static final Array<PooledEffect> activeEffects = new Array<>();

    // Cache the scale factor locally (1f / 16f)
    private static float unitScale;

    public static void initialize(float scale) {
        unitScale = scale;
        clearAll();

        // Dynamically configure and load every registered particle configuration file
        for (ParticleType type : ParticleType.values()) {
            ParticleEffect baseEffect = new ParticleEffect();

            // Loads the structural template text file and its associated images from the assets folder
            baseEffect.load(Gdx.files.internal(type.filePath), Gdx.files.internal("particles"));

            // CRITICAL: Scale the entire template to match your 1/16f viewport coordinates automatically!
            baseEffect.scaleEffect(unitScale);

            // Pre-allocate pools to prevent stuttering mid-game during intense RPG combat
            ParticleEffectPool pool = new ParticleEffectPool(baseEffect, 4, 30);
            pools.put(type, pool);
        }
    }

    /**
     * Spawns any specified particle configuration anywhere in your RPG world.
     */
    public static void spawn(ParticleType type, float worldX, float worldY) {
        ParticleEffectPool pool = pools.get(type);
        if (pool == null) return;

        PooledEffect effect = pool.obtain();
        effect.setPosition(worldX, worldY);
        effect.start();
        activeEffects.add(effect);
    }

    public static void updateAndDraw(float deltaTime, SpriteBatch batch) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            PooledEffect effect = activeEffects.get(i);

            effect.update(deltaTime);
            effect.draw(batch);

            if (effect.isComplete()) {
                effect.reset();
                effect.free();
                activeEffects.removeIndex(i);
            }
        }
    }

    private static void clearAll() {
        for (ParticleEffectPool pool : pools.values()) {
            // Freeing the pool returns internal assets
            pool.clear();
        }
        pools.clear();
        activeEffects.clear();
    }

    public static void dispose() {
        clearAll();
    }
}
