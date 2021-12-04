package common.commands.user;

import server.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.ui.UserInterface;
import common.elementsOfCollection.Vehicle;
import server.interaction.StorageInteraction;

import java.util.List;

/**
 * Класс команды remove_lower.
 */
public class RemoveLower extends Command {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public RemoveLower() {
        cmdLine = "remove_lower";
        description = "удалить из коллекции все элементы, меньшие, чем заданный";
        options = "Параметры: Сравниваемый объект";
        needsObject = true;
        argumentAmount = 1;
        serverCommandLabel = false;
        editsCollection = true;
    }

    /**
     * Метод исполнения
     */
    @Override
    public String execute(StorageInteraction storageInteraction, Vehicle vehicle, DataBaseCenter dbc, User user) {
        int size1 = storageInteraction.getSize();
        List<Long> idRemoved = storageInteraction.removeLower(vehicle);
        int size2 = storageInteraction.getSize();
        if (size2 < size1) {
            for (Long aLong : idRemoved) {
                dbc.removeVehicle(aLong, user);
            }
            dbc.retrieveCollectionFromDB(storageInteraction);
            return ("Операция успешно выполнена");
        }
        else return "Что-то пошло не так";
    }
}
