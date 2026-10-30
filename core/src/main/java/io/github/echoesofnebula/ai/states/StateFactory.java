package io.github.echoesofnebula.ai.states;

import io.github.echoesofnebula.ai.BrainOwner;

@FunctionalInterface
public interface StateFactory {
    AIState create(BrainOwner owner);
}
