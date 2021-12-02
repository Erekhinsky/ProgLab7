package common.commands.abstracts;

import common.DataBaseCenter;
import common.User;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import common.ui.UserInterface;
import server.interaction.StorageInteraction;

import java.io.IOException;
import java.io.Serializable;

/**
 * Класс, от которого наследуются команды.
 */
public abstract class Command implements Serializable {

    /**
     * Строка вызова команды.
     */
    protected String cmdLine;

    /**
     * Полное описание команды.
     */
    protected String description;
    protected String options;
    protected boolean needsObject;
    protected int argumentAmount;
    protected boolean serverCommandLabel;
    protected boolean editsCollection;
    protected StringBuilder messageToClient;

    /**
     * Аргументы команды.
     */
    protected String argument;
    protected Vehicle object;
    protected User user;

    /**
     * Методы исполнения команды.
     *
     * @param ui       Объект для взаимодействия с пользователем.
     */
    public String execute(UserInterface ui, StorageInteraction si, String argument, Vehicle vehicle, DataBaseCenter dbc, User user) throws IOException {
        return null;
    }

    public String execute(UserInterface ui, StorageInteraction si, Vehicle vehicle, DataBaseCenter dbc, User user) throws IOException, IncorrectValueException {
        return null;
    }

    public String execute(UserInterface ui, StorageInteraction si, String argument, DataBaseCenter dbc, User user) throws IOException {
        return null;
    }

    public String execute(UserInterface ui, StorageInteraction si, DataBaseCenter dbc, User user) throws IOException {
        return null;
    }

    public String execute(UserInterface ui, StorageInteraction si, User user) throws IOException {
        return null;
    }



    /**
     * Пустой конструктор Command.
     */
    public Command() {
    }

    /**
     * Возвращает строку вызова команды.
     *
     * @return Строка вызова команды.
     */
    public String getCmdLine() {
        return cmdLine;
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Возвращает необходимые аргументы.
     *
     * @return Необходимые аргументы для команды.
     */
    public String getOptions() {
        return options;
    }


    /**
     * Возвращает необходимость в дополнительных для исполнения команды.
     *
     * @return True - нужно, false - не нужно.
     */
    public boolean getNeedsObject() {
        return needsObject;
    }

    public int getArgumentAmount() {
        return argumentAmount;
    }

    /**
     * Добавляет объект для исполнения.
     *
     * @param object Объект коллекции.
     */
    public void setObject(Vehicle object) {
        this.object = object;
    }

    /**
     * Возвращает объект для исполнения.
     *
     * @return Объект коллекции.
     */
    public Vehicle getObject() {
        return this.object;
    }

    /**
     * Добавляет аргументы для исполнения.
     *
     * @param arg Аргументы.
     */
    public void setArgument(String arg) {
        this.argument = arg;
    }

    /**
     * Возвращает аргументы для исполнения.
     *
     * @return Аргументы для исполнения.
     */
    public String getArgument() {
        return this.argument;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public boolean getServerCommandLabel() {
        return serverCommandLabel;
    }

    public boolean isEditsCollection() {
        return this.editsCollection;
    }

}
