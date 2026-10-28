package io.github.echoesofnebula.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

import io.github.echoesofnebula.ai.EnemyBrain;
import io.github.echoesofnebula.entities.Enemy;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.core.UpdateAble;
import io.github.echoesofnebula.entities.Player;
import io.github.echoesofnebula.environment.SolidObstacle;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.items.MapEntityFactory;
import io.github.echoesofnebula.managers.ItemEffectRegistry;
import io.github.echoesofnebula.items.ItemEffect;
import io.github.echoesofnebula.items.ConsumableItem;
import io.github.echoesofnebula.items.Item;
import io.github.echoesofnebula.managers.ItemFactory;
import io.github.echoesofnebula.view.GameScreen;
import io.github.echoesofnebula.managers.AssetHub;

public class GameWorld {
    private TiledMap map;
    private Array<UpdateAble> updateables;
    private Array<Entity> physicsEntities;
    private Vector2 mapDimensions;

    // TODO: Temporarily stored here
    private static final float FALLBACK_OFFSET_METERS = 10f;
    private final Array<Entity> entitiesToRemove = new Array<>();
    private final ObjectMap<Rectangle, PortalData> portals = new ObjectMap<>();
    private PortalData pendingTransition = null;
    private PortalData activePortalUnderPlayer = null;
    private Player player;
    private final ItemFactory itemFactory;
    public GameWorld(String initialMapPath, AssetHub assetHub) {
        itemFactory = new ItemFactory(assetHub);
        init(initialMapPath, assetHub
        );
    }

    public void spawnItemByName(String itemName, float worldX, float worldY) {
        System.out.println("🔍 Attempting to spawn item: '" + itemName + "' at (" + worldX + ", " + worldY + ")");

        Item item = itemFactory.createItem(itemName, worldX, worldY);

        if (item == null) {
            System.err.println("❌ ERROR: ItemFactory returned null for '" + itemName + "'!");
            return;
        }

        System.out.println("✅ Item created: " + item.getItemName());
        System.out.println("   Position: (" + item.getPosition().x + ", " + item.getPosition().y + ")");
        System.out.println("   Bounds: (" + item.getBounds().x + ", " + item.getBounds().y +
            ", " + item.getBounds().width + ", " + item.getBounds().height + ")");

        this.addDynamicEntity(item);

        System.out.println("✨ Successfully added to world. Total entities: " + physicsEntities.size);
    }
    private void init(String initialMapPath, AssetHub assetHub) {
        updateables = new Array<>();
        physicsEntities = new Array<>();

        // 1. Try to get the asynchronously loaded map first
        this.map = assetHub.getMap(initialMapPath);

        // 2. Fallback for dynamic maps not pre-queued (prevents crashes during portal transitions)
        if (this.map == null) {
            System.out.println("⚠️ Map not pre-loaded, loading synchronously (may cause minor hitch): " + initialMapPath);
            this.map = new TmxMapLoader().load(initialMapPath);
        }

        MapProperties prop = getMap().getProperties();
        float width = prop.get("width", Integer.class) * prop.get("tilewidth", Integer.class);
        float height = prop.get("height", Integer.class) * prop.get("tileheight", Integer.class);

        mapDimensions = new Vector2(width * GameScreen.UNIT_SCALE, height * GameScreen.UNIT_SCALE);

        loadCollisionBoundariesFromMap();
        loadEntitiesFromMap(assetHub, "Items", "healingItem", (tileObj, atlasKey, x, y, w, h, region, anim) -> {
            boolean isConsumable = !tileObj.getProperties().containsKey("consumable") ||
                ((Boolean) tileObj.getProperties().get("consumable"));

            if (isConsumable) {
                String actionType = tileObj.getProperties().containsKey("action_type") ?
                    tileObj.getProperties().get("action_type", String.class) : "HEAL";
                float intensity = tileObj.getProperties().containsKey("intensity") ?
                    ((Number) tileObj.getProperties().get("intensity")).floatValue() : 50f;

                ItemEffect concreteEffect = ItemEffectRegistry.createEffect(actionType, intensity);

                // 🌟 CHECK: Double check that 'w' and 'h' are passed to ConsumableItem!
                return (anim != null) ?
                    new ConsumableItem(atlasKey, x, y, w, h, concreteEffect, anim) :
                    new ConsumableItem(atlasKey, x, y, w, h, concreteEffect, region);

            } else {
                return (anim != null) ?
                    new Item(atlasKey, x, y, w, h, anim) :
                    new Item(atlasKey, x, y, w, h, region);
            }
        });
        loadPortalsFromMap();
    }

    public TiledMap getMap() {
        return map;
    }

    public Vector2 getMapDimensions() {
        return mapDimensions;
    }

    public Player getPlayer() {
        return player;
    }

    public PortalData getPendingTransition() {
        return pendingTransition;
    }

    public void clearPendingTransition() {
        this.pendingTransition = null;
    }

    public void spawnPlayer(AssetHub assetHub, float x, float y) {
        this.player = new Player(x, y, assetHub);
        addDynamicEntity(player);
    }

    public void spawnWaveOfEnemies(Player p, int count) {
        for (int i = 0; i < count; i++) {
            Enemy e = new Enemy(p.getPosition().x, p.getPosition().y, new EnemyBrain(p));

            this.addDynamicEntity(e);
        }
    }

    public void addStaticEntity(Entity entity) {
        physicsEntities.add(entity);
    }

    public <T extends Entity & UpdateAble> void addDynamicEntity(T dynamicEntity) {
        physicsEntities.add(dynamicEntity);
        updateables.add(dynamicEntity);

        if (dynamicEntity instanceof Player) {
            this.player = (Player) dynamicEntity;
        }
    }

    public void removeEntity(Entity entity) {

        entitiesToRemove.add(entity);
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
                    System.err.println("Warning: Portal shape at " + rect.x + "," + rect.y + " is missing targetMap attributes.");
                    continue;
                }

                String targetMap = props.get("targetMap", String.class);
                float targetX = 64f * GameScreen.UNIT_SCALE;
                float targetY = 64f * GameScreen.UNIT_SCALE;

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

                // FIX: Extract primitive values first to prevent compounding scale calculations
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
    private <T extends Entity & UpdateAble> void loadEntitiesFromMap(AssetHub assetHub, String layerName, String defaultAtlasKey, MapEntityFactory<T> factory) {
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            System.out.println("⚠️ WARNING: Map layer '" + layerName + "' not found.");
            return;
        }

        System.out.println("\n--- 🛠️ Starting Map Load for Layer: [" + layerName + "] ---");

        for (MapObject object : layer.getObjects()) {
            if (!(object instanceof TiledMapTileMapObject)) continue;

            TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
            tileObj.setTile(null); // Decouple from Tiled default renderer

            // 1. Resolve key names and fetch textures
            String atlasKey = resolveAtlasKey(tileObj, defaultAtlasKey);
            AtlasRegion primaryRegion = assetHub.getRegion(atlasKey);

            if (primaryRegion == null) {
                System.err.println("❌ ERROR: AssetHub lacks mapping region trace token: " + atlasKey);
                continue;
            }

            // 2. Parse frame dimensions and map coordinates
            // 2. Parse frame dimensions (used strictly for slicing the sprite sheet)
            int frameW = getFrameWidth(tileObj, primaryRegion);
            int frameH = getFrameHeight(tileObj, primaryRegion);

// 3. Parse render dimensions (used for drawing the item on screen)
// This allows you to make the potion visually smaller than its actual sprite sheet size!
            float renderW = tileObj.getProperties().containsKey("render_width") ?
                ((Number) tileObj.getProperties().get("render_width")).floatValue() : (float) frameW;

            float renderH = tileObj.getProperties().containsKey("render_height") ?
                ((Number) tileObj.getProperties().get("render_height")).floatValue() : (float) frameH;

            float scaledW = renderW * GameScreen.UNIT_SCALE;
            float scaledH = renderH * GameScreen.UNIT_SCALE;

            float x = tileObj.getX() * GameScreen.UNIT_SCALE;
            float y = (tileObj.getY() * GameScreen.UNIT_SCALE) - scaledH; // Anchors to the bottom

            // 3. Build animation if needed
            Array<AtlasRegion> indexedRegions = assetHub.getRegions(atlasKey);
            Animation<TextureRegion> anim = createAnimation(tileObj, primaryRegion, indexedRegions, frameW, frameH);

            // 4. Instantiate & register the generic entity
            T entity = factory.create(tileObj, atlasKey, x, y, scaledW, scaledH, primaryRegion, anim);
            if (entity != null) {
                this.addDynamicEntity(entity);
            }
        }
        System.out.println("--- 📦 Layer [" + layerName + "] Loading Sequence Finished ---\n");
    }

    // --- PRIVATE HELPERS ---

    private String resolveAtlasKey(TiledMapTileMapObject tileObj, String defaultKey) {
        String key = tileObj.getName();
        if (key == null || key.trim().isEmpty()) {
            key = tileObj.getProperties().get("type", String.class);
        }
        return (key == null || key.trim().isEmpty()) ? defaultKey : key;
    }


    private int getFrameWidth(TiledMapTileMapObject tileObj, AtlasRegion region) {
        // ALWAYS check for explicit frame_width first
        if (tileObj.getProperties().containsKey("frame_width")) {
            return ((Number) tileObj.getProperties().get("frame_width")).intValue();
        }
        // Fallback logic...
        return region.getRegionWidth();
    }

    private int getFrameHeight(TiledMapTileMapObject tileObj, AtlasRegion region) {
        // 1. If an explicit frame_height custom property exists, prioritize it
        if (tileObj.getProperties().containsKey("frame_height")) {
            return ((Number) tileObj.getProperties().get("frame_height")).intValue();
        }
        // 2. Fallback to Tiled object's height property if available
        if (tileObj.getProperties().containsKey("height")) {
            return ((Number) tileObj.getProperties().get("height")).intValue();
        }
        // 3. Static fallback boundary
        return region.originalHeight;
    }

    private Animation<TextureRegion> createAnimation(
        TiledMapTileMapObject tileObj,
        AtlasRegion primaryRegion,
        Array<AtlasRegion> indexedRegions,
        int frameW, int frameH) {

        String entityName = tileObj.getName() != null ? tileObj.getName() : "Unnamed (" + primaryRegion.name + ")";
        boolean isIndexed = indexedRegions != null && indexedRegions.size > 1;

        // 🌟 NEW: Calculate total frames based on a grid (handles horizontal, vertical, AND grid sheets)
        int totalFrames = (primaryRegion.getRegionWidth() / frameW) * (primaryRegion.getRegionHeight() / frameH);

        System.out.println("🔍 Scanning Asset [" + entityName + "]:");
        System.out.println("   -> Atlas Region Size: [" + primaryRegion.getRegionWidth() + "x" + primaryRegion.getRegionHeight() + "]");
        System.out.println("   -> Target Frame Size: [" + frameW + "x" + frameH + "]");
        System.out.println("   -> Calculated Total Frames: " + totalFrames);
        System.out.println("   -> isIndexed: " + isIndexed);

        if (!isIndexed && totalFrames <= 1) {
            System.out.println("   -> ⏸️ Static Item Detected (1 frame). No animation compiled.");
            return null;
        }

        float duration = tileObj.getProperties().containsKey("frame_duration") ?
            ((Number) tileObj.getProperties().get("frame_duration")).floatValue() : 0.1f;

        Array<TextureRegion> frames = new Array<>();

        if (isIndexed) {
            System.out.println("   -> 🎞️ Compilation: Multi-file Indexed Sequence found.");
            for (AtlasRegion r : indexedRegions) {
                frames.add(r);
            }
        } else {
            System.out.println("   -> 🎞️ Compilation: Sprite Sheet detected. Slicing into " + totalFrames + " frames.");

            int cols = primaryRegion.getRegionWidth() / frameW;
            int rows = primaryRegion.getRegionHeight() / frameH;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int frameX = primaryRegion.getRegionX() + (col * frameW);
                    int frameY = primaryRegion.getRegionY() + (row * frameH);

                    TextureRegion individualFrame = new TextureRegion(
                        primaryRegion.getTexture(),
                        frameX,
                        frameY,
                        frameW,
                        frameH
                    );
                    frames.add(individualFrame);
                }
            }
        }

        System.out.println("   -> ⚡ Animation built successfully with " + frames.size + " frames at " + duration + "s per frame.");
        return frames.size > 0 ? new Animation<>(duration, frames, Animation.PlayMode.LOOP) : null;
    }
    public void update(float deltaTime) {
        int updatableSize = this.updateables.size;
        for (int i = 0; updatableSize > i; i++) { // Rewritten without less-than symbol!
            this.updateables.get(i).update(deltaTime);
        }

        int physicsSize = this.physicsEntities.size;
        for (int i = 0; physicsSize > i; i++) { // Rewritten without less-than symbol!
            Entity entity = this.physicsEntities.get(i);
            if (entity.getVelocity().x != 0 || entity.getVelocity().y != 0) {
                simulatePhysicsAndCollisions(deltaTime, entity);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (player != null) {
                this.spawnItemByName("health_potion", player.getPosition().x + 1f, player.getPosition().y);
            }
        }
        // 3. THIRD: Track static portal interactions and key-taps
       /* if (player != null) {
            checkPortalOverlap(player);

            if (player.getMovementIntent() != null && player.getMovementIntent().triggeredAction) {
                tryToActivatePortal();
            }
        */
        // }

        int removeSize = this.entitiesToRemove.size;
        if (removeSize > 0) {
            for (int i = 0; removeSize > i; i++) {
                Entity e = this.entitiesToRemove.get(i);
                System.out.println(e.getClass().getSimpleName() + " being removed");
                this.physicsEntities.removeValue(e, true);
                if (e instanceof UpdateAble) {
                    this.updateables.removeValue((UpdateAble) e, true);
                }
            }
            this.entitiesToRemove.clear();
        }
    }

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
            System.out.println("Action Key Fired! Moving character into map scene: " + pendingTransition.targetMapPath);
        }
    }


    private void onCollisionTriggered(CollisionEventData event) {
        // System.out.println("💥 COLLISION! Player hit: " + event.target.getClass().getSimpleName());

        event.initiator.acceptCollision(this, event);
        event.target.acceptCollision(this, event);
    }

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
        System.out.println(player.toString() + " collided with: " + event.target.toString());
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

    public void drawTextures(SpriteBatch batch) {
        int physicsSize = physicsEntities.size;
        for (int i = 0; i < physicsSize; i++) {
            Entity entity = physicsEntities.get(i);

            // Skip drawing pure collision boxes
            if (entity instanceof SolidObstacle) {
                continue;
            }

            // Cast to your interface so the player's custom texture code fires!
            if (entity instanceof SpriteRenderable) {
                ((SpriteRenderable) entity).drawTextures(batch);
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
        for (Entity entity : physicsEntities) {
            if (entity instanceof SolidObstacle) {
                Rectangle bounds = entity.getBounds();
                float w = bounds.width > 0 ? bounds.width : 32f;
                float h = bounds.height > 0 ? bounds.height : 32f;
                shapeRenderer.rect(bounds.x, bounds.y, w, h);
            }
        }
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.CYAN);
        for (Rectangle portalRect : portals.keys()) {
            float w = portalRect.width > 0 ? portalRect.width : 32f;
            float h = portalRect.height > 0 ? portalRect.height : 32f;
            shapeRenderer.rect(portalRect.x, portalRect.y, w, h);
        }
    }

    public void dispose() {
        if (map != null) map.dispose();
    }

    public Vector2 findSpawnPointFromMap() {
        // Check your object layers for a spawn point marker
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
                        // If you used a Point object instead of a Rectangle
                        float x = object.getProperties().get("x", Float.class);
                        float y = object.getProperties().get("y", Float.class);
                        return new Vector2(x * GameScreen.UNIT_SCALE, y * GameScreen.UNIT_SCALE);
                    }
                }
            }
        }

        System.out.println("No named 'Spawn' point object found. Calculating top-left fallback position.");

        // 1. Top-left of the map is X = 0, Y = map height (already scaled to meters in init())
        float fallbackX = 0f + FALLBACK_OFFSET_METERS;
        float fallbackY = this.mapDimensions.y - FALLBACK_OFFSET_METERS;

        // 2. Guard against extreme edge offsets collapsing below 0
        if (0 > fallbackY) {
            fallbackY = this.mapDimensions.y / 2f;
        }

        return new Vector2(fallbackX, fallbackY);
    }

    public void handleItemCollision(Item item, CollisionEventData event) {
        if (event.initiator instanceof Player) {
            Player player = (Player) event.initiator;

            if (item instanceof ConsumableItem) {
                ConsumableItem consumable = (ConsumableItem) item;
                consumable.use(player);
            }
        } else {
            System.out.println("Collected a regular persistent inventory/quest entity: " + item.getItemName());
        }

        item.collect();
        this.removeEntity(item);
    }
}





