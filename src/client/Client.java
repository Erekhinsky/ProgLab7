package client;

import common.SerializationTool;
import common.User;
import common.commands.abstracts.Command;
import common.commands.server.Login;
import common.commands.server.Register;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import common.ui.CommandCenter;
import common.ui.UserInterface;

import java.io.*;
import java.net.*;
import java.util.*;

import static client.PasswordEncoder.getHexString;

public class Client {

    private SocketAddress address;
    private DatagramSocket socket;
    private final UserInterface userInterface = new UserInterface(new InputStreamReader(System.in), true, new OutputStreamWriter(System.out));
    private User user = null;

    public static void main(String[] args) {
        Client client = new Client();
        boolean tryingToConnect = true;
        while (tryingToConnect) {
            try {
                client.connect();
                boolean entrance = false;
                try {
                    while (!entrance)
                        entrance = client.authorisation();
                } catch (IOException e) {
                    System.out.println("Невозможно завершить авторизацию");
                }
                client.run();
            } catch (IOException | IncorrectValueException | ClassNotFoundException e) {
                System.out.println("Сервер недоступен.");
                if (ask() <= 0) {
                    tryingToConnect = false;
                }
            }
        }
        client.getSocket().close();
        System.out.println("Завершение работы.");
    }

    public void connect() throws IOException {
        int PORT = 8725;
        String HOST = "localhost";
        address = new InetSocketAddress(HOST, PORT);
        socket = new DatagramSocket();
        userInterface.showMessage("Попытка подключения");
    }

    public void run() throws IOException, IncorrectValueException, ClassNotFoundException {
        sendServerCommand(CommandCenter.getInstance().getCmdCommand("server_info"));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите команду: (Введите \"help\" чтобы получить информацию о командах.)");
        String command = "";
        while (scanner.hasNextLine() && !command.equals("exit")) {
            String[] input = scanner.nextLine().trim().split(" ");
            command = input[0];
            Command cmd = CommandCenter.getInstance().getCmdCommand(input[0]);
            if (!(cmd == null) && !cmd.getServerCommandLabel()) {
                byte[] cmdByte;
                if (cmd.getArgumentAmount() == 0) {
                    cmdByte = SerializationTool.serialize(cmd);
                    send(cmdByte);
                    userInterface.showMessage(receive());
                }
                if (cmd.getArgumentAmount() == 1 && cmd.getNeedsObject()) {
                    cmd.setObject(userInterface.readVehicle(userInterface));
                    cmdByte = SerializationTool.serialize(cmd);
                    send(cmdByte);
                    userInterface.showMessage(receive());
                }
                if (cmd.getArgumentAmount() == 1 && !cmd.getNeedsObject()) {
                    cmd.setArgument(userInterface.readArgument("Введите " + cmd.getOptions(), false));
                    cmdByte = SerializationTool.serialize(cmd);
                    send(cmdByte);
                    userInterface.showMessage(receive());
                }
                if (cmd.getArgumentAmount() == 2 && cmd.getNeedsObject()) {
                    cmd.setArgument(userInterface.readArgument("Введите " + cmd.getOptions(), false));
                    Vehicle vehicle = userInterface.readVehicle(userInterface);
                    cmd.setObject(vehicle);
                    cmdByte = SerializationTool.serialize(cmd);
                    send(cmdByte);
                    userInterface.showMessage(receive());
                }
            } else {
                userInterface.showMessage("Введена несуществующая команда, используйте команду help, " +
                        "чтобы получить список возможных команд");
            }
        }
        scanner.close();
        System.out.println("Завершение работы.");
        System.exit(0);
    }

    public DatagramSocket getSocket() {
        return this.socket;
    }

    public void send(byte[] bytes) throws IOException {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
        socket.send(packet);
    }

    public String receive() throws IOException {
        byte[] bytes = new byte[1000000];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        socket.setSoTimeout(2 * 1000);
        socket.receive(packet);
        return (String) SerializationTool.deserialize(bytes);
    }

    public static int ask() {
        Scanner scanner = new Scanner(System.in);
        String answer;
        System.out.println("Попробовать подключиться снова? (\"Да\"/\"Нет\")");
        while (scanner.hasNextLine()) {
            answer = scanner.nextLine();
            if (answer.equals("Да") || answer.equals("да")) {
                return 1;
            } else if (answer.equals("Нет") || answer.equals("нет")) {
                return 0;
            } else {
                System.out.println("Введите: \"да\" или \"нет\".");
            }
        }
        return -1;
    }

    public boolean authorisation() throws IOException {
        String action = userInterface.readArgument("Здравствуйте! Введите login, если вы уже зарегистрированы. В ином случае, введите register.", false);
        if (action.equals("login")) {
            return login();
        } else {
            if (action.equals("register")) {
                return register();
            } else return false;
        }
    }

    public boolean login() {
        byte[] cmdByte;
        try {
            String login = userInterface.readArgument("Введите ваш логин:", false);
            String password = getHexString(userInterface.readArgument("Введите пароль: ", false));
            Command cmd = new Login();
            User user = new User(login, password);
            cmd.setUser(user);
            cmdByte = SerializationTool.serialize(cmd);
            sendServerCommand(cmd);
//            if (answer.contains(" не "))
//                return false;
//            else {
//                this.user = user;
//                System.out.println("Вход успешен!");
//                return true;
//            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register() {
        try {
            byte[] cmdByte;
            String login = userInterface.readArgument("Придумайте логин:", false);
            String password = "";
            do {
                password = getHexString(userInterface.readArgument("Введите пароль", false));
            } while (password.isEmpty());
            Command cmd = new Register();
            User user = new User(login, password);
            cmd.setUser(user);
            cmdByte = SerializationTool.serialize(cmd);
            sendServerCommand(cmd);
//            String answer = receive();
//            if (answer.contains(" не "))
//                return false;
//            else {
//                this.user = user;
//                System.out.println("Вход успешен!");
//                return true;
//            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendServerCommand(Command cmd) throws IOException {
        byte[] cmdByte;
        cmdByte = SerializationTool.serialize(cmd);
        DatagramPacket packet = new DatagramPacket(Objects.requireNonNull(cmdByte), cmdByte.length, address);
        socket.send(packet);
        userInterface.showMessage(receive());
    }
}