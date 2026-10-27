package io.github.echoesofnebula.input;

import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.entities.PlayerActionType;


public class PlayerIntent {


    // Movement snapshot
    public final Vector2 movementDirection = new Vector2(0, 0);
    public boolean wantsToRun = false;

    // Action snapshot (Replaces "triggeredAction")
    public PlayerActionType activeAction = PlayerActionType.NONE;

    // Crucial context: Where is the action aiming? (e.g., shooting a spell toward the mouse)
    public final Vector2 actionTargetWorld = new Vector2(0, 0);

    public void clear() {
        movementDirection.set(0, 0);
        wantsToRun = false;
        activeAction = PlayerActionType.NONE;
        actionTargetWorld.set(0, 0);
    }
}
