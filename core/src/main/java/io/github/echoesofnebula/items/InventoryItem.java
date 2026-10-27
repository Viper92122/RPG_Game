package io.github.echoesofnebula.items;

public class InventoryItem {
    private String id;
    private String displayName;
    private int quantity;

    public InventoryItem(String id, String displayName, int quantity) {
        this.id = id;
        this.displayName = displayName;
        this.quantity = quantity;
    }
    public String getId() {
        return id;
    }
    public void increaseQuantity(int count) {
        quantity += count;
    }
}
