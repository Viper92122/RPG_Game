package io.github.echoesofnebula.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.echoesofnebula.ai.AIBehavior;
import io.github.echoesofnebula.entities.Enemy;
import io.github.echoesofnebula.entities.Stats;
import io.github.echoesofnebula.items.LootTable;

public class EnemyFactory {
    private final AssetHub assetHub;

    public EnemyFactory(AssetHub assetHub) {
        this.assetHub = assetHub;
    }

    public Enemy createEnemy(String enemyType, float x, float y, AIBehavior aiBehavior) {
        Stats stats;
        LootTable lootTable = new LootTable();
        Animation<TextureRegion> idleAnim = null;
        Animation<TextureRegion> walkAnim = null;

        switch (enemyType.toLowerCase()) {
            case "test_slime":
                // Simple rectangle enemy for testing
                stats = new Stats(20, 0, 2.0f, 3); // 20 HP, 0 Mana, Speed 2, Damage 3
                lootTable.addLoot("healthPotion", 1f, 1, 1); // 50% chance for 1 potion
                // No animations - will use rectangle fallback
                break;
            case "boss":
                // Simple rectangle enemy for testing
                stats = new Stats(20, 0, 2.0f, 3); // 20 HP, 0 Mana, Speed 2, Damage 3
                lootTable.addLoot("healthPotion", 0.5f, 1, 1); // 50% chance for 1 potion
                // No animations - will use rectangle fallback
                break;
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }

        return new Enemy(x, y, aiBehavior, stats, lootTable, idleAnim, walkAnim);
    }
}
