package io.github.echoesofnebula;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import io.github.echoesofnebula.managers.ConfigManager;
import io.github.echoesofnebula.managers.MasterDataManager;
import io.github.echoesofnebula.view.GameScreen;

public class App extends ApplicationAdapter {

    private GameScreen gameScreen;

    @Override
    public void create() {
        MasterDataManager.loadAllRegistries();

        Gdx.graphics.setTitle(ConfigManager.get().gameTitle);

        gameScreen = new GameScreen();
    }

    @Override
    public void render() {
        if (gameScreen != null) {
            gameScreen.render();
        }
        Gdx.graphics.setTitle(ConfigManager.get().gameTitle + " " + Gdx.graphics.getFramesPerSecond());
    }

    @Override
    public void dispose() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
    }
}
