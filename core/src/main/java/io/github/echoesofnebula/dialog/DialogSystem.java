package io.github.echoesofnebula.dialog;

public class DialogSystem {
    private DialogSystem instance;

    private DialogSystem(){

    }

    public DialogSystem get(){
        if(instance == null) {
            instance = new DialogSystem();
        }
        return instance;
    }
    public static String showDialog(){

        return "";
    }
}
