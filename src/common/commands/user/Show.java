package common.commands.user;

import server.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.ui.UserInterface;
import server.interaction.StorageInteraction;

import java.io.IOException;

/**
 * Класс команды show.
 */
public class Show extends Command {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public Show() {
        cmdLine = "show";
        description = "вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
        options = "Нет параметров.";
        needsObject = false;
        argumentAmount = 0;
        serverCommandLabel = false;
        editsCollection = false;
    }

    /**
     * Метод исполнения
     */
    public String execute(StorageInteraction storageInteraction, DataBaseCenter dataBaseCenter, User user) throws IOException {
        if (storageInteraction.getSize() == 0)
            return ("Коллекция пуста");
        else {
            return ("Коллекция: " + storageInteraction.show());
        }
    }
}
