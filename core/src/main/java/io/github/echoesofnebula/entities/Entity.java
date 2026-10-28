package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.items.Item;
import io.github.echoesofnebula.items.ItemEffect;
import io.github.echoesofnebula.view.GameScreen;

public abstract class Entity {
    protected Vector2 pos = new Vector2();
    protected Vector2 velocity = new Vector2();
    protected Rectangle bounds = new Rectangle();
    protected float size = 16f * GameScreen.UNIT_SCALE;

    // --- DEFAULT NO-OP METHODS (This eliminates the need for instanceof!) ---

    public void update(float deltaTime) {
        // Override in subclasses if the entity needs to update (e.g., Player, Enemy, Item)
    }

    public void drawTextures(SpriteBatch batch) {
        // Override in subclasses if the entity has a visual texture
    }

    public void drawShapes(ShapeRenderer shapeRenderer) {
    }

    public void onRemovedFromWorld(GameWorld world) {
    }

    public void takeDamage(float amount, Entity source) {

    }
    public void pickupItem(Item item, GameWorld world) {

    }

    public void applyConsumableEffect(ItemEffect effect) {

    }


    public void checkState(GameWorld world) {
    }

    // --- CORE METHODS ---
    public abstract void acceptCollision(GameWorld world, CollisionEventData event);

    public Vector2 getPosition() { return pos; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getBounds() { return bounds; }
    public float getSize() { return size; }
}
