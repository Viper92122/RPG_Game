package io.github.echoesofnebula.managers;

import io.github.echoesofnebula.spells.SpellBlueprint;

public class SpellRegistry extends BaseRegistry<SpellBlueprint> {
    public SpellRegistry() {
        super("spells/spells.json", SpellBlueprint.class);
    }
}
