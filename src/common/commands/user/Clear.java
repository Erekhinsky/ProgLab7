package common.commands.user;

import common.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.exception.IncorrectValueException;
import common.ui.UserInterface;
import server.Server;
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
     * @param ui объект, через который ведется взаимодействие с пользователем.
     * @return Результат команды.
     */
    public String execute(UserInterface ui, StorageInteraction storageInteraction, DataBaseCenter dbc, User user) throws IOException {
//        storageInteraction.clear();
//        if (storageInteraction.getSize() > 0)
//            return ("Что-то пошло не так, попробуйте еще раз");
//        else return ("Коллекция очищена");

        Server.getExecutorService().execute(() -> {
            if (!(dbc.clearCollection(user))) {
                messageToClient.append("Что-то пошло не так.\n");
            } else {
                storageInteraction.clear();
                messageToClient.append("Коллекция очищена\n");
                dbc.retrieveCollectionFromDB(storageInteraction);
            }
            if (ui.isInteractionMode()) {
                messageToClient.append("Ожидаем дальнейших инструкций от клиента.\n");
            }
        });
        return messageToClient.toString();
    }
}

