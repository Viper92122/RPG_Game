package io.github.echoesofnebula.ai;

import io.github.echoesofnebula.ai.states.AIState;
import io.github.echoesofnebula.ai.states.StateMachine;

public class AIController {
    private final BrainOwner owner;
    private final StateMachine stateMachine;

    public AIController(BrainOwner owner, StateMachine stateMachine) {
        this.owner = owner;
        this.stateMachine = stateMachine;
    }

    public void update(float deltaTime) {
        stateMachine.update(deltaTime);
    }

    public void handleWallImpact() {
        stateMachine.getCurrentState().handleWallImpact(owner);
    }

    public void setState(AIState newState) {
        stateMachine.setState(newState);
    }

    public AIState getCurrentState() {
        return stateMachine.getCurrentState();
    }
}
