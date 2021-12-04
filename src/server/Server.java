package server;

import common.SerializationTool;
import common.User;
import common.commands.abstracts.Command;
import common.commands.server.Login;
import common.commands.server.Register;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import common.ui.CommandCenter;
import common.ui.UserInterface;
import server.collection.VehicleStorage;
import server.interaction.StorageInteraction;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
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

    private static final StringBuilder stringMessage = new StringBuilder();
    private static boolean firstOpen = true;
    boolean authorise = false;

    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    private DataBaseCenter dbc = new DataBaseCenter();

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
        if (cmd.getCmdLine().equals("register")) {
            authorise = authoriseUser(cmd.getUser(), "new");
            if (authorise) result = "Регистрация успешна!";
            else result = "Регистрация не удалась\n";
        }
        if (cmd.getCmdLine().equals("login")) {
            authorise = authoriseUser(cmd.getUser(), "old");
            if (authorise) result = "Авторизация успешна!";
            else result = "Авторизация не удалась\n" + "";
        }
        if (!cmd.getCmdLine().equals("login") && !cmd.getCmdLine().equals("register") && authorise) {
            if (cmd.getServerCommandLabel()) {
                logger.log(Level.INFO, "Выполнение серверной команды - " + cmd.getCmdLine() + "\n");
                result = CommandCenter.getInstance().executeCommand(cmd, storageInteraction);
            } else {
                if (cmd.getArgumentAmount() == 0) {
                    logger.log(Level.INFO, "Выполнение команды без аргументов - " + cmd.getCmdLine() + "\n");
                    result = CommandCenter.getInstance().executeCommand(cmd, storageInteraction, dbc) + "\nВведите команду:";
                }
                if (cmd.getArgumentAmount() == 1 && !cmd.getNeedsObject()) {
                    logger.log(Level.INFO, "Выполнение команды с аргументом - " + cmd.getCmdLine() + "\n");
                    argument = cmd.getArgument();
                    result = CommandCenter.getInstance().executeCommand(cmd, argument, storageInteraction, dbc) + "\nВведите команду:";
                }
                if (cmd.getArgumentAmount() == 1 && cmd.getNeedsObject()) {
                    logger.log(Level.INFO, "Выполнение команды с аргументом-объектом - " + cmd.getCmdLine() + "\n");
                    vehicle = cmd.getObject();
                    result = CommandCenter.getInstance().executeCommand(cmd, storageInteraction, vehicle, dbc) + "\nВведите команду:";
                }
                if (cmd.getArgumentAmount() == 2 && cmd.getNeedsObject()) {
                    logger.log(Level.INFO, "Выполнение команды с аргументом и аргументом-объектом - " + cmd.getCmdLine() + "\n");
                    argument = cmd.getArgument();
                    vehicle = cmd.getObject();
                    result = CommandCenter.getInstance().executeCommand(cmd, argument, storageInteraction, vehicle, dbc) + "\nВведите команду:";
                }
            }
        }
        if (result != null) {
            return result;
        } else return "Вы не авторизованы. Работа с базой данных невозможна.";
    }

    public void sendAnswer(String str) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Objects.requireNonNull(SerializationTool.serialize(str)));
        datagramChannel.send(byteBuffer, socketAddress);
        logger.log(Level.INFO, "Сервер отправил ответ клиенту." + "\n");
    }

    public void run() {
        try {
            storageInteraction = new StorageInteraction(vehicleStorage);
            try {
                logger.log(Level.INFO, "Чтение коллекции из базы данных" + "\n");
                dbc.createTable();
                dbc.retrieveCollectionFromDB(storageInteraction);
            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "Данные недействительны" + "\n", e);
                System.exit(-1);
            } catch (DateTimeParseException e) {
                logger.log(Level.SEVERE, "Форматирование даты недопустимо" + "\n", e);
                System.exit(-1);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.log(Level.SEVERE, "Недостаточно аргументов" + "\n", e);
                System.exit(-1);
            } catch (IllegalArgumentException e) {
                logger.log(Level.SEVERE, "Недействительные аргументы" + "\n", e);
                System.exit(-1);
            }
            logger.log(Level.INFO, "Коллекция успешно прочитана" + "\n");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Server.logger.log(Level.INFO, "ВСЕМ ББ" + "\n");
                System.exit(-1);
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
                        if (stringMessage != null) {

                            Command cmd = forkJoinPool.invoke(new TaskForRequest(this));

                            Future<String> executor = executorService.submit(() -> {
                                try {
                                    return executeCommand(cmd);
                                } catch (IOException | IncorrectValueException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            });

                            String commandResult = executor.get();

                            executorService.submit(() -> {
                                try {
                                    sendAnswer(stringMessage + "\n" + commandResult);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            setStringMessage(null);
                        } else {

                            Command cmd = forkJoinPool.invoke(new TaskForRequest(this));

                            Future<String> executor = executorService.submit(() -> {
                                try {
                                    return executeCommand(cmd);
                                } catch (IOException | IncorrectValueException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            });

                            String commandResult = executor.get();

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
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage() + "\n");
        }
    }

    public static void setStringMessage(String stringMessage1) {
        if (stringMessage1 != null) {
            stringMessage.append(stringMessage1);
        }
    }

    public static String getStringMessage() {
        return stringMessage.toString();
    }

    public boolean authoriseUser(User user, String existence) throws IOException, SQLException {
        if (existence.equals("new")) {
            if (dbc.addUser(user)) {
                CommandCenter.getInstance().executeCommand(new Register(), storageInteraction);
                logger.log(Level.INFO, "<" + user.getLogin() + ">" + ": " + "Register success" + "\n");
                return true;
            } else {
                CommandCenter.getInstance().executeCommand(new Register(), storageInteraction);
                logger.log(Level.INFO, "<" + user.getLogin() + ">" + ": " + "Register fail" + "\n");
                return false;
            }
        } else {
            if (dbc.loginUser(user)) {
                CommandCenter.getInstance().executeCommand(new Login(), storageInteraction);
                logger.log(Level.INFO, "<" + user.getLogin() + ">" + ": " + "Login success" + "\n");
                return true;
            } else {
                CommandCenter.getInstance().executeCommand(new Login(), storageInteraction);
                logger.log(Level.INFO, "<" + user.getLogin() + ">" + ": " + "Login fail" + "\n");
                return false;
            }
        }
    }

    public String[] getArguments() {
        return arguments;
    }
}
