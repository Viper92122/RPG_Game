package io.github.echoesofnebula.config;

public class GameConfig {
    public String gameTitle;
    public String version;
    public int defaultWidth;
    public int defaultHeight;
    public GameConfig() {
        this.gameTitle = "json didnt load";
        this.version = "1.0.0";
        this.defaultWidth = 240;
        this.defaultHeight = 160;
    }
}
