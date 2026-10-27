package io.github.echoesofnebula.items;

import io.github.echoesofnebula.entities.ItemUser;

@FunctionalInterface
public interface ItemEffect {
    void apply(ItemUser target);
}
