package server.utils;

import common.elementsOfCollection.FuelType;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Класс-утилита для проверки значений на соответствие условиям.
 */
public class ValidationClass {

    /**
     * Метод проверки названия.
     * Является потенциально бесполезным ввиду того, что в названии транспорта могут быть не только буквы.
     *
     * @param value       Строка ввода.
     * @param interaction Режим взаимодействия.
     * @return Результат проверки.
     * @throws IOException В случае ошибки ввода/вывода.
     */
    public static boolean validateName(String value, boolean interaction) throws IOException {
        if (value.contains(" ")) {
            if (interaction) {
                System.out.println("Это поле должно быть без пробелов");
            } else {
                throw new InterruptedIOException();
            }
            return false;
        } else return true;
    }

    /**
     * Метод проверки даты.
     *
     * @param value       Строка ввода.
     * @param interaction Режим взаимодействия.
     * @return Результат проверки.
     * @throws IOException В случае ошибки ввода/вывода.
     */
    public static boolean validateDate(String value, boolean interaction) throws IOException {
        if (!(value == null)) {
            try {
                LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return true;
            } catch (DateTimeParseException e) {
                if (interaction)
                    System.out.println("Введена дата неверного формата");
                else
                    throw new InterruptedIOException();
                return false;
            }
        } else return true;
    }

    /**
     * Метод проверки значения типа Double.
     *
     * @param value       Строка ввода.
     * @param interaction Режим взаимодействия.
     * @param nullable    Может или не может быть null.
     * @return Результат проверки.
     * @throws IOException В случае ошибки ввода/вывода.
     */
    public static boolean validateDouble(String value, boolean interaction, boolean nullable) throws IOException {
        if (value == null && nullable)
            return true;
        else {
            if (value == null)
                return false;
            else {
                try {
                    Double.parseDouble(value);
                    return true;
                } catch (NumberFormatException e) {
                    if (interaction)
                        System.out.println("Значение должно быть вещественным числом");
                    else
                        throw new InterruptedIOException();
                    return false;
                }
            }
        }
    }

    /**
     * Метод проверки значения типа long.
     *
     * @param value       строка ввода.
     * @param interaction режим взаимодействия.
     * @param nullable    может или не можеть быть null.
     * @return true/false.
     * @throws IOException в случае ошибки ввода/вывода.
     */
    public static boolean validateLong(String value, boolean interaction, boolean nullable) throws IOException {
        if (value == null && nullable)
            return true;
        else {
            if (value == null)
                return false;
            else if (Long.parseLong(value) <= 0) {
                if (interaction) {
                    System.out.println("Введенное значение должно быть больше 0.");
                }
                return false;
            } else {
                try {
                    Long.parseLong(value);
                    return true;
                } catch (NumberFormatException e) {
                    if (interaction)
                        System.out.println("Значение должно быть числом.");
                    else
                        throw new InterruptedIOException();
                    return false;
                }
            }
        }
    }

    /**
     * Метод проверки значения типа Double.
     *
     * @param value       Строка ввода.
     * @param interaction Режим взаимодействия.
     * @param nullable    Может или не может быть null.
     * @return Результат проверки.
     * @throws IOException В случае ошибки ввода/вывода.
     */
    public static boolean validateFloat(String value, boolean interaction, boolean nullable) throws IOException {
        if (value == null && nullable)
            return true;
        else {
            if (value == null)
                return false;
            else if (Float.parseFloat(value) <= 0) {
                if (interaction) {
                    System.out.println("Введенное значение должно быть больше 0.");
                }
                return false;
            } else
                try {
                    Float.parseFloat(value);
                    return true;
                } catch (NumberFormatException e) {
                    if (interaction)
                        System.out.println("Значение должно быть вещественным числом");
                    else
                        throw new InterruptedIOException();
                    return false;
                }
        }
    }


    /**
     * Метод проверки типа топлива транспорта.
     *
     * @param value       Строка ввода.
     * @param interaction Режим взаимодействия.
     * @return Результат проверки.
     * @throws IOException В случае ошибки ввода/вывода.
     */
    public static boolean validateFuelType(String value, boolean interaction) throws IOException {
        if (!(value == null)) {
            try {
                return FuelType.getPossibleValues().contains(FuelType.valueOf(value.toUpperCase()));
            } catch (IllegalArgumentException e) {
                if (interaction)
                    System.out.println("Указано некорректное значение");
                else
                    throw new InterruptedIOException();
                return false;
            }
        } else return true;
    }
}
