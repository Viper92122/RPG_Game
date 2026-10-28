package io.github.echoesofnebula.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.entities.Entity;

public class ConsumableItem extends Item {
    private final ItemEffect effect;

    public ConsumableItem(String itemName, float startX, float startY, float width, float height,
                          ItemEffect effect, TextureRegion texture, Animation<TextureRegion> animation,
                          ItemBlueprint blueprint) {
        super(itemName, startX, startY, width, height,blueprint, texture, animation);
        this.effect = effect;
    }

    @Override
    public void onCollectedBy(Entity collector, GameWorld world) {
        collector.applyConsumableEffect(this.effect);
        System.out.println("✨ Used consumable: " + getItemName());
        world.removeEntity(this);
    }
}
