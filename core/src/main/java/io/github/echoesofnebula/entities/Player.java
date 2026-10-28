package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.input.PlayerIntent;
import io.github.echoesofnebula.items.Inventory;
import io.github.echoesofnebula.items.Item;
import io.github.echoesofnebula.items.ItemEffect;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.managers.ItemEffectFactory;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.status.StatusEffect;

public class Player extends Entity implements ItemUser {
    private final CharacterAnimationSet animations;
    private EntityFacingDirection facingDirection = EntityFacingDirection.DOWN;
    private float stateTime = 0f;
    private boolean isMoving = false;

    private final Array<StatusEffect> activeStatusEffects = new Array<>();

    private float walkingSpeed = 5.0f;
    private float runningSpeed = 9.0f;
    private float currentSpeed = 5.0f;

    private final Inventory inventory;
    private PlayerIntent currentIntent = new PlayerIntent();

    public Player(float startX, float startY, AssetHub assetHub, ItemEffectFactory effectFactory) {
        this.pos.set(startX, startY);
        this.velocity.set(0, 0);

        float hitboxWidth = size * 0.75f;
        float hitboxHeight = size * 0.40f;
        this.bounds.set(startX + (size - hitboxWidth) / 2f, startY, hitboxWidth, hitboxHeight);

        // ✅ Animation initialization delegated entirely to AssetHub!
        this.animations = assetHub.loadCharacterAnimations("walking_animations", 0.1f);
        if (this.animations == null) {
            System.err.println("🚨 CRITICAL: Failed to load player animations!");
        }

        this.inventory = new Inventory(effectFactory);
    }

    public void setIntent(PlayerIntent intent) {
        if (intent == null) return;
        this.currentIntent.movementDirection.x = intent.movementDirection.x;
        this.currentIntent.movementDirection.y = intent.movementDirection.y;
        this.currentIntent.wantsToRun = intent.wantsToRun;
        this.currentIntent.activeAction = intent.activeAction;
        this.currentIntent.actionTargetWorld.set(intent.actionTargetWorld);
    }

    @Override
    public void update(float deltaTime) {
        currentSpeed = currentIntent.wantsToRun ? runningSpeed : walkingSpeed;
        isMoving = currentIntent.movementDirection.len2() > 0.001f;

        if (isMoving) {
            velocity.set(currentIntent.movementDirection).scl(currentSpeed);
            setFacingDirection(currentIntent.movementDirection.x, currentIntent.movementDirection.y);
            stateTime += deltaTime;
        } else {
            velocity.set(0, 0);
            stateTime = 0f;
        }

        float hitboxWidth = size * 0.75f;
        this.bounds.setPosition(this.pos.x + (size - hitboxWidth) / 2f, this.pos.y);

        handlePlayerActions();

        for (int i = activeStatusEffects.size - 1; i >= 0; i--) {
            StatusEffect effect = activeStatusEffects.get(i);
            effect.update(this, deltaTime);

            if (effect.isExpired()) {
                effect.onRemove(this);
                activeStatusEffects.removeIndex(i);
            }
        }
    }
    @Override
    public void applyStatusEffect(StatusEffect effect) {
        // Check if we already have this effect
        for (StatusEffect existing : activeStatusEffects) {
            if (existing.getBlueprint().effectType.equals(effect.getBlueprint().effectType)) {
                if (existing.getBlueprint().stackable) {
                    // Add as new instance
                    activeStatusEffects.add(effect);
                    effect.onApply(this);
                } else if (existing.getBlueprint().refreshDuration) {
                    // Refresh existing
                    existing.refresh();
                    System.out.println("🔄 Refreshed status: " + effect.getBlueprint().displayName);
                }
                return;
            }
        }

        // New effect
        activeStatusEffects.add(effect);
        effect.onApply(this);
    }

    @Override
    public void clearStatusEffects() {
        for (StatusEffect effect : activeStatusEffects) {
            effect.onRemove(this);
        }
        activeStatusEffects.clear();
    }

    public Array<StatusEffect> getActiveStatusEffects() {
        return activeStatusEffects;
    }
    private void handlePlayerActions() {
        if (currentIntent.activeAction == PlayerActionType.NONE) return;

        switch (currentIntent.activeAction) {
            case CAST_SPELL:
                System.out.println("🔥 Cast Fireball in direction: " + facingDirection);
                break;
            case MELEE_ATTACK:
                System.out.println("⚔️ Player swung weapon toward: " + facingDirection);
                break;
            case INTERACT:
                System.out.println("🖐️ Player interacted facing: " + facingDirection);
                break;
        }
        currentIntent.activeAction = PlayerActionType.NONE;
    }

    private void setFacingDirection(float moveX, float moveY) {
        if (moveX > 0 && moveY > 0) facingDirection = EntityFacingDirection.SIDEWAYS_UP_RIGHT;
        else if (moveX < 0 && moveY > 0) facingDirection = EntityFacingDirection.SIDEWAYS_UP_LEFT;
        else if (moveX > 0 && moveY < 0) facingDirection = EntityFacingDirection.SIDEWAYS_DOWN_RIGHT;
        else if (moveX < 0 && moveY < 0) facingDirection = EntityFacingDirection.SIDEWAYS_DOWN_LEFT;
        else if (moveX > 0) facingDirection = EntityFacingDirection.RIGHT;
        else if (moveX < 0) facingDirection = EntityFacingDirection.LEFT;
        else if (moveY > 0) facingDirection = EntityFacingDirection.UP;
        else if (moveY < 0) facingDirection = EntityFacingDirection.DOWN;
    }

    @Override
    public void drawTextures(SpriteBatch batch) {
        if (animations == null) return; // Fail-safe

        TextureRegion currentFrame = animations.getAnimation(facingDirection).getKeyFrame(stateTime, true);
        batch.setColor(1, 1, 1, 1);
        batch.draw(currentFrame, pos.x, pos.y, size, size);
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer) {
        // Optional debug drawing
    }

    @Override
    public void acceptCollision(GameWorld world, CollisionEventData event) {
        world.handlePlayerCollision(this, event);
    }

    @Override
    public void pickupItem(Item item, GameWorld world) {
        inventory.addItem(item.getBlueprint(), 1);
    }

    @Override
    public void applyConsumableEffect(ItemEffect effect) {
        effect.apply(this);
    }

    public Inventory getInventory() { return inventory; }
    public PlayerIntent getMovementIntent() { return this.currentIntent; }

    @Override
    public void heal(int amount) {
        System.out.println("Player healed! HP: " + amount); // Replace with actual Stats logic
    }

    @Override
    public void restoreMana(int amount) {
        System.out.println("Player restored mana! MP: " + amount); // Replace with actual Stats logic
    }


}
