package io.github.echoesofnebula.ai.states;

import com.badlogic.gdx.utils.Array;

import io.github.echoesofnebula.ai.BrainOwner;
import io.github.echoesofnebula.ai.TransitionRule;
import io.github.echoesofnebula.entities.enemies.Enemy;

public class StateMachine {
    private final BrainOwner owner;
    private AIState currentState;
    private final Array<TransitionRule> rules = new Array<>();

    public StateMachine(BrainOwner owner) {
        this.owner = owner;
    }

    public void addRule(TransitionRule rule) {
        rules.add(rule);
    }

    public void update(float deltaTime) {
        currentState.update(owner, deltaTime);

        // Check all rules — state machine decides transitions, not states
        for (TransitionRule rule : rules) {
            if (rule.from.getClass().equals(currentState.getClass()) && rule.condition.test(owner)) {
                currentState.exit();
                currentState = rule.toFactory.create(owner);
                currentState.enter();
                break;
            }
        }
    }

    public AIState getCurrentState() {
        return currentState;
    }

    public void setState(AIState state) {
        this.currentState = state;
    }
}




