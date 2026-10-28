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
                stats = new Stats(20, 0, 2.0f, 3);
                lootTable.addLoot("healthPotion", 1f, 1, 1);
                // ✅ Now you can easily add animations when you have the assets!
                // idleAnim = assetHub.getAnimation("slime_idle", 0.15f);
                // walkAnim = assetHub.getAnimation("slime_walk", 0.1f);
                break;

            case "boss":
                stats = new Stats(100, 0, 1.5f, 10);
                lootTable.addLoot("healthPotion", 0.5f, 1, 1);
                // idleAnim = assetHub.getAnimation("boss_idle", 0.2f);
                // walkAnim = assetHub.getAnimation("boss_walk", 0.12f);
                break;

            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }

        return new Enemy(x, y, aiBehavior, stats, lootTable, idleAnim, walkAnim);
    }
}
