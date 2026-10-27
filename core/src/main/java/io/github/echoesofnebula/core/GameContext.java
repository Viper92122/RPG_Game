package io.github.echoesofnebula.core;

import io.github.echoesofnebula.entities.Player;

public class GameContext {
    private Player currentPlayer;
    private GameWorld currentWorld;

    public Player getPlayer() {
        return currentPlayer;
    }
    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }

    public GameWorld getWorld() {
        return currentWorld;
    }
    public void setWorld(GameWorld world) {
        this.currentWorld = world;
    }
}
