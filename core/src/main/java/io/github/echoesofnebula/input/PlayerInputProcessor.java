package io.github.echoesofnebula.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import io.github.echoesofnebula.entities.PlayerActionType;

public class PlayerInputProcessor {

    private final PlayerIntent currentIntent = new PlayerIntent();

    public PlayerIntent readInput() {
        currentIntent.clear();

        // --- 1. HANDLE MOVEMENT ---
        float moveX = 0;
        float moveY = 0;

        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))  moveX = -1;
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) moveX = 1;
        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))    moveY = 1;
        if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))  moveY = -1;

        if (moveX != 0 || moveY != 0) {
            currentIntent.movementDirection.set(moveX, moveY).nor();
        }
        currentIntent.wantsToRun = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT);

        // --- 2. HANDLE ACTIONS & SPELLS ---
        // Just pressed logic handles single-action activation cleanly
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.E)) {
            currentIntent.activeAction = PlayerActionType.INTERACT;
        } else if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
            currentIntent.activeAction = PlayerActionType.MELEE_ATTACK;
        } else if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
            currentIntent.activeAction = PlayerActionType.CAST_SPELL;
        }

        // --- 3. CAPTURE TARGET CONTEXT (Optional but highly useful) ---
        // If your game requires aiming spells/weapons with the mouse
        // You would unproject this screen coordinate to world coordinates in your controller layer
        currentIntent.actionTargetWorld.set(Gdx.input.getX(), Gdx.input.getY());

        return currentIntent;
    }
}
