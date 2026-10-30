package io.github.echoesofnebula.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.echoesofnebula.rendering.AssetHub;

public class MenuScreen implements Screen {
    private final Game game;
    private final AssetHub assetHub;
    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private Skin skin;

    public MenuScreen(Game game, AssetHub assetHub) {
        this.game = game;
        this.assetHub = assetHub;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        shapeRenderer = new ShapeRenderer();

        // Load UI Skin (Place uiskin.json, .atlas, and .png in assets/ui/)
        try {
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        } catch (Exception e) {
            skin = new Skin(); // Fallback to prevent crashes
        }

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Try to get the font we generated in LoadingScreen, or create a fallback
        BitmapFont titleFont = assetHub.getFont("rpg_font");

        if (titleFont == null) {
            // Fallback: Use default LibGDX font scaled up and colored gold
            titleFont = new BitmapFont();
            titleFont.getData().setScale(3f);
            titleFont.setColor(Color.GOLD);
        }

            Label titleLabel = new Label("ECHOES OF NEBULA", new Label.LabelStyle(titleFont, Color.WHITE));
            rootTable.add(titleLabel).padBottom(50f).expandX();
            rootTable.row();

        // 1. Get a valid font for the buttons (reuse the title font logic)
        BitmapFont buttonFont = assetHub.getFont("rpg_font");
        if (buttonFont == null) {
            buttonFont = new BitmapFont();
            buttonFont.getData().setScale(1.2f); // Slightly smaller than title
            buttonFont.setColor(Color.WHITE);
        }

        // 2. Safely get or create the button style
        TextButton.TextButtonStyle buttonStyle;
        if (skin.has("default", TextButton.TextButtonStyle.class)) {
            buttonStyle = skin.get("default", TextButton.TextButtonStyle.class);
            // CRITICAL: Ensure the style from the skin actually has a font
            if (buttonStyle.font == null) {
                buttonStyle.font = buttonFont;
            }
        } else {
            // Fallback: create a style from scratch and MUST assign the font!
            buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = buttonFont;
        }

        // 3. Create the buttons
        TextButton startButton = new TextButton("START ADVENTURE", buttonStyle);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, assetHub));
            }
        });

        rootTable.add(startButton).width(300f).height(60f);
        rootTable.row();
        rootTable.padTop(20f);

        TextButton exitButton = new TextButton("EXIT", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        rootTable.add(exitButton).width(200f).height(50f);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Transition to GameScreen, passing the AssetHub along!
                game.setScreen(new GameScreen(game, assetHub));
            }
        });

        rootTable.add(startButton).width(300f).height(60f);
        rootTable.row();
        rootTable.padTop(20f);

        exitButton.getLabel().setColor(Color.LIGHT_GRAY);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        rootTable.add(exitButton).width(200f).height(50f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Moody gradient background (Nebula Purple to Deep Black)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
            new Color(0.2f, 0.1f, 0.3f, 1f),
            new Color(0.2f, 0.1f, 0.3f, 1f),
            new Color(0.05f, 0.05f, 0.1f, 1f),
            new Color(0.05f, 0.05f, 0.1f, 1f)
        );
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        // Do NOT dispose of skin or font here if they are managed by AssetHub!
    }
}
