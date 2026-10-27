package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.echoesofnebula.ai.AIBehavior;
import io.github.echoesofnebula.core.GameWorld;
import io.github.echoesofnebula.core.SpriteRenderable;
import io.github.echoesofnebula.core.UpdateAble;
import io.github.echoesofnebula.events.CollisionEventData;
import io.github.echoesofnebula.view.GameScreen;

public class Enemy extends Entity implements UpdateAble, SpriteRenderable, ItemUser {
    private final AIBehavior aiBehavior;
    private float enemyRangeToSeePlayer = 50f * GameScreen.UNIT_SCALE;
    public Enemy(float startX, float startY, AIBehavior aiBehavior) {
        this.pos.set(startX * GameScreen.UNIT_SCALE, startY * GameScreen.UNIT_SCALE);
        this.velocity.set(0, 0);
        this.bounds.set(startX * GameScreen.UNIT_SCALE, startY * GameScreen.UNIT_SCALE, size, size);

        this.aiBehavior = aiBehavior;
    }

    @Override
    public void update(float deltaTime) {
        aiBehavior.update(this, deltaTime);
    }

    public void handleWallImpact() {
        aiBehavior.handleWallImpact(this);
    }

    @Override
    public void acceptCollision(GameWorld world, CollisionEventData event) {
        world.handleEnemyCollision(this, event);
    }

    @Override
    public void drawShapes(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(pos.x, pos.y, size, size);
    }

    @Override
    public void drawTextures(SpriteBatch batch) {
    }

    @Override
    public void heal(int amount) {
    }

    @Override
    public void restoreMana(int amount) {
    }

    @Override
    public void applyStatusEffect(String status) {
    }

    public float getEnemyRangeToSeePlayer() {
        return enemyRangeToSeePlayer;
    }
}
