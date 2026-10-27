package io.github.echoesofnebula.items;

import com.badlogic.gdx.utils.Array;

public class Inventory {
    private Array<InventoryItem> items = new Array<>();

    public void addItem(String id, String name, int count) {
        // Optional: Check if item already exists to stack it
        for (InventoryItem item : items) {
            if (item.getId().equals(id)) {
                item.increaseQuantity(count);
                return;
            }
        }
        items.add(new InventoryItem(id, name, count));
    }

    public Array<InventoryItem> getItems() {
        return items;
    }
}
