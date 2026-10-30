package io.github.echoesofnebula.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.github.echoesofnebula.ai.AIController;
import io.github.echoesofnebula.entities.enemies.Enemy;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.entities.Player;
import io.github.echoesofnebula.environment.SolidObstacle;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.items.Item;
import io.github.echoesofnebula.items.ItemFactory;
import io.github.echoesofnebula.items.effects.ItemEffectFactory;
import io.github.echoesofnebula.items.ItemRegistry;
import io.github.echoesofnebula.items.MapEntityFactory;
import io.github.echoesofnebula.entities.enemies.EnemyFactory;
import io.github.echoesofnebula.ai.AIBehavior;
import io.github.echoesofnebula.rendering.AssetHub;
import io.github.echoesofnebula.view.GameScreen;

public class GameWorld {
    private TiledMap map;
    private Array<Entity> physicsEntities;
    private Vector2 mapDimensions;

    private static final float FALLBACK_OFFSET_METERS = 10f;
    private final Array<Entity> entitiesToRemove = new Array<>();
    private final ObjectMap<Rectangle, PortalData> portals = new ObjectMap<>();
    private PortalData pendingTransition = null;
    private PortalData activePortalUnderPlayer = null;
    private Player player;
    private final ItemFactory itemFactory;
    private final EnemyFactory enemyFactory;

    public GameWorld(String initialMapPath, AssetHub assetHub, ItemRegistry itemRegistry,
                     ItemEffectFactory effectFactory) {
        this.itemFactory = new ItemFactory(assetHub, itemRegistry, effectFactory);
        this.enemyFactory = new EnemyFactory(assetHub);

        init(initialMapPath, assetHub);
    }

    private void init(String initialMapPath, AssetHub assetHub) {
        physicsEntities = new Array<>();
        map = new TmxMapLoader().load(initialMapPath);

        MapProperties prop = getMap().getProperties();
        float width = prop.get("width", Integer.class) * prop.get("tilewidth", Integer.class);
        float height = prop.get("height", Integer.class) * prop.get("tileheight", Integer.class);
        mapDimensions = new Vector2(width * GameScreen.UNIT_SCALE, height * GameScreen.UNIT_SCALE);

        loadCollisionBoundariesFromMap();

        // ✅ Beautifully simple - just pass name and position!
        loadEntitiesFromMap(assetHub, "Items", "healingItem",
            (tileObj, atlasKey, x, y, w, h) -> {
                return itemFactory.createItem(atlasKey, x, y);
            });
        loadPortalsFromMap();
    }

    public void spawnTestEnemies(){


        for(int i = 0; i<20; i++) {
            enemyFactory.createEnemy(
                "orc", player.getPosition().x + 50 * i, player.getPosition().y + 20 * i);
        }
    }

    public TiledMap getMap() { return map; }
    public Vector2 getMapDimensions() { return mapDimensions; }
    public Player getPlayer() { return player; }
    public PortalData getPendingTransition() { return pendingTransition; }
    public void clearPendingTransition() { this.pendingTransition = null; }
    public Array<Entity> getPhysicsEntities() { return physicsEntities; }


    public void spawnPlayer(AssetHub assetHub, float x, float y, ItemEffectFactory effectFactory) {
        this.player = new Player(x, y, assetHub, effectFactory);
        addDynamicEntity(player);
    }

    public void addStaticEntity(Entity entity) {
        physicsEntities.add(entity);
    }

    public void addDynamicEntity(Entity dynamicEntity) {
        physicsEntities.add(dynamicEntity);
        if (dynamicEntity instanceof Player) {
            this.player = (Player) dynamicEntity;
        }
    }

    public void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    /**
     * Safely removes an entity from the world's lists.
     * Used when transitioning maps so the old world can be cached cleanly.
     */
    public void detachEntity(Entity entity) {
        physicsEntities.removeValue(entity, true);
        entitiesToRemove.removeValue(entity, true);
    }
    public void spawnItemByName(String itemName, float worldX, float worldY) {
        System.out.println("🔍 Attempting to spawn item: '" + itemName + "' at (" + worldX + ", " + worldY + ")");

        Item item = itemFactory.createItem(itemName, worldX, worldY);

        if (item == null) {
            System.err.println("❌ ERROR: ItemFactory returned null for '" + itemName + "'!");
            return;
        }

        this.addDynamicEntity(item);
        System.out.println("✨ Spawned item: " + item.getItemName());
    }

    // --- MAIN UPDATE LOOP (Polymorphic, no instanceof!) ---

    public void update(float deltaTime) {
        // 1. Update all entities and run physics
        int physicsSize = this.physicsEntities.size;
        for (int i = 0; i < physicsSize; i++) {
            Entity entity = this.physicsEntities.get(i);

            // Polymorphism: each entity decides how to update itself
            entity.update(deltaTime);

            if (entity.getVelocity().x != 0 || entity.getVelocity().y != 0) {
                simulatePhysicsAndCollisions(deltaTime, entity);
            }
        }

        // 2. Let every entity check its own state (death, expiration, etc.)
        // No more checkEnemyDeaths() with instanceof!
        for (int i = physicsEntities.size - 1; i >= 0; i--) {
            physicsEntities.get(i).checkState(this);
        }

        // 3. Remove entities flagged for removal
        int removeSize = this.entitiesToRemove.size;
        if (removeSize > 0) {
            for (int i = 0; i < removeSize; i++) {
                Entity e = this.entitiesToRemove.get(i);
                System.out.println(e.getClass().getSimpleName() + " being removed");
                this.physicsEntities.removeValue(e, true);
            }
            this.entitiesToRemove.clear();
        }
    }

    // --- PHYSICS & COLLISIONS ---

    private void simulatePhysicsAndCollisions(float deltaTime, Entity entity) {
        float safeBoundsX = entity.getBounds().x;
        float safePosX = entity.getPosition().x;
        entity.getBounds().x += entity.getVelocity().x * deltaTime;
        Entity hitTargetX = checkEntityCollisions(entity);

        if (hitTargetX != null) {
            entity.getBounds().x = safeBoundsX;
            entity.getPosition().x = safePosX;
            onCollisionTriggered(new CollisionEventData(entity, hitTargetX, safePosX, entity.getPosition().y, true));
        } else {
            entity.getPosition().x += entity.getVelocity().x * deltaTime;
        }

        float safeBoundsY = entity.getBounds().y;
        float safePosY = entity.getPosition().y;
        entity.getBounds().y += entity.getVelocity().y * deltaTime;
        Entity hitTargetY = checkEntityCollisions(entity);

        if (hitTargetY != null) {
            entity.getBounds().y = safeBoundsY;
            entity.getPosition().y = safePosY;
            onCollisionTriggered(new CollisionEventData(entity, hitTargetY, entity.getPosition().x, safePosY, false));
        } else {
            entity.getPosition().y += entity.getVelocity().y * deltaTime;
        }
    }

    public Entity checkEntityCollisions(Entity movingEntity) {
        for (Entity staticEntity : physicsEntities) {
            if (staticEntity == movingEntity) continue;
            if (movingEntity.getBounds().overlaps(staticEntity.getBounds())) {
                return staticEntity;
            }
        }
        return null;
    }

    private void onCollisionTriggered(CollisionEventData event) {
        event.initiator.acceptCollision(this, event);
        event.target.acceptCollision(this, event);
    }

    // --- COLLISION HANDLERS ---

    public void handleSolidObstacleCollision(SolidObstacle obstacle, CollisionEventData event) {
        if (event.isXAxis) {
            event.initiator.getPosition().x = event.safeX;
        } else {
            event.initiator.getPosition().y = event.safeY;
        }
        event.initiator.getBounds().setPosition(event.initiator.getPosition().x, event.initiator.getPosition().y);
    }

    public void handlePlayerCollision(Player player, CollisionEventData event) {
        if (event.isXAxis) {
            event.initiator.getPosition().x = event.safeX;
        } else {
            event.initiator.getPosition().y = event.safeY;
        }
        // System.out.println(player.toString() + " collided with: " + event.target.toString());
    }

    public void handleEnemyCollision(Enemy enemy, CollisionEventData event) {
        if (event.isXAxis) {
            enemy.getPosition().x = event.safeX;
        } else {
            enemy.getPosition().y = event.safeY;
        }
        enemy.getBounds().setPosition(enemy.getPosition().x, enemy.getPosition().y);
        enemy.handleWallImpact();
    }

    public void handleItemCollision(Item item, CollisionEventData event) {
        item.onCollectedBy(event.initiator, this);
    }

    public void drawTextures(SpriteBatch batch) {
        int physicsSize = physicsEntities.size;
        for (int i = 0; i < physicsSize; i++) {
            Entity entity = physicsEntities.get(i);
            // Skip pure collision boxes - their drawTextures() is a no-op
            if (entity instanceof SolidObstacle) continue;

            entity.drawTextures(batch);
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // Polymorphism: each entity draws its own debug shape
        for (int i = 0; i < physicsEntities.size; i++) {
            physicsEntities.get(i).drawShapes(shapeRenderer);
        }

        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.CYAN);
        for (Rectangle portalRect : portals.keys()) {
            float w = portalRect.width > 0 ? portalRect.width : 32f;
            float h = portalRect.height > 0 ? portalRect.height : 32f;
            shapeRenderer.rect(portalRect.x, portalRect.y, w, h);
        }
    }

    private void loadPortalsFromMap() {
        portals.clear();
        MapLayer layer = map.getLayers().get("Portals");
        if (layer == null) {
            System.out.println("No 'Portals' layer found on this map file.");
            return;
        }

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                MapProperties props = object.getProperties();

                if (!props.containsKey("targetMap")) {
                    System.err.println("Warning: Portal at " + rect.x + "," + rect.y + " missing targetMap.");
                    continue;
                }

                String targetMap = props.get("targetMap", String.class);
                float targetX = 64f;
                float targetY = 64f;

                if (props.containsKey("targetX")) {
                    Object xObj = props.get("targetX");
                    if (xObj instanceof Number) targetX = ((Number) xObj).floatValue();
                }
                if (props.containsKey("targetY")) {
                    Object yObj = props.get("targetY");
                    if (yObj instanceof Number) targetY = ((Number) yObj).floatValue();
                }

                portals.put(rect, new PortalData(targetMap, targetX, targetY));
            }
        }
        System.out.println("Total portals mapped: " + portals.size);
    }

    private void loadCollisionBoundariesFromMap() {
        MapLayer layer = map.getLayers().get("Collisions");
        if (layer == null) return;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                float scaledX = rect.x * GameScreen.UNIT_SCALE;
                float scaledY = rect.y * GameScreen.UNIT_SCALE;
                float scaledW = rect.width * GameScreen.UNIT_SCALE;
                float scaledH = rect.height * GameScreen.UNIT_SCALE;
                SolidObstacle obstacle = new SolidObstacle(scaledX, scaledY, scaledW, scaledH);
                this.addStaticEntity(obstacle);
            }
        }
    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private <T extends Entity> void loadEntitiesFromMap(
        AssetHub assetHub, String layerName, String defaultAtlasKey, MapEntityFactory<T> factory) {

        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            System.out.println("⚠️ WARNING: Map layer '" + layerName + "' not found.");
            return;
        }

        System.out.println("\n--- 🛠️ Starting Map Load for Layer: [" + layerName + "] ---");

        for (MapObject object : layer.getObjects()) {
            if (!(object instanceof TiledMapTileMapObject)) continue;
            TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
            tileObj.setTile(null);

            String atlasKey = resolveAtlasKey(tileObj, defaultAtlasKey);
            AtlasRegion primaryRegion = assetHub.getRegion(atlasKey);
            if (primaryRegion == null) {
                System.err.println("❌ ERROR: AssetHub lacks region: " + atlasKey);
                continue;
            }

            int frameW = getFrameWidth(tileObj, primaryRegion);
            int frameH = primaryRegion.originalHeight;
            float scaledW = frameW * GameScreen.UNIT_SCALE;
            float scaledH = frameH * GameScreen.UNIT_SCALE;
            float x = tileObj.getX() * GameScreen.UNIT_SCALE;
            float y = (tileObj.getY() * GameScreen.UNIT_SCALE) - scaledH;

            T entity = factory.create(tileObj, atlasKey, x, y, scaledW, scaledH);
            if (entity != null) {
                this.addDynamicEntity(entity);
            }
        }
        System.out.println("--- 📦 Layer [" + layerName + "] Loading Sequence Finished ---\n");
    }

    private String resolveAtlasKey(TiledMapTileMapObject tileObj, String defaultKey) {
        String key = tileObj.getName();
        if (key == null || key.trim().isEmpty()) {
            key = tileObj.getProperties().get("type", String.class);
        }
        return (key == null || key.trim().isEmpty()) ? defaultKey : key;
    }

    private int getFrameWidth(TiledMapTileMapObject tileObj, AtlasRegion region) {
        if (tileObj.getProperties().containsKey("frame_width")) {
            return ((Number) tileObj.getProperties().get("frame_width")).intValue();
        }
        boolean isStrip = region.originalWidth > region.originalHeight;
        if (isStrip && tileObj.getProperties().containsKey("width")) {
            return ((Number) tileObj.getProperties().get("width")).intValue();
        }
        return region.originalWidth;
    }

    private void checkPortalOverlap(Player player) {
        boolean standingOnPortal = false;
        for (ObjectMap.Entry<Rectangle, PortalData> entry : portals.entries()) {
            if (player.getBounds().overlaps(entry.key)) {
                activePortalUnderPlayer = entry.value;
                standingOnPortal = true;
                break;
            }
        }
        if (!standingOnPortal) {
            activePortalUnderPlayer = null;
        }
    }

    public void tryToActivatePortal() {
        if (activePortalUnderPlayer != null) {
            this.pendingTransition = activePortalUnderPlayer;
            System.out.println("Action Key Fired! Moving to: " + pendingTransition.targetMapPath);
        }
    }

    // --- SPAWN POINT ---

    public Vector2 findSpawnPointFromMap() {
        String[] layersToCheck = {"Portals", "Collisions", "SpawnPoint"};
        for (String layerName : layersToCheck) {
            MapLayer layer = map.getLayers().get(layerName);
            if (layer == null) continue;

            for (MapObject object : layer.getObjects()) {
                if ("Spawn".equalsIgnoreCase(object.getName())) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        return new Vector2(rect.x * GameScreen.UNIT_SCALE, rect.y * GameScreen.UNIT_SCALE);
                    } else {
                        float x = object.getProperties().get("x", Float.class);
                        float y = object.getProperties().get("y", Float.class);
                        return new Vector2(x * GameScreen.UNIT_SCALE, y * GameScreen.UNIT_SCALE);
                    }
                }
            }
        }

        System.out.println("No 'Spawn' point found. Using top-left fallback.");
        float fallbackX = 0f + FALLBACK_OFFSET_METERS;
        float fallbackY = this.mapDimensions.y - FALLBACK_OFFSET_METERS;
        if (0 > fallbackY) {
            fallbackY = this.mapDimensions.y / 2f;
        }
        return new Vector2(fallbackX, fallbackY);
    }
    public void dispose() {
        if (map != null) map.dispose();
    }
}
