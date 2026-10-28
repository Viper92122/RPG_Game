package io.github.echoesofnebula.managers;


import io.github.echoesofnebula.items.ItemBlueprint;

public class ItemRegistry extends BaseRegistry<ItemBlueprint> {
    public ItemRegistry() {
        super("items/items.json", ItemBlueprint.class);
    }
}
