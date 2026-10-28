package io.github.echoesofnebula.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.events.CollisionEventData;

public class Item extends Entity   {
    private final String itemName;
    private boolean isCollected = false;
    private float stateTime = 0f;
    protected ItemBlueprint blueprint;
    protected Animation<TextureRegion> animation;
    protected TextureRegion staticTexture;

    protected float width, height;

    public Item(String itemName, float startX, float startY, float width, float height, ItemBlueprint blueprint){
        this.itemName = itemName;
        this.pos.set(startX, startY);
        this.bounds.set(startX, startY, width, height);
        this.width = width;
        this.height = height;
        this.blueprint = blueprint;
    }
    public Item(String itemName, float startX, float startY, float width, float height,ItemBlueprint blueprint,
                TextureRegion texture, Animation<TextureRegion> animation) {
        this(itemName, startX, startY, width, height, blueprint);
        this.staticTexture = texture;       // Will be null if animated
        this.animation = animation;   // Will be null if static
    }


    @Override
    public void update(float deltaTime) {
        if (isCollected) return;
        this.stateTime += deltaTime;
    }


    @Override
    public void drawTextures(SpriteBatch batch) {
        if (isCollected) return;

        TextureRegion currentFrame = (animation != null) ? animation.getKeyFrame(stateTime, true) : staticTexture;

        if (currentFrame != null) {


            float bobbingY = (float) Math.sin(stateTime * 3.0f) * 0.04f;
            batch.draw(currentFrame, getPosition().x, getPosition().y + bobbingY, width, height);
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
    public void onCollectedBy(Entity collector, GameWorld world) {
        collector.pickupItem(this, world);
        world.removeEntity(this);
    }
    public void interactWith(Entity entity, GameWorld world) {
        System.out.println("Interacted with persistent entity: " + this.getItemName());
    }

    public ItemBlueprint getBlueprint() {
        return blueprint;
    }
}
