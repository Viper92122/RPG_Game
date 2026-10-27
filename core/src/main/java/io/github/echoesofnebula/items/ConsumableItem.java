package io.github.echoesofnebula.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.echoesofnebula.entities.ItemUser;

public class ConsumableItem extends Item {
    private final ItemEffect effect;

    // Static Single Sprite Construction
    public ConsumableItem(String itemName, float x, float y, float width, float height, ItemEffect effect, TextureRegion texture) {
        super(itemName, x, y, width, height, texture); // Calls Texture constructor
        this.effect = effect;
    }

    // Animated Strip Construction
    public ConsumableItem(String itemName, float x, float y, float width, float height, ItemEffect effect, Animation<TextureRegion> animation) {
        super(itemName, x, y, width, height, animation); // 🌟 FIX: Must call Animation constructor!
        this.effect = effect;
    }

    public void use(ItemUser target) {
        if (effect != null) {
            effect.apply(target);
        }
    }
}
