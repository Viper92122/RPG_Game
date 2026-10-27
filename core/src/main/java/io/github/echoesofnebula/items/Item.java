package io.github.echoesofnebula.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.core.UpdateAble;
import io.github.echoesofnebula.core.SpriteRenderable;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.view.GameScreen;

public class Item extends Entity implements UpdateAble, SpriteRenderable {
    private final String itemName;
    private boolean isCollected = false;
    private float stateTime = 0f;

    private final float renderWidth;
    private final float renderHeight;

    protected Animation<TextureRegion> animation;
    protected TextureRegion staticTexture;

    public Item(String itemName, float x, float y, float width, float height, TextureRegion texture) {
        this(itemName, x, y, width, height);
        this.staticTexture = texture;
    }

    public Item(String itemName, float x, float y, float width, float height, Animation<TextureRegion> animation) {
        this(itemName, x, y, width, height);
        this.animation = animation;
    }

    private Item(String itemName, float x, float y, float width, float height) {
        this.itemName = itemName;
        this.getPosition().set(x, y);
        this.renderWidth = width;
        this.renderHeight = height;

        // Auto-center a tight 8x8 pixel collection hitbox safely at the base floor
        float hitboxWidth = 8f * GameScreen.UNIT_SCALE;
        float hitboxHeight = 8f * GameScreen.UNIT_SCALE;
        float offsetX = (renderWidth - hitboxWidth) / 2f;
        float offsetY = 0f;

        this.getBounds().set(x + offsetX, y + offsetY, hitboxWidth, hitboxHeight);
    }

    @Override
    public void update(float deltaTime) {
        if (isCollected) return;
        this.stateTime += deltaTime;
    }

    private boolean hasLoggedFrame = false; // Add this to the top fields of Item.java

    @Override
    public void drawTextures(SpriteBatch batch) {
        if (isCollected) return;

        TextureRegion currentFrame = (animation != null) ? animation.getKeyFrame(stateTime, true) : staticTexture;

        if (currentFrame != null) {
            // 🔍 RUNTIME RENDERING TRACKER LOG
            if (!hasLoggedFrame && animation != null) {
                System.out.println("📺 RUNTIME DRAW CHECK for [" + itemName + "]:");
                System.out.println("   -> Target Draw Dimensions on Canvas: " + renderWidth + "x" + renderHeight);
                System.out.println("   -> Active Frame Region Dimensions: " + currentFrame.getRegionWidth() + "x" + currentFrame.getRegionHeight());
                hasLoggedFrame = true;
            }

            float bobbingY = (float) Math.sin(stateTime * 3.0f) * 0.04f;
            batch.draw(currentFrame, getPosition().x, getPosition().y + bobbingY, renderWidth, renderHeight);
        }
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer) {
        if (!isCollected) {
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.rect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
        }
    }

    @Override
    public void acceptCollision(GameWorld world, CollisionEventData event) {
        if (!isCollected) {
            world.handleItemCollision(this, event);
        }
    }

    public String getItemName() { return itemName; }
    public boolean isCollected() { return isCollected; }
    public void collect() { this.isCollected = true; }
}
