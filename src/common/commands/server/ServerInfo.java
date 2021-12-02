package common.commands.server;

import common.DataBaseCenter;
import common.User;
import common.commands.abstracts.Command;
import common.ui.UserInterface;
import server.interaction.StorageInteraction;

import java.io.IOException;

public class ServerInfo extends Command {

    /**
     * Стандартный конструктор, добавляющий строку вызова и описание команды.
     */
    public ServerInfo() {
        cmdLine = "server_info";
        needsObject = false;
        argumentAmount = 0;
        serverCommandLabel = true;
        editsCollection = false;
    }

    /**
     * Метод исполнения
     *
     * @param ui объект, через который ведется взаимодействие с пользователем.
     * @return Результат команды.
     */
    @Override
    public String execute(UserInterface ui, StorageInteraction storageInteraction, DataBaseCenter dataBaseCenter, User user) throws IOException {
        return "SMTH";
    }

}
