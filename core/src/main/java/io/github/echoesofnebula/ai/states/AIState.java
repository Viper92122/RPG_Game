package io.github.echoesofnebula.ai.states;


import io.github.echoesofnebula.ai.AIBehavior;
import io.github.echoesofnebula.ai.BrainOwner;

public abstract class AIState implements AIBehavior {

    public abstract void enter();
    public abstract void exit();
    public abstract AIState checkTransitions(BrainOwner owner);  // Decide when to switch states
}
