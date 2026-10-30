package io.github.echoesofnebula.items.loot;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class LootTable {
    public static class LootEntry {
        public String itemName;
        public float dropChance;
        public int minAmount;
        public int maxAmount;

        public LootEntry(String itemName, float dropChance, int minAmount, int maxAmount) {
            this.itemName = itemName;
            this.dropChance = dropChance;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
        }
    }
    private final Array<LootEntry> entries = new Array<>();

    public void addLoot(String itemName, float dropChance, int minAmount, int maxAmount) {
        entries.add(new LootEntry(itemName, dropChance, minAmount, maxAmount));
    }

    public Array<String> rollLoot() {
        Array<String> droppedItems = new Array<>();
        for (LootEntry entry : entries) {
            if (MathUtils.random() <= entry.dropChance) {
                int amount = MathUtils.random(entry.minAmount, entry.maxAmount);
                for (int i = 0; i < amount; i++) {
                    droppedItems.add(entry.itemName);
                }
            }
        }
        return droppedItems;
    }
}
