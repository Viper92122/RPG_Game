package io.github.echoesofnebula.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.echoesofnebula.managers.AssetHub;

public class LoadingScreen implements Screen {
    private final Game game;
    private final AssetHub assetHub;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    public LoadingScreen(Game game, AssetHub assetHub) {
        this.game = game;
        this.assetHub = assetHub;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        // GUARANTEED FALLBACK: Uses LibGDX's built-in default font
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);
        this.font.setColor(Color.GOLD);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        boolean finished = assetHub.update();
        float progress = assetHub.getProgress();

        batch.begin();
        font.draw(batch, "Preparing the Realm...", 20, Gdx.graphics.getHeight() - 30);

        float barWidth = 400f;
        float barHeight = 20f;
        float x = (Gdx.graphics.getWidth() - barWidth) / 2f;
        float y = (Gdx.graphics.getHeight() - 50f) / 2f;

        // Draw Progress Bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.25f, 1f); // Background
        shapeRenderer.rect(x, y, barWidth, barHeight);

        shapeRenderer.setColor(Color.GOLD); // Fill
        shapeRenderer.rect(x, y, barWidth * progress, barHeight);

        shapeRenderer.setColor(Color.WHITE); // Border
        shapeRenderer.rectLine(x, y, x + barWidth, y, 2f);
        shapeRenderer.rectLine(x, y + barHeight, x + barWidth, y + barHeight, 2f);
        shapeRenderer.rectLine(x, y, x, y + barHeight, 2f);
        shapeRenderer.rectLine(x + barWidth, y, x + barWidth, y + barHeight, 2f);
        shapeRenderer.end();

        // Draw Percentage
        font.getData().setScale(1.2f);
        font.draw(batch, (int)(progress * 100) + "%", x + (barWidth / 2f) - 15f, y + barHeight + 25f);
        batch.end();

        if (finished) {
            game.setScreen(new MenuScreen(game, assetHub));
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}
