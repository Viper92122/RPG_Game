package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.view.GameScreen;

public abstract class Entity {
    protected Vector2 pos = new Vector2();
    protected Vector2 velocity = new Vector2();
    protected Rectangle bounds = new Rectangle();
    protected float size = 16f * GameScreen.UNIT_SCALE;

    public abstract void acceptCollision(GameWorld world, CollisionEventData event);

    public abstract void drawShapes(ShapeRenderer shapeRenderer);

    public Vector2 getPosition() {
        return pos;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Rectangle getBounds() {
        return bounds;
    }
    public float getSize() {
        return size;
    }
}
