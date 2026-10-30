package io.github.echoesofnebula.items;


import io.github.echoesofnebula.core.BaseRegistry;

public class ItemRegistry extends BaseRegistry<ItemBlueprint> {
    public ItemRegistry() {
        super("items/items.json", ItemBlueprint.class);
    }
}
