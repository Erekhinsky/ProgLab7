package client;

import common.User;
import common.elementsOfCollection.Vehicle;

import java.io.Serializable;

public class ClientCommand implements Serializable {

    protected int argumentAmount;
    protected String options;
    protected String argument;

    protected boolean needsObject;
    protected Vehicle object;

    protected User user;

    protected boolean serverCommandLabel;

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
