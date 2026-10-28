package io.github.echoesofnebula.managers;

import com.badlogic.gdx.utils.ObjectMap;
import io.github.echoesofnebula.core.GameWorld;

public class WorldCache {
    private final ObjectMap<String, GameWorld> loadedWorlds = new ObjectMap<>();
    private final AssetHub assetHub;
    public WorldCache(AssetHub assetHub) {
        this.assetHub = assetHub;
    }
    public GameWorld getOrCreateWorld(String mapPath, ItemRegistry itemRegistry, ItemEffectFactory effectFactory) {
        if (loadedWorlds.containsKey(mapPath)) {
            return loadedWorlds.get(mapPath);
        }
        GameWorld freshWorld = new GameWorld(mapPath, assetHub, itemRegistry, effectFactory); // ✅ UPDATED
        loadedWorlds.put(mapPath, freshWorld);
        return freshWorld;
    }

    public void disposeAll() {
        for (GameWorld world : loadedWorlds.values()) {
            world.dispose();
        }
        loadedWorlds.clear();
    }
}
