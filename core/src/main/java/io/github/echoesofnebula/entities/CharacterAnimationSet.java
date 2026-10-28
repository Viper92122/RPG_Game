package io.github.echoesofnebula.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Holds all directional walking animations for a character.
 * Keeps the Player class clean and focused on game logic.
 */
public class CharacterAnimationSet {
    public final Animation<TextureRegion> walkDown;
    public final Animation<TextureRegion> walkSideDownRight;
    public final Animation<TextureRegion> walkSideDownLeft;
    public final Animation<TextureRegion> walkRight;
    public final Animation<TextureRegion> walkLeft;
    public final Animation<TextureRegion> walkSideUpRight;
    public final Animation<TextureRegion> walkSideUpLeft;
    public final Animation<TextureRegion> walkUp;

    public CharacterAnimationSet(
        Animation<TextureRegion> walkDown,
        Animation<TextureRegion> walkSideDownRight,
        Animation<TextureRegion> walkSideDownLeft,
        Animation<TextureRegion> walkRight,
        Animation<TextureRegion> walkLeft,
        Animation<TextureRegion> walkSideUpRight,
        Animation<TextureRegion> walkSideUpLeft,
        Animation<TextureRegion> walkUp) {

        this.walkDown = walkDown;
        this.walkSideDownRight = walkSideDownRight;
        this.walkSideDownLeft = walkSideDownLeft;
        this.walkRight = walkRight;
        this.walkLeft = walkLeft;
        this.walkSideUpRight = walkSideUpRight;
        this.walkSideUpLeft = walkSideUpLeft;
        this.walkUp = walkUp;
    }


    public Animation<TextureRegion> getAnimation(EntityFacingDirection direction) {
        if (direction == null) return walkDown;

        switch (direction) {
            case UP: return walkUp;
            case SIDEWAYS_UP_RIGHT: return walkSideUpRight;
            case SIDEWAYS_UP_LEFT: return walkSideUpLeft;
            case RIGHT: return walkRight;
            case LEFT: return walkLeft;
            case SIDEWAYS_DOWN_RIGHT: return walkSideDownRight;
            case SIDEWAYS_DOWN_LEFT: return walkSideDownLeft;
            default: return walkDown;
        }
    }
}
