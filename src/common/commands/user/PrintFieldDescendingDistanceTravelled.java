package common.commands.user;

import common.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.ui.UserInterface;
import server.interaction.StorageInteraction;

import java.io.IOException;

/**
 * Класс команды print_field_descending_distance_travelled.
 */
public class PrintFieldDescendingDistanceTravelled extends Command {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public PrintFieldDescendingDistanceTravelled() {
        cmdLine = "print_field_descending_distance_travelled";
        description = "вывести значения поля distanceTravelled всех элементов в порядке убывания";
        options = "Нет параметров.";
        needsObject = false;
        argumentAmount = 0;
        serverCommandLabel = false;
        editsCollection = false;
    }

    /**
     * Метод исполнения
     *
     * @param ui объект, через который ведется взаимодействие с пользователем.
     */
    public String execute(UserInterface ui, StorageInteraction storageInteraction, DataBaseCenter dataBaseCenter, User user) throws IOException {
        return storageInteraction.printFieldDescendingDistanceTravelled().listIterator().next().toString();
    }
}
