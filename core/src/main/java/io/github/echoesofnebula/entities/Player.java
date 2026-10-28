package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.core.SpriteRenderable;
import io.github.echoesofnebula.core.UpdateAble;
import io.github.echoesofnebula.input.PlayerIntent;
import io.github.echoesofnebula.items.Inventory;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.managers.AssetHub;
import io.github.echoesofnebula.managers.ParticleTestHub;
public class Player extends Entity implements  SpriteRenderable, UpdateAble, ItemUser {
    private float walkingSpeed = 5.0f;
    private float runningSpeed = 9.0f;
    private float currentSpeed = 5.0f;
    private Inventory inventory = new Inventory();
    private String username = "Temporary Username";
    private PlayerIntent currentIntent = new PlayerIntent();

    private EntityFacingDirection facingDirection = EntityFacingDirection.DOWN;
    private TextureRegion masterSheet;
    private Animation<TextureRegion> walkDownAnim;
    private Animation<TextureRegion> walkSideDownRightAnim;
    private Animation<TextureRegion> walkSideDownLeftAnim;
    private Animation<TextureRegion> walkRightAnim;
    private Animation<TextureRegion> walkLeftAnim;
    private Animation<TextureRegion> walkSideUpRightAnim;
    private Animation<TextureRegion> walkSideUpLeftAnim;
    private Animation<TextureRegion> walkUpAnim;
    private final Stats stats;
    private float stateTime = 0f;
    private boolean isMoving = false;

    public void setIntent(PlayerIntent intent) {
        if (intent == null) return;

        // 1. Copy movement properties
        this.currentIntent.movementDirection.x = intent.movementDirection.x;
        this.currentIntent.movementDirection.y = intent.movementDirection.y;
        this.currentIntent.wantsToRun = intent.wantsToRun;

        // 2. CRITICAL FIX: Copy the action properties so the player loop registers the input!
        this.currentIntent.activeAction = intent.activeAction;
        this.currentIntent.actionTargetWorld.set(intent.actionTargetWorld);
    }
    public Player(float startX, float startY, AssetHub assetHub) {
        // Set the values on the EXISTING vectors inherited from Entity
        this.pos.set(startX, startY);
        this.velocity.set(0, 0);

        float hitboxWidth = size * 0.75f;
        float hitboxHeight = size * 0.40f;
        this.stats = new Stats(100, 50, 5.0f, 10);
        // Update your bounds instance directly
        this.bounds.set(startX + (size - hitboxWidth) / 2f, startY, hitboxWidth, hitboxHeight);

        initAnimations(assetHub);
    }
    @Override
    public void update(float deltaTime) {
        // 1. Process movement properties using the passed intent
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

        // Keep physical bounding box tracking visual position
        float hitboxWidth = size * 0.75f;
        this.bounds.setPosition(this.pos.x + (size - hitboxWidth) / 2f, this.pos.y);

        // 2. CRITICAL: Execute actions triggered during this single frame tick
        handlePlayerActions();
    }
    private void handlePlayerActions() {
        if (currentIntent.activeAction == PlayerActionType.NONE) return;

        switch (currentIntent.activeAction) {
            case CAST_SPELL:
                // Calculate center point of player
                float spawnX = this.pos.x + (this.size / 2f);
                float spawnY = this.pos.y + (this.size / 2f);

                // 1. Get the direction vector based on where the player is currently looking
                Vector2 launchDirection = getFacingVector();

                // 2. Spawn your fire particle system
                ParticleTestHub.spawn(ParticleTestHub.ParticleType.FIRE, spawnX, spawnY);

                // 3. TODO: Instantiate your Fireball projectile entity here, passing it:
                //    spawnX, spawnY, and launchDirection (e.g. projectile.setVelocity(launchDirection.scl(projectileSpeed)))
                System.out.println("🔥 Cast Fireball in direction: " + facingDirection + " | Vector: " + launchDirection);
                break;

            case MELEE_ATTACK:
                System.out.println("⚔️ Player swung weapon toward: " + facingDirection);
                break;

            case INTERACT:
                System.out.println("🖐️ Player interacted facing: " + facingDirection);
                break;
        }

        // Cleanly consume the action flag
        currentIntent.activeAction = PlayerActionType.NONE;
    }
    private Vector2 getFacingVector() {
        Vector2 dirVector =  Vector2.Zero;

        switch (facingDirection) {
            case UP:                 dirVector.set(0, 1);   break;
            case DOWN:               dirVector.set(0, -1);  break;
            case RIGHT:              dirVector.set(1, 0);   break;
            case LEFT:               dirVector.set(-1, 0);  break;
            case SIDEWAYS_UP_RIGHT:   dirVector.set(1, 1);   break;
            case SIDEWAYS_UP_LEFT:    dirVector.set(-1, 1);  break;
            case SIDEWAYS_DOWN_RIGHT: dirVector.set(1, -1);  break;
            case SIDEWAYS_DOWN_LEFT:  dirVector.set(-1, -1); break;
        }

        // Normalize to prevent diagonal movement or projectiles from going faster (length of 1)
        if (dirVector.len2() > 0) {
            dirVector.nor();
        }

        return dirVector;
    }
    private void initAnimations(AssetHub assetHub) {
        this.masterSheet = assetHub.getRegion("walking_animations");

        if (masterSheet == null) {
            System.err.println("🚨 ERROR: 'walking_animations' texture region is NULL! AssetHub failed to load it.");
            return;
        }

        int totalRows = 5;
        int totalCols = 4;

        // Explicit safety diagnostic check
        System.out.println("Master Sprite Sheet Size: Width=" + masterSheet.getRegionWidth() + " Height=" + masterSheet.getRegionHeight());

        int frameWidth = masterSheet.getRegionWidth() / totalCols;
        int frameHeight = masterSheet.getRegionHeight() / totalRows;

        // FIX: If the calculation drops to 0, force it to match your base graphics asset grid size (e.g., 16 or 32 pixels per frame)
        if (frameWidth <= 0 || frameHeight <= 0) {
            System.err.println("🚨 WARNING: Frame calculation yielded 0! Forcing default 16x16 frame splitting dimensions.");
            frameWidth = 16;
            frameHeight = 16;
        }

        TextureRegion[][] tmp = masterSheet.split(frameWidth, frameHeight);
        float frameDuration = 0.1f;

        walkDownAnim = new Animation<>(frameDuration, extractRowFrames(tmp[0], totalCols));

        TextureRegion[] sideDownRight = extractRowFrames(tmp[1], totalCols);
        walkSideDownRightAnim = new Animation<>(frameDuration, sideDownRight);
        walkSideDownLeftAnim = new Animation<>(frameDuration, flipFramesHorizontal(sideDownRight));

        TextureRegion[] pureRight = extractRowFrames(tmp[2], totalCols);
        walkRightAnim = new Animation<>(frameDuration, pureRight);
        walkLeftAnim = new Animation<>(frameDuration, flipFramesHorizontal(pureRight));

        TextureRegion[] sideUpRight = extractRowFrames(tmp[3], totalCols);
        walkSideUpRightAnim = new Animation<>(frameDuration, sideUpRight);
        walkSideUpLeftAnim = new Animation<>(frameDuration, flipFramesHorizontal(sideUpRight));

        walkUpAnim = new Animation<>(frameDuration, extractRowFrames(tmp[4], totalCols));

        Animation<TextureRegion>[] allAnims = new Animation[]{
            walkDownAnim, walkSideDownRightAnim, walkSideDownLeftAnim,
            walkRightAnim, walkLeftAnim, walkSideUpRightAnim, walkSideUpLeftAnim, walkUpAnim
        };
        for (Animation<TextureRegion> anim : allAnims) {
            anim.setPlayMode(Animation.PlayMode.LOOP);
        }
    }

    private TextureRegion[] extractRowFrames(TextureRegion[] rowCells, int count) {
        TextureRegion[] isolatedRow = new TextureRegion[count];
        System.arraycopy(rowCells, 0, isolatedRow, 0, count);
        return isolatedRow;
    }

    private TextureRegion[] flipFramesHorizontal(TextureRegion[] originalFrames) {
        TextureRegion[] flipped = new TextureRegion[originalFrames.length];
        for (int i = 0; i < originalFrames.length; i++) {
            flipped[i] = new TextureRegion(originalFrames[i]);
            flipped[i].flip(true, false);
        }
        return flipped;
    }

    private void setFacingDirection(float moveX, float moveY){
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
        TextureRegion currentFrame;
        switch (facingDirection) {
            case UP: currentFrame = walkUpAnim.getKeyFrame(stateTime); break;
            case SIDEWAYS_UP_RIGHT: currentFrame = walkSideUpRightAnim.getKeyFrame(stateTime); break;
            case SIDEWAYS_UP_LEFT: currentFrame = walkSideUpLeftAnim.getKeyFrame(stateTime); break;
            case RIGHT: currentFrame = walkRightAnim.getKeyFrame(stateTime); break;
            case LEFT: currentFrame = walkLeftAnim.getKeyFrame(stateTime); break;
            case SIDEWAYS_DOWN_RIGHT: currentFrame = walkSideDownRightAnim.getKeyFrame(stateTime); break;
            case SIDEWAYS_DOWN_LEFT: currentFrame = walkSideDownLeftAnim.getKeyFrame(stateTime); break;
            default: currentFrame = walkDownAnim.getKeyFrame(stateTime); break;
        }
        if (currentFrame == null) {
            System.err.println("🚨 DRAW ERROR: currentFrame is NULL for animation frame time: " + stateTime);
        }
        batch.setColor(1, 1, 1, 1); // Reset to pure white so colors are authentic
        batch.draw(currentFrame, pos.x, pos.y, size, size);
    }

    @Override
    public void acceptCollision(GameWorld world, CollisionEventData event) {
        ParticleTestHub.spawn(ParticleTestHub.ParticleType.FIRE, this.getPosition().x, this.getPosition().y);
        world.handlePlayerCollision(this, event);
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer) {}

    public void dispose() {}

    public Inventory getInventory() { return inventory; }


    @Override
    public void heal(int amount) {
        stats.setCurrentHealth(stats.getCurrentHealth() + amount);
        System.out.println("Player healed! HP: " + stats.getCurrentHealth() + "/" + stats.getMaxHealth());
    }

    @Override
    public void restoreMana(int amount) {
        stats.setCurrentMana(stats.getCurrentMana() + amount);
        System.out.println("Player restored mana! MP: " + stats.getCurrentMana() + "/" + stats.getMaxMana());
    }

    @Override
    public void applyStatusEffect(String status) {
        System.out.println("Player affected by: " + status);
    }

    public PlayerIntent getMovementIntent() {
       return this.currentIntent;
    }

    @SuppressWarnings("DefaultLocale")
    @Override
    public String toString() {
        return String.format(
            "═[ PLAYER DIAGNOSTIC BLINK ]═════════════════════════════════════════%n" +
                "  • Object HashCode: %d%n" +
                "  • Visual Position: X=%.4f, Y=%.4f%n" +
                "  • Physics Velocity: X=%.4f, Y=%.4f%n" +
                "  • Size Variable: %.2f%n" +
                "  • Hitbox Bounds: X=%.4f, Y=%.4f | Width=%.4f, Height=%.4f%n" +
                "  • Internal Flags: isMoving=%b | FacingDirection=%s | StateTime=%.2fs%n" +
                "  • Input Direction Vector: (%s)%n" +
                "════════════════════════════════════════════════════════════════════",
            this.hashCode(),
            this.pos.x, this.pos.y,
            this.velocity.x, this.velocity.y,
            this.size,
            this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height,
            this.isMoving, this.facingDirection, this.stateTime,
            this.currentIntent != null ? this.currentIntent.movementDirection.toString() : "NULL"
        );
    }
}
