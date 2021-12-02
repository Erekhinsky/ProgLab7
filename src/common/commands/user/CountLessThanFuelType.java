package common.commands.user;

import common.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.exception.IncorrectValueException;
import common.ui.UserInterface;
import common.elementsOfCollection.FuelType;
import server.Server;
import server.interaction.StorageInteraction;
import server.utils.ValidationClass;

import java.io.IOException;

/**
 * Класс команды count_less_than_fuel_type.
 */
public class CountLessThanFuelType extends Command {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public CountLessThanFuelType() {
        cmdLine = "count_less_than_fuel_type";
        description = "вывести количество элементов, значение поля fuelType которых меньше заданного";
        options = "Параметры: Fuel Type:" + FuelType.getPossibleValues();
        needsObject = false;
        argumentAmount = 1;
        serverCommandLabel = false;
        editsCollection = false;
    }

    /**
     * Метод исполнения
     *
     * @param ui объект, через который ведется взаимодействие с пользователем.
     */
    @Override
    public String execute(UserInterface ui, StorageInteraction storageInteraction, String arguments, DataBaseCenter dbc, User user) throws IOException {
        int result;
        if (ValidationClass.validateFuelType(arguments, true, ui)) {
            FuelType fuelType = FuelType.valueOf(arguments.toUpperCase());
            result = storageInteraction.countLessThanFuelType(fuelType);
        } else return ("Неверно введено значение Fuel Type.");
        return ("Элементов с типом топлива, меньше указанного: " + result);
    }
}
