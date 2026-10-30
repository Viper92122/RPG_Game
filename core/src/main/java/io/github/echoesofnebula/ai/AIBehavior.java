package io.github.echoesofnebula.ai;

public interface AIBehavior {
    void update(BrainOwner owner, float deltaTime);
    void handleWallImpact(BrainOwner owner);
}
