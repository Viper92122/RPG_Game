package io.github.echoesofnebula.input;

import io.github.echoesofnebula.core.GameContext;
import io.github.echoesofnebula.entities.Player;

public class PlayerController {
    private final GameContext context;
    private final PlayerInputProcessor inputProcessor;

    public PlayerController(GameContext context, PlayerInputProcessor inputProcessor) {
        this.context = context;
        this.inputProcessor = inputProcessor;
    }

    public void tickInput() {
        PlayerIntent intent = inputProcessor.readInput();

        // Dynamically fetch the valid player pointer for the current active frame loop
        Player activePlayer = context.getPlayer();
        if (activePlayer != null) {
            activePlayer.setIntent(intent);
        }
    }
}
