package io.github.echoesofnebula.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.entities.Entity;
import io.github.echoesofnebula.events.CollisionEventData;

public class SolidObstacle extends Entity {

    public SolidObstacle(float x, float y, float width, float height) {
        this.pos.set(x, y);
        this.bounds.set(x, y, width, height);
    }

    @Override
    public void acceptCollision(GameWorld world, CollisionEventData event) {
        // world.handleSolidObstacleCollision(this, event);
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        float w = bounds.width > 0 ? bounds.width : 32f;
        float h = bounds.height > 0 ? bounds.height : 32f;
        shapeRenderer.rect(bounds.x, bounds.y, w, h);
    }
}
