package io.github.echoesofnebula.items.inventory;

import com.badlogic.gdx.utils.Array;
import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.items.ItemBlueprint;
import io.github.echoesofnebula.items.effects.ItemEffect;
import io.github.echoesofnebula.items.effects.ItemEffectFactory;
public class Inventory {
    private final Array<InventoryItem> items = new Array<>();
    private final ItemEffectFactory effectFactory;

    public Inventory(ItemEffectFactory effectFactory) {
        this.effectFactory = effectFactory;
    }


    public void addItem(ItemBlueprint blueprint, int quantity) {
        for (InventoryItem invItem : items) {
            if (invItem.getItemName().equals(blueprint.atlasKey)) {
                invItem.addQuantity(quantity);
                System.out.println("📦 Added " + quantity + "x " + blueprint.displayName +
                    " (Total: " + invItem.getQuantity() + ")");
                return;
            }
        }
        InventoryItem newItem = new InventoryItem(blueprint, quantity);
        items.add(newItem);
        System.out.println("📦 Added " + quantity + "x " + blueprint.displayName + " (new entry)");
    }

    /**
     * Removes an item from the inventory.
     * Returns true if successful, false if not enough quantity.
     */
    public boolean removeItem(String itemName, int quantity) {
        for (int i = 0; i < items.size; i++) {
            InventoryItem invItem = items.get(i);
            if (invItem.getItemName().equals(itemName)) {
                if (invItem.getQuantity() >= quantity) {
                    invItem.removeQuantity(quantity);

                    // Remove entry if quantity hits 0
                    if (invItem.getQuantity() == 0) {
                        items.removeIndex(i);
                    }

                    System.out.println("🗑️ Removed " + quantity + "x " + invItem.getDisplayName());
                    return true;
                } else {
                    System.err.println("❌ Not enough " + itemName + " (have " +
                        invItem.getQuantity() + ", need " + quantity + ")");
                    return false;
                }
            }
        }
        System.err.println("❌ Item not found in inventory: " + itemName);
        return false;
    }

    public boolean useItem(String itemName, ItemUser user) {
        for (int i = 0; i < items.size; i++) {
            InventoryItem invItem = items.get(i);
            if (invItem.getItemName().equals(itemName)) {
                ItemBlueprint blueprint = invItem.getBlueprint();

                if (!blueprint.isConsumable) {
                    System.err.println("❌ Cannot use non-consumable item: " + itemName);
                    return false;
                }

                ItemEffect effect = effectFactory.createEffect(blueprint.effectType, blueprint.intensity);

                effect.apply(user);

                invItem.removeQuantity(1);
                if (invItem.getQuantity() == 0) {
                    items.removeIndex(i);
                }

                System.out.println("✨ Used " + blueprint.displayName + " (Remaining: " +
                    invItem.getQuantity() + ")");
                return true;
            }
        }
        System.err.println("❌ Item not found in inventory: " + itemName);
        return false;
    }

    /**
     * Check if player has a specific item.
     */
    public boolean hasItem(String itemName, int quantity) {
        for (InventoryItem invItem : items) {
            if (invItem.getItemName().equals(itemName)) {
                return invItem.getQuantity() >= quantity;
            }
        }
        return false;
    }


    public int getQuantity(String itemName) {
        for (InventoryItem invItem : items) {
            if (invItem.getItemName().equals(itemName)) {
                return invItem.getQuantity();
            }
        }
        return 0;
    }

    public Array<InventoryItem> getAllItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Inventory:\n");
        if (items.isEmpty()) {
            sb.append("  (Empty)\n");
        } else {
            for (InventoryItem item : items) {
                sb.append("  - ").append(item.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
