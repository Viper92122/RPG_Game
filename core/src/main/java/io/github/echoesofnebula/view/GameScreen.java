package io.github.echoesofnebula.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.core.GameContext;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.core.PortalData;
import io.github.echoesofnebula.entities.Player;
import io.github.echoesofnebula.input.PlayerController;
import io.github.echoesofnebula.input.PlayerInputProcessor;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.managers.CameraManager;
import io.github.echoesofnebula.managers.MapCacheManager;
import io.github.echoesofnebula.managers.ParticleTestHub;
import io.github.echoesofnebula.input.PlayerIntent;

public class GameScreen implements Screen {
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final AssetHub assetHub;

    private MapCacheManager mapCacheManager;
    private final GameContext context;
    private CustomTiledMapRenderer mapRenderer;
    private int[] groundLayers;
    private int[] foregroundLayers;
    private final PlayerController playerController;
    private final CameraManager cameraManager;

    public static final float UNIT_SCALE = 1f / 16f;

    // UPDATED: Accept AssetHub as a parameter
    public GameScreen(Game game, AssetHub assetHub) {
        this.assetHub = assetHub;

        // REMOVED: assetHub.queueLoading();
        // REMOVED: assetHub.finishLoading();

        context = new GameContext();
        mapCacheManager = new MapCacheManager(assetHub);

        // This is now instant because LoadingScreen already queued and loaded it
        GameWorld world = mapCacheManager.getOrCreateWorld("tilemap_first.tmx");

        Vector2 spawnPoint = world.findSpawnPointFromMap();
        world.spawnPlayer(assetHub, spawnPoint.x, spawnPoint.y);

        context.setWorld(world);
        context.setPlayer(world.getPlayer());

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();

        float viewportWidth = 15.0f;
        float viewportHeight = 10.0f;
        camera.setToOrtho(false, viewportWidth, viewportHeight);

        mapRenderer = new CustomTiledMapRenderer(context.getWorld().getMap(), UNIT_SCALE);
        initMapLayers();

        PlayerInputProcessor playerInputProcessor = new PlayerInputProcessor();
        playerController = new PlayerController(context, playerInputProcessor);
        cameraManager = new CameraManager(camera);

        ParticleTestHub.initialize(GameScreen.UNIT_SCALE);
    }

    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);
        AnimatedTiledMapTile.updateAnimationBaseTime();

        playerController.tickInput();
        GameWorld activeWorld = context.getWorld();
        activeWorld.update(Gdx.graphics.getDeltaTime());

        cameraManager.updateTracking(context);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render(groundLayers);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        activeWorld.drawTextures(batch);
        ParticleTestHub.updateAndDraw(Gdx.graphics.getDeltaTime(), batch);
        batch.end();

        mapRenderer.render(foregroundLayers);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        activeWorld.draw(shapeRenderer);
        shapeRenderer.end();
    }

    private void changeActiveMap(PortalData transition) {
        mapRenderer.dispose();
        Player activePlayer = context.getPlayer();

        if (activePlayer != null) {
            activePlayer.setIntent(new PlayerIntent());
        }

        context.getWorld().dispose();

        // This will use the async map if preloaded, or fallback gracefully if it's a new map
        GameWorld freshWorld = new GameWorld(transition.targetMapPath, assetHub);

        activePlayer.getPosition().set(transition.targetSpawnPosition);
        activePlayer.getBounds().setPosition(activePlayer.getPosition().x, activePlayer.getPosition().y);

        freshWorld.addDynamicEntity(activePlayer);

        context.setWorld(freshWorld);
        context.setPlayer(activePlayer);

        this.mapRenderer = new CustomTiledMapRenderer(context.getWorld().getMap(), GameScreen.UNIT_SCALE);
        this.initMapLayers();

        context.getWorld().clearPendingTransition();
        System.out.println("Transition complete. Player safe position: " + activePlayer.getPosition());
    }

    private void initMapLayers() {
        IntArray groundIndices = new IntArray();
        IntArray foregroundIndices = new IntArray();
        GameWorld activeWorld = context.getWorld();

        for (int i = 0; i < activeWorld.getMap().getLayers().getCount(); i++) {
            String name = activeWorld.getMap().getLayers().get(i).getName().trim();
            if ("Background".equalsIgnoreCase(name) || "Ground".equalsIgnoreCase(name) || "Grass".equalsIgnoreCase(name)) {
                groundIndices.add(i);
            } else {
                foregroundIndices.add(i);
            }
        }
        this.groundLayers = groundIndices.toArray();
        this.foregroundLayers = foregroundIndices.toArray();
    }

    @Override
    public void show() {

    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        mapRenderer.dispose();
        if (context.getWorld() != null) context.getWorld().dispose();
        ParticleTestHub.dispose();
        if (assetHub != null) assetHub.dispose();
        mapCacheManager.disposeAll();
    }
}
