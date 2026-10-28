package io.github.echoesofnebula;

import com.badlogic.gdx.Game;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.view.LoadingScreen;

public class App extends Game {
    private AssetHub assetHub;

    @Override
    public void create() {
        assetHub = new AssetHub();
        assetHub.queueInitialAssets(); // Replace with your actual loading method


        setScreen(new LoadingScreen(this, assetHub));
    }

    @Override
    public void dispose() {
        if (assetHub != null) assetHub.dispose();
        super.dispose();
    }
}
