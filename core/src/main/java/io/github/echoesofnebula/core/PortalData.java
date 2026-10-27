package io.github.echoesofnebula.core;


import com.badlogic.gdx.math.Vector2;

public class PortalData {
    public final String targetMapPath;
    public final Vector2 targetSpawnPosition;

    public PortalData(String targetMapPath, float targetX, float targetY) {
        this.targetMapPath = targetMapPath;
        this.targetSpawnPosition = new Vector2(targetX, targetY);
    }
}
