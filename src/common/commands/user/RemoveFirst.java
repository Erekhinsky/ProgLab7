package common.commands.user;

import server.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.ui.UserInterface;
import server.interaction.StorageInteraction;

import java.io.IOException;

/**
 * Класс команды remove_first.
 */
public class RemoveFirst extends Command {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public RemoveFirst() {
        cmdLine = "remove_first";
        description = "удалить первый элемент из коллекции";
        options = "Нет параметров.";
        needsObject = false;
        argumentAmount = 0;
        serverCommandLabel = false;
        editsCollection = true;
    }

    /**
     * Метод исполнения
     */
    @Override
    public String execute(StorageInteraction storageInteraction, DataBaseCenter dataBaseCenter, User user) throws IOException {
        int size1 = storageInteraction.getSize();
        try {
            long id = storageInteraction.removeFirst();
            int size2 = storageInteraction.getSize();
            if (size2 < size1 && dataBaseCenter.removeVehicle(id, user) ) {
                dataBaseCenter.retrieveCollectionFromDB(storageInteraction);
                return ("Операция успешно выполнена");
            }
            else return ("Упс, что-то пошло не так.");
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        } return "Коллекция не полна.";
    }
}
