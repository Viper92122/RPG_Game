package io.github.echoesofnebula.abilities.spells;

import io.github.echoesofnebula.core.BaseRegistry;

public class SpellRegistry extends BaseRegistry<SpellBlueprint> {
    public SpellRegistry() {
        super("spells/spells.json", SpellBlueprint.class);
    }
}
