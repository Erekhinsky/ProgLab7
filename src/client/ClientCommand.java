package client;

import common.User;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import common.ui.UserInterface;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ClientCommand implements Serializable {

    protected int argumentAmount;
    protected String options;
    protected String argument;
    protected String commandLine;

    protected boolean needsObject;
    protected Vehicle object;

    protected User user;

    protected boolean serverCommandLabel;

    ClientCommand(String commandLine, String argument, User user){

    }

    public void set(ClientCommand clientCommand, UserInterface ui) throws IOException, IncorrectValueException {
        if (!(clientCommand.getArgument() == null)) {
            if (commandVehicle.contains(clientCommand.getCommandLine())) {
                ui.readVehicle(ui);
            }
        }
    }

    private final List<String> commandVehicle = Arrays.asList("update","add", "add_if_max", "remove_lower");


    public String getOptions() {
        return options;
    }

    public boolean getNeedsObject() {
        return needsObject;
    }

    public int getArgumentAmount() {
        return argumentAmount;
    }

    public void setObject(Vehicle object) {
        this.object = object;
    }

    public Vehicle getObject() {
        return this.object;
    }

    public void setArgument(String arg) {
        this.argument = arg;
    }

    public String getArgument() {
        return this.argument;
    }

    public void setCommandLine(String commandLine1) {
        this.commandLine = commandLine1;
    }

    public String getCommandLine() {
        return this.commandLine;
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
}
