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
import io.github.echoesofnebula.entities.Enemy;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.entities.Player;
import io.github.echoesofnebula.input.PlayerController;
import io.github.echoesofnebula.input.PlayerInputProcessor;
import io.github.echoesofnebula.input.PlayerIntent;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.managers.CameraManager;
import io.github.echoesofnebula.managers.DialogRegistry;
import io.github.echoesofnebula.managers.ItemEffectFactory;
import io.github.echoesofnebula.managers.ItemRegistry;
import io.github.echoesofnebula.managers.ParticleSystem;
import io.github.echoesofnebula.managers.SpellRegistry;
import io.github.echoesofnebula.managers.WorldCache;

public class GameScreen implements Screen {
    private final Game game;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final AssetHub assetHub;
    private WorldCache worldCache;
    private final GameContext context;
    private CustomTiledMapRenderer mapRenderer;
    private int[] groundLayers;
    private int[] foregroundLayers;
    private final PlayerController playerController;
    private final CameraManager cameraManager;
    private boolean isPaused = false;
    private Stage pauseStage;
    private Skin pauseSkin;
    public static final float UNIT_SCALE = 1f / 16f;

    public GameScreen(Game game, AssetHub assetHub) {
        this.game = game; // FIX: Save the reference!
        this.assetHub = assetHub;

        // 1. Initialize Core Rendering & Camera FIRST
        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, 15.0f, 10.0f);

        DialogRegistry dialogs = new DialogRegistry();
        dialogs.initialize();
        SpellRegistry spells = new SpellRegistry();
        spells.initialize();
        ItemRegistry items = new ItemRegistry(); // ✅ NEW
        items.initialize();
        ItemEffectFactory effects = new ItemEffectFactory(); // ✅ NEW
        this.context = new GameContext(dialogs, spells, items, effects);

        this.worldCache = new WorldCache(assetHub);
        GameWorld world = worldCache.getOrCreateWorld("tilemap_first.tmx", context.getItems(), context.getEffectFactory());
        Vector2 spawnPoint = world.findSpawnPointFromMap();
        world.spawnPlayer(assetHub, spawnPoint.x, spawnPoint.y, effects);

        context.setWorld(world);
        context.setPlayer(world.getPlayer());

        this.mapRenderer = new CustomTiledMapRenderer(context.getWorld().getMap(), UNIT_SCALE);
        this.initMapLayers();

        this.playerController = new PlayerController(context, new PlayerInputProcessor());
        this.cameraManager = new CameraManager(this.camera);
        ParticleSystem.initialize(GameScreen.UNIT_SCALE);

        world.spawnTestEnemy(spawnPoint.x + 3f, spawnPoint.y);
        world.spawnTestEnemy(spawnPoint.x - 2f, spawnPoint.y + 2f);
    }

    @Override
    public void show() {
        pauseStage = new Stage(new ScreenViewport());
        try {
            pauseSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        } catch (Exception e) {
            pauseSkin = new Skin();
        }

        Table pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseStage.addActor(pauseTable);

        BitmapFont buttonFont = assetHub.getFont("rpg_font");
        if (buttonFont == null) {
            buttonFont = new BitmapFont();
            buttonFont.getData().setScale(1.5f);
            buttonFont.setColor(Color.WHITE);
        }

        TextButton.TextButtonStyle buttonStyle = pauseSkin.has("default", TextButton.TextButtonStyle.class)
            ? pauseSkin.get("default", TextButton.TextButtonStyle.class)
            : new TextButton.TextButtonStyle();
        if (buttonStyle.font == null) buttonStyle.font = buttonFont;

        TextButton resumeButton = new TextButton("RESUME", buttonStyle);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isPaused = false;
            }
        });
        pauseTable.add(resumeButton).width(250f).height(50f);
        pauseTable.row().padTop(20f);

        TextButton mainMenuButton = new TextButton("MAIN MENU", buttonStyle);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                game.setScreen(new MenuScreen(game, assetHub));
            }
        });
        pauseTable.add(mainMenuButton).width(250f).height(50f);
    }

    private void onPaused(float deltaTime){
        ScreenUtils.clear(0, 0, 0, 1f);

        mapRenderer.setView(camera);
        mapRenderer.render(groundLayers);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        context.getWorld().drawTextures(batch);
        ParticleSystem.updateAndDraw(Gdx.graphics.getDeltaTime(), batch);
        batch.end();

        mapRenderer.render(foregroundLayers);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        context.getWorld().draw(shapeRenderer);
        shapeRenderer.end();

        shapeRenderer.setProjectionMatrix(pauseStage.getViewport().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        pauseStage.act(deltaTime);
        pauseStage.draw();
    }

    @Override
    public void render(float deltaTime) {
        // 1. CHECK FOR PAUSE TOGGLE
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        // 2. PAUSED STATE
        if (isPaused) {
            onPaused(deltaTime);
            return;
        }

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
        ParticleSystem.updateAndDraw(Gdx.graphics.getDeltaTime(), batch);
        batch.end();

        mapRenderer.render(foregroundLayers);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        activeWorld.draw(shapeRenderer);
        shapeRenderer.end();

        // DEBUG: Press 'H' to damage nearby enemies to test the loot drop
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            Player p = context.getPlayer();
            if (p != null) {
                for (Entity entity : activeWorld.getPhysicsEntities()) {
                    if (entity instanceof Enemy) {
                        Enemy enemy = (Enemy) entity;
                        if (enemy.getPosition().dst(p.getPosition()) < 2f) {
                            enemy.takeDamage(30); // Instakill the 30 HP test enemy
                        }
                    }
                }
            }
        }
    }

    private void changeActiveMap(PortalData transition) {
        GameWorld oldWorld = context.getWorld();
        Player activePlayer = context.getPlayer();

        if (activePlayer != null) {
            activePlayer.setIntent(new PlayerIntent());
            // oldWorld.detachEntity(activePlayer);
        }

        GameWorld targetWorld = worldCache.getOrCreateWorld(transition.targetMapPath, context.getItems(), context.getEffectFactory());
        activePlayer.getPosition().set(transition.targetSpawnPosition);
        activePlayer.getBounds().setPosition(activePlayer.getPosition().x, activePlayer.getPosition().y);
        targetWorld.addDynamicEntity(activePlayer);

        context.setWorld(targetWorld);
        context.setPlayer(activePlayer);

        this.mapRenderer.dispose();
        this.mapRenderer = new CustomTiledMapRenderer(targetWorld.getMap(), GameScreen.UNIT_SCALE);
        this.initMapLayers();

        targetWorld.clearPendingTransition();
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
        if (pauseStage != null) {
            pauseStage.getViewport().update(width, height, true);
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (pauseStage != null) pauseStage.dispose();
        if (pauseSkin != null) pauseSkin.dispose();
        ParticleSystem.dispose();
        if (worldCache != null) worldCache.disposeAll();

        // CRITICAL FIX: DO NOT dispose of assetHub here! The MenuScreen still needs it.
    }
}
