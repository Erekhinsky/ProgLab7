package common.commands.server;

import common.commands.abstracts.Command;
import common.ui.UserInterface;

public class Register extends Command {
    public Register() {
        cmdLine = "register";
        description = "регистрация";
        needsObject = false;
        argumentAmount = 2;
        serverCommandLabel = true;
        editsCollection = false;
    }

    public String execute(UserInterface ui, boolean success) {
        if (success)
            return ("Регистрация успешна!");
        else return ("Регистрация не удалась.");
    }
}