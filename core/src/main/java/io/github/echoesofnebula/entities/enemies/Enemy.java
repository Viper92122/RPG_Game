package io.github.echoesofnebula.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.ai.AIConfiguration;
import io.github.echoesofnebula.ai.AIController;
import io.github.echoesofnebula.ai.BrainOwner;
import io.github.echoesofnebula.ai.states.ChaseState;
import io.github.echoesofnebula.ai.states.StateMachine;
import io.github.echoesofnebula.ai.states.WanderState;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.entities.ItemUser;
import io.github.echoesofnebula.entities.CharacterStats;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.items.loot.LootTable;
import io.github.echoesofnebula.rendering.AssetHub;
import io.github.echoesofnebula.rendering.ParticleSystem;
import io.github.echoesofnebula.status.StatusEffect;

public class Enemy extends Entity implements ItemUser, BrainOwner {
    private final CharacterStats characterStats;
    private final LootTable lootTable;

    private final AIConfiguration aiConfig;

    // Animation fields (can be null for simple rectangle testing)
    private final Animation<TextureRegion> idleAnim;
    private final Animation<TextureRegion> walkAnim;
    private float stateTime = 0f;

    private Entity target;
    private Vector2 destination;
    private final AIController aiController;  // ✅ New pattern
    public Enemy(float startX, float startY, CharacterStats characterStats, LootTable lootTable,
                 AIConfiguration aiConfig, Animation<TextureRegion> idleAnim, Animation<TextureRegion> walkAnim) {
        this.pos.set(startX, startY);
        this.velocity.set(0, 0);
        this.bounds.set(startX, startY, size, size);
        this.aiConfig = aiConfig;
        this.aiController = buildAIController();
        this.characterStats = characterStats;
        this.lootTable = lootTable;
        this.idleAnim = idleAnim;
        this.walkAnim = walkAnim;

    }

    private AIController buildAIController() {
        StateMachine stateMachine = new StateMachine(this);

        switch (aiConfig.aiType) {
            case WANDER_ONLY:
                stateMachine.setState(new WanderState());
                break;

            case WANDER_AND_CHASE:
                stateMachine.setState(new WanderState());
                // TODO: Add transition rules if using transition table pattern
                break;

            case AGGRESSIVE:
                stateMachine.setState(new ChaseState());
                break;

            case BOSS:
                stateMachine.setState(new WanderState());
                break;
        }

        return new AIController(this, stateMachine);
    }


    @Override
    public void update(float deltaTime) {
        if (characterStats.isDead()) return; // Don't process AI for dead enemies



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
        characterStats.takeDamage(amount);
        System.out.println("Enemy took " + amount + " damage! HP: " + characterStats.getCurrentHealth());
    }

    @Override
    public void applyStatusEffect(StatusEffect effect) {

    }

    @Override
    public void clearStatusEffects() {

    }


    public boolean isDead() {
        return characterStats.isDead();
    }

    @Override
    public Entity getEntity() { return this; }

    @Override
    public CharacterStats getStats() { return characterStats; }

    @Override
    public void setTarget(Entity target) { this.target = target; }

    @Override
    public Entity getTarget() { return target; }

    @Override
    public Vector2 getDestination() {
        return null;
    }

    @Override
    public void setDestination(Vector2 destination) {
        this.destination = destination;
    }

    @Override
    public void clearDestination() {
        destination = null;
    }

    @Override
    public boolean hasDestination() {
        return destination != null;
    }

    @Override public float getVisionRange() { return aiConfig.visionRange; }
    @Override public float getAttackRange() { return aiConfig.attackRange; }
    @Override public float getFleeHealthThreshold() { return aiConfig.fleeHealthThreshold; }

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
        aiController.handleWallImpact();
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
        characterStats.setCurrentHealth(characterStats.getCurrentHealth() + amount);
    }

    @Override
    public void restoreMana(int amount) {
        characterStats.setCurrentMana(characterStats.getCurrentMana() + amount);
    }
    @Override
    public void checkState(GameWorld world) {
        if (characterStats.isDead()) {
            System.out.println("💀 Enemy died! Dropping loot...");
            this.dropLoot(world);
            ParticleSystem.spawn(ParticleSystem.ParticleType.FIRE, this.pos.x, this.pos.y);
            world.removeEntity(this); // Flags itself for removal

        }
    }

    @Override
    public void takeDamage(float amount, Entity source) {
        characterStats.takeDamage((int) amount);
        System.out.println("Enemy took " + (int)amount + " damage! HP: " + characterStats.getCurrentHealth());
    }


}
