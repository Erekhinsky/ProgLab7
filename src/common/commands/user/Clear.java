package common.commands.user;

import server.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.ui.UserInterface;
import server.interaction.StorageInteraction;

import java.io.IOException;

/**
 * Класс команды clear.
 */
public class Clear extends Command {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public Clear() {
        cmdLine = "clear";
        description = "очистить коллекцию";
        options = "Нет параметров.";
        needsObject = false;
        argumentAmount = 0;
        serverCommandLabel = false;
        editsCollection = true;
    }

    /**
     * Метод исполнения
     *
     * @return Результат команды.
     */
    public String execute(StorageInteraction storageInteraction, DataBaseCenter dbc, User user) throws IOException {
        if (!(dbc.clearCollection(user)))
            return "Что-то пошло не так, попробуйте еще раз";
        else {
            storageInteraction.clear();
            dbc.retrieveCollectionFromDB(storageInteraction);
            return("Коллекция очищена");
        }
    }
}

