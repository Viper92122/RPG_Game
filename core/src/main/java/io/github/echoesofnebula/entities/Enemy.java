package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.echoesofnebula.ai.AIBehavior;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.core.SpriteRenderable;
import io.github.echoesofnebula.core.UpdateAble;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.items.LootTable;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.view.GameScreen;

public class Enemy extends Entity implements UpdateAble, SpriteRenderable, ItemUser {
    private final AIBehavior aiBehavior;
    private final Stats stats;
    private final LootTable lootTable;

    // The Enemy only holds the ready-to-use animations!
    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> walkAnim;
    private float stateTime = 0f;

    // NO AssetHub here! Just the data it needs to function.
    public Enemy(float startX, float startY, AIBehavior aiBehavior, Stats stats,
                 LootTable lootTable, Animation<TextureRegion> idleAnim, Animation<TextureRegion> walkAnim) {
        this.pos.set(startX, startY); // Coordinates should ALREADY be scaled by the caller
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
        if (stats.isDead()) return;

        aiBehavior.update(this, deltaTime);

        if (velocity.len2() > 0.001f) {
            stateTime += deltaTime;
        } else {
            stateTime = 0f;
        }

        this.bounds.setPosition(this.pos.x, this.pos.y);
    }

    public void takeDamage(int amount) {
        stats.takeDamage(amount);
    }

    public void dropLoot(GameWorld world) {
        for (String itemName : lootTable.rollLoot()) {
            world.spawnItemByName(itemName, this.pos.x, this.pos.y);
        }
    }

    public boolean isDead() { return stats.isDead(); }
    public Stats getStats() { return stats; }
    public void handleWallImpact() { aiBehavior.handleWallImpact(this); }

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
        // If animations exist, use them
        if (idleAnim != null || walkAnim != null) {
            Animation<TextureRegion> currentAnim = (velocity.len2() > 0.001f) ? walkAnim : idleAnim;
            if (currentAnim != null) {
                batch.draw(currentAnim.getKeyFrame(stateTime, true), pos.x, pos.y, size, size);
                return;
            }
        }

        // FALLBACK: Draw a simple colored rectangle (perfect for testing!)
        // We'll use a white 1x1 pixel texture and tint it
        batch.setColor(0.8f, 0.2f, 0.2f, 1f); // Reddish color
        batch.draw(AssetHub.getWhitePixel(), pos.x, pos.y, size, size);
        batch.setColor(1, 1, 1, 1); // Reset color
    }

    // ItemUser methods now actually modify the Stats component!
    @Override public void heal(int amount) { stats.setCurrentHealth(stats.getCurrentHealth() + amount); }
    @Override public void restoreMana(int amount) { stats.setCurrentMana(stats.getCurrentMana() + amount); }
    @Override public void applyStatusEffect(String status) { /* TODO */ }
}
