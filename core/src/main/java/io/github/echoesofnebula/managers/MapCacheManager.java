package io.github.echoesofnebula.managers;

import com.badlogic.gdx.utils.ObjectMap;
import io.github.echoesofnebula.core.GameWorld;

public class MapCacheManager {
    private final ObjectMap<String, GameWorld> loadedWorlds = new ObjectMap<>();
    private final AssetHub assetHub;

    public MapCacheManager(AssetHub assetHub) {
        this.assetHub = assetHub;
    }
    public GameWorld getOrCreateWorld(String mapPath) {
        if (loadedWorlds.containsKey(mapPath)) {
            System.out.println("Retrieving cached world state for: " + mapPath);
            return loadedWorlds.get(mapPath);
        }

        System.out.println("First time visiting map. Generating fresh simulation for: " + mapPath);

        GameWorld freshWorld = new GameWorld(mapPath, assetHub);
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
