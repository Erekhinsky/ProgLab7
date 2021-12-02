package server;

import common.commands.abstracts.Command;

import java.io.IOException;
import java.util.concurrent.RecursiveTask;

public class TaskForRequest extends RecursiveTask<Command> {

    Server serv;

    TaskForRequest(Server server){
        this.serv = server;
    }

    @Override
    protected Command compute() {
        try {
            return (Command) serv.readRequest();
        } catch (IOException e) {
            e.printStackTrace();
        } return null;
    }
}
