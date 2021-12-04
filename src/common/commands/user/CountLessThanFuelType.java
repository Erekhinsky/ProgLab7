package common.commands.user;

import server.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.ui.UserInterface;
import common.elementsOfCollection.FuelType;
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
     */
    @Override
    public String execute(StorageInteraction storageInteraction, String arguments, DataBaseCenter dbc, User user) throws IOException {
        int result;
        if (ValidationClass.validateFuelType(arguments, true)) {
            FuelType fuelType = FuelType.valueOf(arguments.toUpperCase());
            result = storageInteraction.countLessThanFuelType(fuelType);
        } else return ("Неверно введено значение Fuel Type.");
        return ("Элементов с типом топлива, меньше указанного: " + result);
    }
}
