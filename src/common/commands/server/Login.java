package common.commands.server;

import common.commands.abstracts.Command;
import common.ui.UserInterface;

public class Login extends Command {
    public Login() {
        cmdLine = "login";
        description = "авторизация";
        needsObject = false;
        argumentAmount = 2;
        serverCommandLabel = true;
        editsCollection = false;
    }

    public String execute(boolean success) {
        if (success)
            return ("Вход в систему успешен!");
        else return ("Вход в систему не удался.");
    }
}
