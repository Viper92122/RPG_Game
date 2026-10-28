package io.github.echoesofnebula.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.managers.ItemEffectFactory;
import io.github.echoesofnebula.managers.ItemRegistry;
import io.github.echoesofnebula.view.GameScreen;

public class ItemFactory {
    private final AssetHub assetHub;
    private final ItemRegistry itemRegistry;
    private final ItemEffectFactory effectFactory;

    public ItemFactory(AssetHub assetHub, ItemRegistry itemRegistry, ItemEffectFactory effectFactory) {
        this.assetHub = assetHub;
        this.itemRegistry = itemRegistry;
        this.effectFactory = effectFactory;
    }

    public Item createItem(String itemName, float worldX, float worldY) {
        // 1. Look up blueprint
        ItemBlueprint blueprint = itemRegistry.get(itemName);
        if (blueprint == null) {
            System.err.println("❌ Unknown item type: " + itemName);
            return null;
        }

        // 2. Convert pixel sizes to world units
        float renderW = blueprint.renderWidth * GameScreen.UNIT_SCALE;
        float renderH = blueprint.renderHeight * GameScreen.UNIT_SCALE;

        // 3. Resolve visuals cleanly (NO instanceof, NO Object casting)
        Animation<TextureRegion> anim = null;
        TextureRegion tex = null;

        if (blueprint.isAnimated) {
            anim = assetHub.getAnimation(blueprint.atlasKey, blueprint.frameDuration);
        }

        // If it's not animated, or animation failed to load, fall back to static texture
        if (anim == null) {
            tex = assetHub.getRegion(blueprint.atlasKey);
            if (tex == null) {
                System.err.println("❌ Could not find visual for: " + blueprint.atlasKey);
                return null;
            }
        }

        // 4. Create the item (Only 2 branches now: Consumable vs Regular)
        if (blueprint.isConsumable) {
            ItemEffect effect = effectFactory.createEffect(blueprint.effectType, blueprint.intensity);
            return new ConsumableItem(blueprint.displayName, worldX, worldY, renderW, renderH,
                effect, tex, anim, blueprint);
        } else {
            return new Item(blueprint.displayName, worldX, worldY, renderW, renderH,
                blueprint,tex, anim);
        }
    }
}
