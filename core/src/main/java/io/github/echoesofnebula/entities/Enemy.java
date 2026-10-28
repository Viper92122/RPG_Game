package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.echoesofnebula.ai.AIBehavior;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.items.LootTable;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.managers.ParticleSystem;

public class Enemy extends Entity implements  ItemUser {
    private final AIBehavior aiBehavior;
    private final Stats stats;
    private final LootTable lootTable;

    // Animation fields (can be null for simple rectangle testing)
    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> walkAnim;
    private float stateTime = 0f;

    // ✅ NEW CONSTRUCTOR: Matches exactly what EnemyFactory provides
    public Enemy(float startX, float startY, AIBehavior aiBehavior, Stats stats,
                 LootTable lootTable, Animation<TextureRegion> idleAnim, Animation<TextureRegion> walkAnim) {

        this.pos.set(startX, startY);
        this.velocity.set(0, 0);
        this.bounds.set(startX, startY, size, size);

        this.aiBehavior = aiBehavior;
        this.stats = stats;
        this.lootTable = lootTable;
        this.idleAnim = idleAnim;
        this.walkAnim = walkAnim;
    }

    @Override
    public void update(float deltaTime) {
        if (stats.isDead()) return; // Don't process AI for dead enemies

        aiBehavior.update(this, deltaTime);

        // Update animation state time based on movement
        if (velocity.len2() > 0.001f) {
            stateTime += deltaTime;
        } else {
            stateTime = 0f;
        }

        // Keep bounds synced with visual position
        this.bounds.setPosition(this.pos.x, this.pos.y);
    }

    public void takeDamage(int amount) {
        stats.takeDamage(amount);
        System.out.println("Enemy took " + amount + " damage! HP: " + stats.getCurrentHealth());
    }

    public boolean isDead() {
        return stats.isDead();
    }

    public Stats getStats() {
        return stats;
    }

    /**
     * Triggers the loot table and spawns items into the world.
     */
    public void dropLoot(GameWorld world) {
        for (String itemName : lootTable.rollLoot()) {
            world.spawnItemByName(itemName, this.pos.x, this.pos.y);
        }
        System.out.println("Enemy dropped loot at (" + this.pos.x + ", " + this.pos.y + ")");
    }

    public void handleWallImpact() {
        aiBehavior.handleWallImpact(this);
    }

    @Override
    public void acceptCollision(GameWorld world, CollisionEventData event) {
        world.handleEnemyCollision(this, event);
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void drawTextures(SpriteBatch batch) {
        // 1. If animations exist, use them
        if (idleAnim != null || walkAnim != null) {
            Animation<TextureRegion> currentAnim = (velocity.len2() > 0.001f) ? walkAnim : idleAnim;
            if (currentAnim != null) {
                batch.draw(currentAnim.getKeyFrame(stateTime, true), pos.x, pos.y, size, size);
                return;
            }
        }

        // 2. FALLBACK: Draw a simple red rectangle (perfect for testing!)
        batch.setColor(0.8f, 0.2f, 0.2f, 1f); // Reddish color
        batch.draw(AssetHub.getWhitePixel(), pos.x, pos.y, size, size);
        batch.setColor(1, 1, 1, 1); // Reset color
    }

    // --- ItemUser Interface Implementation (Delegates to Stats) ---
    @Override
    public void heal(int amount) {
        stats.setCurrentHealth(stats.getCurrentHealth() + amount);
    }

    @Override
    public void restoreMana(int amount) {
        stats.setCurrentMana(stats.getCurrentMana() + amount);
    }
    @Override
    public void checkState(GameWorld world) {
        if (stats.isDead()) {
            System.out.println("💀 Enemy died! Dropping loot...");
            this.dropLoot(world);
            ParticleSystem.spawn(ParticleSystem.ParticleType.FIRE, this.pos.x, this.pos.y);
            world.removeEntity(this); // Flags itself for removal

        }
    }

    @Override
    public void takeDamage(float amount, Entity source) {
        stats.takeDamage((int) amount);
        System.out.println("Enemy took " + (int)amount + " damage! HP: " + stats.getCurrentHealth());
    }
}
