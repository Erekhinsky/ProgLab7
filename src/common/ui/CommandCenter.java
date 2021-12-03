package common.ui;

import common.DataBaseCenter;
import common.commands.abstracts.Command;
import common.commands.server.Login;
import common.commands.server.Register;
import common.commands.server.ServerInfo;
import common.commands.user.*;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import server.Server;
import server.interaction.StorageInteraction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Класс управления командами.
 */
public class CommandCenter {

    /**
     * Логер.
     */
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final ReentrantLock collectionLock = new ReentrantLock();

    /**
     * Список команд.
     */
    public final HashMap<String, Command> commands = new HashMap<>();
    /**
     * Объект управления командами.
     */
    public static CommandCenter commandCenter = new CommandCenter();

    /**
     * Конструктор управления командами, где добавляются все команды.
     */
    public CommandCenter() {
        addCmd(new Help());
        addCmd(new Info());
        addCmd(new Show());
        addCmd(new Update());
        addCmd(new Clear());
        addCmd(new AddIfMax());
        addCmd(new ExecuteScript());
        addCmd(new Exit());
        addCmd(new CountLessThanFuelType());
        addCmd(new RemoveLower());
        addCmd(new Add());
        addCmd(new FilterLessThanEnginePower());
        addCmd(new RemoveFirst());
        addCmd(new RemoveById());
        addCmd(new PrintFieldDescendingDistanceTravelled());

//        addCmd(new Save());
        addCmd(new ServerInfo());
        addCmd(new Login());
        addCmd(new Register());
    }

    /**
     * Метод добавления команды в список команд.
     *
     * @param command Команда.
     */
    public void addCmd(Command command) {
        commands.put(command.getCmdLine(), command);
    }

    /**
     * Метод, распознающий команду в строке, введенной пользователем.
     *
     * @param cmdLine Строка, содержащая команду.
     * @return Объект класса соответствующей команды.
     */
    public Command getCmdCommand(String cmdLine) {
        return commands.getOrDefault(cmdLine, null);
    }

    /**
     * Метод, возвращающий единственный объект класса. Реализация шаблона "Синглтон".
     *
     * @return Объект центра управления командами.
     */
    public static CommandCenter getInstance() {
        if (commandCenter == null)
            return new CommandCenter();
        return commandCenter;
    }

    /**
     * Метод, возвращающий список команд.
     *
     * @return Список команд.
     */
    public List<Command> receiveAllCommands() {
        return commands.keySet().stream().map(commands::get).collect(Collectors.toList());
    }


    /**
     * Методы исполнения команды.
     *
     * @param ui Объект взаимодействия с пользователем.
     */
    public String executeCommand(UserInterface ui, Command cmd, StorageInteraction storageInteraction) throws IOException {
        logger.log(Level.INFO, "Executing server command" + "\n");
        return cmd.execute(ui, storageInteraction, cmd.getUser());
    }

    public String  executeCommand(UserInterface ui, Command cmd, StorageInteraction storageInteraction, DataBaseCenter dataBaseCenter) throws IOException {
        collectionLock.lock();
        logger.log(Level.INFO, "Executing user command with no arguments" + "\n");
        String result = cmd.execute(ui, storageInteraction, dataBaseCenter, cmd.getUser());
        if (collectionLock.isLocked()) collectionLock.unlock();
        return result;
    }

    public String executeCommand(UserInterface ui, Command cmd, String argument, StorageInteraction storageInteraction, DataBaseCenter dataBaseCenter) throws IOException {
        collectionLock.lock();
        logger.log(Level.INFO, "Executing user command with a string argument" + "\n");
        String result = cmd.execute(ui, storageInteraction, argument, dataBaseCenter, cmd.getUser());
        if (collectionLock.isLocked()) collectionLock.unlock();
        return result;
    }

    public String executeCommand(UserInterface ui, Command cmd, StorageInteraction storageInteraction, Vehicle vehicle, DataBaseCenter dbc) throws IncorrectValueException, IOException {
        collectionLock.lock();
        logger.log(Level.INFO, "Executing user command with an object argument" + "\n");
        String result = cmd.execute(ui, storageInteraction, vehicle, dbc, cmd.getUser());
        if (collectionLock.isLocked()) collectionLock.unlock();
        return result;
    }

    public String executeCommand(UserInterface ui, Command cmd, String argument, StorageInteraction storageInteraction, Vehicle vehicle, DataBaseCenter dbc) throws IOException {
        collectionLock.lock();
        logger.log(Level.INFO, "Executing user command with two arguments" + "\n");
        String result = cmd.execute(ui, storageInteraction, argument, vehicle, dbc, cmd.getUser());
        if (collectionLock.isLocked()) collectionLock.unlock();
        return result;
    }

//    public String executeCommand(UserInterface ui, Command cmd, boolean success, DataBaseCenter dbc) {
//        collectionLock.lock();
//        logger.log(Level.INFO, "Executing user command with two string arguments");
//        String result = cmd.execute(ui, success);
//        if (collectionLock.isLocked()) collectionLock.unlock();
//        return result;
//    }

//    public String executeCommand(Command cmd) {
//        if (cmd.getServerCommandLabel()) {
//
//        }
//        if (cmd.getNeedsObject()) {
//
//        }
//        if (cmd.isEditsCollection()) {
//
//        }
//        if (cmd.getArgumentAmount() == 0) {
//            if (cmd.getNeedsObject()) {
//
//            } else {
//
//            }
//        }
//        else if (cmd.getNeedsObject()) {
//
//        } else {
//
//        }
//        if (cmd.getUser() != null) {
//
//        }
//        if (cmd.getNeedsObject())
//        String result = cmd.execute();
//        return result;
//    }
}
