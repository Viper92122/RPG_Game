package io.github.echoesofnebula.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.github.echoesofnebula.items.ConsumableItem;
import io.github.echoesofnebula.items.Item;
import io.github.echoesofnebula.items.ItemEffect;
import io.github.echoesofnebula.view.GameScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
/**
 * Central factory for creating all items in the game.
 * Add new items here as your game grows!
 */
public class ItemFactory {

    private final AssetHub assetHub;

    // Default render size (in pixels) - adjust per item if needed
    private static final float DEFAULT_RENDER_SIZE_PX = 5f;

    public ItemFactory(AssetHub assetHub) {
        this.assetHub = assetHub;
    }

    public Item createItem(String itemName, float worldX, float worldY) {
        System.out.println("🏭 ItemFactory creating: '" + itemName + "'");

        float renderW = DEFAULT_RENDER_SIZE_PX * GameScreen.UNIT_SCALE;
        float renderH = DEFAULT_RENDER_SIZE_PX * GameScreen.UNIT_SCALE;

        System.out.println("   Render size: " + renderW + "x" + renderH + " world units");

        switch (itemName.toLowerCase()) {
            case "health_potion":
            case "healingitem":
                return createHealthPotion(worldX, worldY, 5, 5);

            default:
                System.err.println("❌ Unknown item type: " + itemName);
                return null;
        }
    }

    private ConsumableItem createHealthPotion(float x, float y, float w, float h) {
        System.out.println("   Fetching texture: 'healthPotion'");
        TextureRegion tex = assetHub.getRegion("healthPotion");

        if (tex == null) {
            System.err.println("❌ ERROR: Texture 'healthPotion' not found in AssetHub!");
            return null;
        }

        ItemEffect effect = ItemEffectRegistry.createEffect("HEAL", 50f);

        float customRenderW = 6f * GameScreen.UNIT_SCALE; // Adjust this value!
        float customRenderH = 6f * GameScreen.UNIT_SCALE; // Adjust this value!

        int frameWidth = 24;
        int frameHeight = 39;

        boolean isAnimationStrip = tex.getRegionWidth() > frameWidth;

        if (isAnimationStrip) {
            System.out.println("   🎞️ Detected animation strip! Slicing into frames...");

            int totalFrames = tex.getRegionWidth() / frameWidth;

            Array<TextureRegion> frames = new Array<>();
            for (int i = 0; i < totalFrames; i++) {
                int frameX = tex.getRegionX() + (i * frameWidth);
                int frameY = tex.getRegionY();

                TextureRegion frame = new TextureRegion(
                    tex.getTexture(),
                    frameX,
                    frameY,
                    frameWidth,
                    frameHeight
                );
                frames.add(frame);
            }

            Animation<TextureRegion> anim = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);

            // Use custom render dimensions instead of the passed w/h
            return new ConsumableItem("Health Potion", x, y, customRenderW, customRenderH, effect, anim);

        } else {
            System.out.println("   🖼️ Static texture detected");
            return new ConsumableItem("Health Potion", x, y, customRenderW, customRenderH, effect, tex);
        }
    }

    private ConsumableItem createManaPotion(float x, float y, float w, float h) {
        TextureRegion tex = assetHub.getRegion("manaPotion"); // You'd add this to your atlas
        if (tex == null) return null;
        ItemEffect effect = ItemEffectRegistry.createEffect("RESTORE_MANA", 30f);
        return new ConsumableItem("Mana Potion", x, y, w, h, effect, tex);
    }

    private ConsumableItem createStrengthElixir(float x, float y, float w, float h) {
        TextureRegion tex = assetHub.getRegion("strengthElixir");
        if (tex == null) return null;
        ItemEffect effect = ItemEffectRegistry.createEffect("BUFF_STRENGTH", 25f);
        return new ConsumableItem("Strength Elixir", x, y, w, h, effect, tex);
    }
}
