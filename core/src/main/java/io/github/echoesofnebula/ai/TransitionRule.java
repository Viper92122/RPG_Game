package io.github.echoesofnebula.ai;

import java.util.function.Predicate;  // ✅ This one

import io.github.echoesofnebula.ai.states.AIState;
import io.github.echoesofnebula.ai.states.StateFactory;

public class TransitionRule {
    public final AIState from;
    public final Predicate<BrainOwner> condition;  // Java's Predicate
    public final StateFactory toFactory;

    public TransitionRule(AIState from, Predicate<BrainOwner> condition, StateFactory toFactory) {
        this.from = from;
        this.condition = condition;
        this.toFactory = toFactory;
    }
}


