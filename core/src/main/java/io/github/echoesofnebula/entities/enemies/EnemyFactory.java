package io.github.echoesofnebula.entities.enemies;

import io.github.echoesofnebula.ai.AIConfiguration;
import io.github.echoesofnebula.entities.CharacterStats;
import io.github.echoesofnebula.items.loot.LootTable;
import io.github.echoesofnebula.rendering.AssetHub;

public class EnemyFactory {
    private final AssetHub assetHub;

    public EnemyFactory(AssetHub assetHub) {
        this.assetHub = assetHub;
    }

    public Enemy createEnemy(String enemyType, float x, float y) {
        CharacterStats characterStats;
        LootTable lootTable = new LootTable();
        AIConfiguration aiConfig;

        switch (enemyType.toLowerCase()) {
            case "test_slime":
                characterStats = new CharacterStats(20, 0, 2.0f, 3, 10);
                lootTable.addLoot("healthPotion", 1f, 1, 1);
                aiConfig = AIConfiguration.standardEnemy(5f, 1.5f);  // ✅ Just config
                break;

            case "orc":
                characterStats = new CharacterStats(20, 0, 2.0f, 3, 10);
                lootTable.addLoot("healthPotion", 0.7f, 1, 2);
                aiConfig = AIConfiguration.standardEnemy(6f, 2f);
                break;

            case "boss":
                characterStats = new CharacterStats(20, 0, 2.0f, 3, 10);
                lootTable.addLoot("healthPotion", 0.5f, 1, 1);
                aiConfig = AIConfiguration.boss(10f, 3f);
                break;

            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }

        return new Enemy(x, y, characterStats, lootTable, aiConfig, null,null);
    }
}
