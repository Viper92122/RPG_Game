package io.github.echoesofnebula.items;

/**
 * Represents a stackable item in the inventory.
 * Tracks the item blueprint (what it is) and quantity (how many).
 */
public class InventoryItem {
    private final ItemBlueprint blueprint;
    private int quantity;

    public InventoryItem(ItemBlueprint blueprint, int quantity) {
        this.blueprint = blueprint;
        this.quantity = quantity;
    }

    public ItemBlueprint getBlueprint() {
        return blueprint;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public void removeQuantity(int amount) {
        this.quantity = Math.max(0, this.quantity - amount);
    }

    public String getDisplayName() {
        return blueprint.displayName;
    }

    public String getItemName() {
        return blueprint.atlasKey; // Use atlasKey as unique identifier
    }

    /**
     * Check if this inventory item can stack with another.
     */
    public boolean canStackWith(InventoryItem other) {
        return this.getItemName().equals(other.getItemName());
    }

    @Override
    public String toString() {
        return getDisplayName() + " x" + quantity;
    }
}
