package io.github.echoesofnebula.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.echoesofnebula.core.GameContext;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.core.PortalData;
import io.github.echoesofnebula.entities.Player;
import io.github.echoesofnebula.input.PlayerController;
import io.github.echoesofnebula.input.PlayerInputProcessor;
import io.github.echoesofnebula.input.PlayerIntent;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.managers.CameraManager;
import io.github.echoesofnebula.managers.MapCacheManager;
import io.github.echoesofnebula.managers.ParticleTestHub;

public class GameScreen implements Screen {
    private final Game game; // ADDED: Reference to the main App class
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

    // --- PAUSE MENU FIELDS ---
    private boolean isPaused = false;
    private Stage pauseStage;
    private Skin pauseSkin;

    public static final float UNIT_SCALE = 1f / 16f;

    public GameScreen(Game game, AssetHub assetHub) {
        this.game = game; // Save the reference
        this.assetHub = assetHub;

        context = new GameContext();
        mapCacheManager = new MapCacheManager(assetHub);

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

    @Override
    public void show() {
        // --- PAUSE MENU INITIALIZATION ---
        pauseStage = new Stage(new ScreenViewport());
        try {
            pauseSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        } catch (Exception e) {
            pauseSkin = new Skin(); // Safe fallback
        }

        Table pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseStage.addActor(pauseTable);

        // Safe Font Setup
        BitmapFont buttonFont = assetHub.getFont("rpg_font");
        if (buttonFont == null) {
            buttonFont = new BitmapFont();
            buttonFont.getData().setScale(1.5f);
            buttonFont.setColor(Color.WHITE);
        }

        TextButton.TextButtonStyle buttonStyle;
        if (pauseSkin.has("default", TextButton.TextButtonStyle.class)) {
            buttonStyle = pauseSkin.get("default", TextButton.TextButtonStyle.class);
            if (buttonStyle.font == null) buttonStyle.font = buttonFont;
        } else {
            buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = buttonFont;
        }

        // Resume Button
        TextButton resumeButton = new TextButton("RESUME", buttonStyle);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = false; // Instantly unpauses on next frame
            }
        });
        pauseTable.add(resumeButton).width(250f).height(50f);
        pauseTable.row();
        pauseTable.padTop(20f);

        // Main Menu Button
        TextButton mainMenuButton = new TextButton("MAIN MENU", buttonStyle);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose(); // Clean up GameScreen resources
                // Transition back to MenuScreen (AssetHub is kept alive!)
                game.setScreen(new MenuScreen(game, assetHub));
            }
        });
        pauseTable.add(mainMenuButton).width(250f).height(50f);
    }

    @Override
    public void render(float delta) {
        // 1. CHECK FOR PAUSE TOGGLE (Works globally, bypasses player input)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        // 2. PAUSED STATE: Freeze everything and draw overlay
        if (isPaused) {
            ScreenUtils.clear(0, 0, 0, 1f);

            // Draw the game world exactly as it was (frozen in time)
            mapRenderer.setView(camera);
            mapRenderer.render(groundLayers);

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            context.getWorld().drawTextures(batch);
            ParticleTestHub.updateAndDraw(Gdx.graphics.getDeltaTime(), batch);
            batch.end();

            mapRenderer.render(foregroundLayers);

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            context.getWorld().draw(shapeRenderer);
            shapeRenderer.end();

            // Draw semi-transparent black overlay (75% opaque) over the whole screen
            shapeRenderer.setProjectionMatrix(pauseStage.getViewport().getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();

            // Draw the pause menu UI on top
            pauseStage.act(delta);
            pauseStage.draw();

            return; // CRITICAL: Stop here! Do not update game logic while paused.
        }

        // 3. NORMAL GAME LOOP (Only runs if isPaused == false)
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
    public void resize(int width, int height) {
        // Leave the game camera alone so it stays at your 15x10 zoom level!

        // Only update the Pause UI so the buttons stretch correctly if you resize the window
        if (pauseStage != null) {
            pauseStage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        mapRenderer.dispose();
        if (context.getWorld() != null) context.getWorld().dispose();
        ParticleTestHub.dispose();
        if (pauseStage != null) pauseStage.dispose();
        if (pauseSkin != null) pauseSkin.dispose();
        mapCacheManager.disposeAll();

        // ⚠️ CRITICAL FIX: Do NOT dispose of assetHub here!
        // If you return to the Main Menu, it still needs the fonts and UI assets.
        // if (assetHub != null) assetHub.dispose();
    }
}
