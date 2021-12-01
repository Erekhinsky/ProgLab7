package common;

import common.commands.abstracts.Command;

import java.net.SocketTimeoutException;

public interface ConnectionSource {

    void setArguments(String[] arguments);

    Command receive() throws SocketTimeoutException;

    void processRequest(Command command);

    boolean authoriseUser(User user, String existence);

}
