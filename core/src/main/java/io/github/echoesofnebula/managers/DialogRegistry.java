package io.github.echoesofnebula.managers;


import io.github.echoesofnebula.dialog.DialogBlueprint;

public class DialogRegistry extends BaseRegistry<DialogBlueprint> {
    public DialogRegistry() {
        super("dialogs/dialogs.json", DialogBlueprint.class);
    }
}
