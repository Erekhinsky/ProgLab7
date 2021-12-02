package server;

import common.DataBaseCenter;
import common.SerializationTool;
import common.User;
import common.commands.abstracts.Command;
import common.commands.server.Login;
import common.commands.server.Register;
import common.commands.server.Save;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import common.ui.CommandCenter;
import common.ui.UserInterface;
import server.collection.VehicleStorage;
import server.interaction.StorageInteraction;
import server.utils.FileHelper;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {
    public static final Logger logger = Logger.getLogger(Server.class.getName());
    private String[] arguments;
    private final UserInterface userInterface = new UserInterface(new InputStreamReader(System.in), true, new OutputStreamWriter(System.out));
    private final VehicleStorage vehicleStorage = new VehicleStorage();
    private StorageInteraction storageInteraction;

    private SocketAddress socketAddress;
    public DatagramChannel datagramChannel;
    private Selector selector;

    private static StringBuilder stringMessage = new StringBuilder();
    private static boolean firstOpen = true;
    private boolean authorisation = false;

    private static ExecutorService executorService = Executors.newCachedThreadPool();
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    private DataBaseCenter dbc = new DataBaseCenter();

    public static ExecutorService getExecutorService() {
        return executorService;
    }


    public Server(DataBaseCenter dataBaseCenter) {
        int PORT = 8725;
        this.socketAddress = new InetSocketAddress(PORT);
        this.dbc = dataBaseCenter;
        if (firstOpen) logger.log(Level.INFO, "Сервер начал работу." + "\n");
        Server.firstOpen = false;
    }

    public static void main(String[] args) {
        logger.log(Level.INFO, "commons.app.server запущен." + "\n");
        Server server = new Server(new DataBaseCenter());
        server.setArguments(args);
        server.run();
    }

    public void setArguments(String[] arguments) {
        logger.log(Level.INFO, "Установка аргументов сервера" + "\n");
        this.arguments = arguments;
    }

    public void openChannel() throws IOException {
        selector = Selector.open();
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.register(selector, SelectionKey.OP_READ);
        datagramChannel.bind(socketAddress);
        logger.log(Level.INFO, "Канал открыт и привязан к адресу." + "\n");
    }

    public Object readRequest() throws IOException {
        byte[] buffer = new byte[65536];
        ByteBuffer bufferAnswer = ByteBuffer.wrap(buffer);
        socketAddress = datagramChannel.receive(bufferAnswer);
        logger.log(Level.INFO, "Запрос на чтение сервера." + "\n");
        return SerializationTool.deserialize(bufferAnswer.array());
    }

    public String executeCommand(Command cmd) throws IOException, IncorrectValueException, SQLException {
        String argument;
        String result = null;
        Vehicle vehicle;
        if (cmd.getCmdLine().equals("register"))
            authorisation = authoriseUser(cmd.getUser(), "new");
        if (cmd.getCmdLine().equals("login"))
            authorisation = authoriseUser(cmd.getUser(), "old");
        if (!cmd.getCmdLine().equals("login") && !(cmd.getCmdLine().equals("register"))) {
            authorisation = true;
        }
        if (authorisation) {
            if (cmd.getCmdLine().equals("exit")) {
                Command save = new Save();
                save.setUser(cmd.getUser());
                logger.log(Level.INFO, "Начато сохранение коллекции" + "\n");
                result = CommandCenter.getInstance().executeCommand(userInterface, save, storageInteraction, dbc);
            } else {
                if (cmd.getServerCommandLabel()) {
                    logger.log(Level.INFO, "Выполнение серверной команды - " + cmd.getCmdLine() + "\n");
                    result = CommandCenter.getInstance().executeCommand(userInterface, cmd, storageInteraction);
                } else {
                    if (cmd.getArgumentAmount() == 0) {
                        logger.log(Level.INFO, "Выполнение команды без аргументов - " + cmd.getCmdLine() + "\n");
                        result = CommandCenter.getInstance().executeCommand(userInterface, cmd, storageInteraction, dbc) + "\nВведите команду:";
                    }
                    if (cmd.getArgumentAmount() == 1 && !cmd.getNeedsObject()) {
                        logger.log(Level.INFO, "Выполнение команды с аргументом - " + cmd.getCmdLine() + "\n");
                        argument = cmd.getArgument();
                        result = CommandCenter.getInstance().executeCommand(userInterface, cmd, argument, storageInteraction, dbc) + "\nВведите команду:";
                    }
                    if (cmd.getArgumentAmount() == 1 && cmd.getNeedsObject()) {
                        logger.log(Level.INFO, "Выполнение команды с аргументом-объектом - " + cmd.getCmdLine() + "\n");
                        vehicle = cmd.getObject();
                        result = CommandCenter.getInstance().executeCommand(userInterface, cmd, storageInteraction, vehicle, dbc) + "\nВведите команду:";
                    }
                    if (cmd.getArgumentAmount() == 2 && cmd.getNeedsObject()) {
                        logger.log(Level.INFO, "Выполнение команды с аргументом и аргументом-объектом - " + cmd.getCmdLine() + "\n");
                        argument = cmd.getArgument();
                        vehicle = cmd.getObject();
                        result = CommandCenter.getInstance().executeCommand(userInterface, cmd, argument, storageInteraction, vehicle, dbc) + "\nВведите команду:";
                    }
                }
            }
        } else result = "Авторизация не удалась";
        if (result != null) {
            return result;
        } else return "Как так получилось, что ты оказался здесь";
    }

    public void sendAnswer(String str) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Objects.requireNonNull(SerializationTool.serialize(str)));
        datagramChannel.send(byteBuffer, socketAddress);
        logger.log(Level.INFO, "Сервер отправил ответ клиенту." + "\n");
    }

    public void run() {
        String path = "jsonFile.txt";
        try {
            System.getenv().get(path);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Файла нет.");
            path = null;
            System.exit(0);
        }

        try {
            storageInteraction = new StorageInteraction(vehicleStorage, path);
            FileHelper.readFile(path, storageInteraction);

            dbc.createTable();
            dbc.retrieveCollectionFromDB(storageInteraction);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Server.logger.log(Level.INFO, "Сохранение коллекции." + "\n");
                try {
                    CommandCenter.getInstance().executeCommand(userInterface, new Save(), storageInteraction);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            openChannel();

            while (true) {
                int SERVER_WAITING_TIME = 60 * 60 * 1000;
                int readyChannels = selector.select(SERVER_WAITING_TIME);
                if (readyChannels == 0) {
                    selector.close();
                    datagramChannel.close();
                    logger.log(Level.INFO, "Выключение сервера." + "\n");
                    storageInteraction.close();
                    break;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        datagramChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    }
                    if (key.isWritable()) {
                        if (stringMessage != null){

                            Command cmd = forkJoinPool.invoke(new TaskForRequest(this));

                            Future<String> executor = executorService.submit(()->{
                                try {
                                    String cmdString = executeCommand(cmd);
                                    System.out.println(cmdString);
                                    return cmdString;
                                } catch (IOException | IncorrectValueException e) {
                                    e.printStackTrace();
                                } return null;
                            });

                            String commandResult = executor.get();
                            System.out.println(commandResult + " :::: ");

                            executorService.submit(() -> {
                                try {
                                    System.out.println("SM + CM =: \n");
                                    sendAnswer(stringMessage + "\n" + commandResult);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            stringMessage = null;

                        }
                        else {
                            Command cmd = forkJoinPool.invoke(new TaskForRequest(this));

                            Future<String> executor = executorService.submit(()->{
                                try {
                                    return executeCommand(cmd);
                                } catch (IOException | IncorrectValueException e) {
                                    e.printStackTrace();
                                } return null;
                            });

                            String commandResult = executor.get();
                            System.out.println(commandResult + " :::: ");

                            executorService.submit(() -> {
                                try {
                                    sendAnswer(commandResult);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        datagramChannel.register(selector, SelectionKey.OP_READ);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "В окружении нет файла с коллекцией: jsonFile.txt" + "\n");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage()+ "\n");
        }
    }

    public static void setStringMessage(String sm) {
        if (sm != null){
            stringMessage.append(sm);
        }
    }

    public static String getStringMessage() {
        return stringMessage.toString();
    }

    public boolean authoriseUser(User user, String existence) throws IOException, SQLException {
        if (existence.equals("new")) {
            if (dbc.addUser(user)) {
                CommandCenter.getInstance().executeCommand(userInterface, new Register(), storageInteraction);
                System.out.println(user.getLogin() + " " + "REG success");
                return true;
            } else {
                CommandCenter.getInstance().executeCommand(userInterface, new Register(), storageInteraction);
                System.out.println(user.getLogin() + " " + "REG fail");
                return false;
            }
        } else {
            if (dbc.loginUser(user)) {
                CommandCenter.getInstance().executeCommand(userInterface, new Login(), storageInteraction);
                System.out.println(user.getLogin() + " " + "LOG success");
                return true;
            } else {
                CommandCenter.getInstance().executeCommand(userInterface, new Login(), storageInteraction);
                System.out.println(user.getLogin() + " " + "LOG fail");
                return false;
            }
        }
    }

    public String[] getArguments() {
        return arguments;
    }
}
