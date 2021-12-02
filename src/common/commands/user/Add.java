package common.commands.user;

import common.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.exception.IncorrectValueException;
import common.ui.UserInterface;
import common.elementsOfCollection.Vehicle;
import server.Server;
import server.interaction.StorageInteraction;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

/**
 * Класс команды add.
 */
public class Add extends Command implements Serializable {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public Add() {
        cmdLine = "add";
        description = "добавить новый элемент с заданным ключом";
        options = "Параметры: Добавляемый объект";
        needsObject = true;
        argumentAmount = 1;
        serverCommandLabel = false;
        editsCollection = true;
    }

    /**
     * Метод исполнения.
     *
     * @param ui                 Объект взаимодействия с пользователем.
     * @param storageInteraction Объект исполнения команды.
     * @param vehicle            Хранимый в коллекции объект.
     * @return Результат выполнения команды.
     * @throws IncorrectValueException В случае ошибки ввода/вывода.
     */
    @Override
    public String execute(UserInterface ui, StorageInteraction storageInteraction, Vehicle vehicle, DataBaseCenter dbc, User user) throws IncorrectValueException {
//        vehicle.showVehicle();
//        int initSize = storageInteraction.getSize();
//        storageInteraction.add(vehicle);
//        if (storageInteraction.getSize() > initSize)
//            return ("Транспорт успешно добавлен");
//        else return ("Такой транспорт уже есть");

        if (dbc.addVehicle(vehicle, user)) {
            storageInteraction.add(vehicle);
            dbc.retrieveCollectionFromDB(storageInteraction);
            return ("Транспорт успешно добавлен\n");
        } else return ("Такой транспорт уже есть\n");

    }
}
