package io.github.echoesofnebula.rendering;


import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.echoesofnebula.core.GameContext;
import io.github.echoesofnebula.entities.Player;

public class CameraManager {
    private final OrthographicCamera camera;

    public CameraManager(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void updateTracking(GameContext context) {
        Player player = context.getPlayer();
        if (player == null || context.getWorld() == null) return;

        // Trace positions using pure world meters directly!
        float targetCamX = player.getPosition().x + (player.getSize() / 2f);
        float targetCamY = player.getPosition().y + (player.getSize() / 2f);

        // Snap the camera coordinates directly onto the player
        camera.position.set(targetCamX, targetCamY, 0);
        camera.update();
    }
}
