package io.github.echoesofnebula.dialog;


import io.github.echoesofnebula.core.BaseRegistry;

public class DialogRegistry extends BaseRegistry<DialogBlueprint> {
    public DialogRegistry() {
        super("dialogs/dialogs.json", DialogBlueprint.class);
    }
}
