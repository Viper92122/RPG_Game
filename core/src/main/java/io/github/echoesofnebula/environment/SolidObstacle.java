package io.github.echoesofnebula.environment;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.events.CollisionEventData;

public class SolidObstacle extends Entity {
    public SolidObstacle(float x, float y, float width, float height) {
        this.pos = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void acceptCollision(GameWorld world, CollisionEventData event) {
        world.handleSolidObstacleCollision(this, event);
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer) {
    }
}
