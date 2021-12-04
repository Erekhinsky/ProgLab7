package common.commands.user;

import server.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import common.ui.CommandCenter;
import common.ui.UserInterface;
import server.interaction.StorageInteraction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Класс команды execute_script.
 */
public class ExecuteScript extends Command {

    private static final ArrayList<String> paths = new ArrayList<>();

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public ExecuteScript() {
        cmdLine = "execute_script";
        description = "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме";
        options = "Параметры: Путь к исполняемому файлу";
        needsObject = false;
        argumentAmount = 1;
        serverCommandLabel = false;
        editsCollection = true;
    }

    /**
     * Метод исполнения
     *
     * @param arguments необходимые для исполнения аргументы.
     */
    public String execute(StorageInteraction storageInteraction, String arguments, DataBaseCenter dbc, User user) throws IOException {

        UserInterface scriptInteraction = new UserInterface(new FileReader(arguments), false, new OutputStreamWriter(System.out));

        StringBuilder result = new StringBuilder();
        Path p = Paths.get(arguments);
        boolean exists = Files.exists(p);
        boolean isDirectory = Files.isDirectory(p);
        boolean isFile = Files.isRegularFile(p);
        try {
            if (exists && !isDirectory && isFile) {
                paths.add(arguments);
                String line;
                while (scriptInteraction.hasNextLine()) {
                    line = scriptInteraction.read();
                    String cmdLine = line.split(" ")[0];
                    if (!CommandCenter.getInstance().receiveAllStringCommands().contains(cmdLine)) {
                    } else {
                        String cmdArgument;
                        try {
                            cmdArgument = line.split(" ")[1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            cmdArgument = null;
                        }
                        Command cmd = CommandCenter.getInstance().getCmdCommand(cmdLine);
                        if (cmd.getClass().toString().contains(".Login") || cmd.getClass().toString().contains(".Register"))
                            throw new InvalidParameterException();
                        cmd.setUser(user);
                        if (cmd.getArgumentAmount() == 0) {
                            result.append(CommandCenter.getInstance().executeCommand(cmd, storageInteraction, dbc)).append("\n");
                        } else {
                            if (cmd.getArgumentAmount() == 1 && cmd.getNeedsObject()) {
                                Vehicle vehicle = scriptInteraction.readVehicle(scriptInteraction);
                                result.append(CommandCenter.getInstance().executeCommand(cmd, storageInteraction, vehicle, dbc)).append("\n");
                            }
                            if (cmd.getArgumentAmount() == 1 && !cmd.getNeedsObject()) {
                                if (cmd.getCmdLine().equals("execute_script")) {
                                    paths.forEach(System.out::println);
                                    if (!paths.contains(cmdArgument)) {
                                        paths.add(cmdArgument);
                                        result.append(CommandCenter.getInstance().executeCommand(cmd, cmdArgument, storageInteraction, dbc)).append("\n");
                                    } else {
                                        paths.clear();
                                        throw new InvalidAlgorithmParameterException("Выполнение скрипта остановлено, т.к. возможна рекурсия");
                                    }
                                } else
                                    result.append(CommandCenter.getInstance().executeCommand(cmd, cmdArgument, storageInteraction, dbc)).append("\n");
                            }
                            if (cmd.getArgumentAmount() == 2 && cmd.getNeedsObject()) {
                                Vehicle vehicle = scriptInteraction.readVehicle(scriptInteraction);
                                result.append(CommandCenter.getInstance().executeCommand(cmd, cmdArgument, storageInteraction, vehicle, dbc)).append("\n");
                            }
                        }
                    }
                }
                paths.clear();
                result.append("Скрипт выполнен");
            } else result.append("Скрипт не выполнен, что-то не так с файлом.");
            System.out.println("Я выполнился");
            return result.toString();
        } catch (InvalidParameterException e) {
            paths.clear();
            result.append("Неверный скрипт");
            return result.toString();
        } catch (FileNotFoundException e) {
            paths.clear();
            result.append("В качестве аргумента указан путь к несуществующему файлу");
            return result.toString();
        } catch (NoSuchElementException e) {
            paths.clear();
            result.append("Скрипт некорректен, проверьте верность введенных команд");
            return result.toString();
        } catch (InvalidAlgorithmParameterException e) {
            result.append("Выполнение скрипта остановлено, т.к. возможна рекурсия");
            return result.toString();
        } catch (IncorrectValueException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
