package server;

import common.User;
import common.elementsOfCollection.Coordinates;
import common.elementsOfCollection.FuelType;
import common.elementsOfCollection.Vehicle;
import common.exception.IncorrectValueException;
import server.interaction.StorageInteraction;

import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class DataBaseCenter {
    private final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "postgres";
    private String password = "ykh666";

    public DataBaseCenter() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean addUser(User newUser) throws SQLException {
        try (java.sql.Connection connection = DriverManager.getConnection(URL, user, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement(QueryConstants.USER_INSERTION);
            preparedStatement.setString(1, newUser.getLogin());
            preparedStatement.setString(2, newUser.getPassword());
            System.out.println(newUser.getLogin() + " " + newUser.getPassword());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginUser(User loggingUser) {
        try (java.sql.Connection connection = DriverManager.getConnection(URL, user, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                if (resultSet.getString("username").equals(loggingUser.getLogin()) && resultSet.getString("password").equals(loggingUser.getPassword())) {
                    System.out.println("Вход успешен - DBC");
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addVehicle(Vehicle vehicle, User loggedUser) throws IncorrectValueException {
        try (java.sql.Connection connection = DriverManager.getConnection(URL, user, password)) {
            vehicle.setCreationDate(LocalDate.now());
            PreparedStatement preparedStatement = connection.prepareStatement(QueryConstants.ELEMENT_INSERTION, PreparedStatement.RETURN_GENERATED_KEYS);
            getVehicle(vehicle, loggedUser, preparedStatement);
            ResultSet generated = preparedStatement.getGeneratedKeys();
            if (generated.next())
                vehicle.setId(generated.getLong(1));
            else throw new SQLException();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void getVehicle(Vehicle vehicle, User loggedUser, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, vehicle.getName());
        preparedStatement.setDouble(2, vehicle.getX());
        preparedStatement.setDouble(3, vehicle.getY());
        preparedStatement.setDate(4, Date.valueOf(vehicle.getCreationDate()));
        preparedStatement.setLong(5, vehicle.getEnginePower());
        preparedStatement.setLong(6, vehicle.getNumberOfWheels());
        preparedStatement.setDouble(7, vehicle.getDistanceTravelled());
        if (vehicle.getFuelType() == null)
            preparedStatement.setNull(8, Types.VARCHAR);
        else preparedStatement.setString(8, vehicle.getFuelType().toString());
        preparedStatement.setString(9, loggedUser.getLogin());
        preparedStatement.execute();
    }

    public boolean updateVehicle(Vehicle vehicle, long id, User loggedUser) {
        try (java.sql.Connection connection = DriverManager.getConnection(URL, user, password)) {
            PreparedStatement statement = connection.prepareStatement("SELECT creationdate FROM vehicle WHERE id = ?");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            LocalDate creationDate = null;
            while (resultSet.next()) {
                creationDate = LocalDate.parse(String.valueOf(resultSet.getDate(1)));
            }
            vehicle.setCreationDate(creationDate);
            PreparedStatement preparedStatement = connection.prepareStatement(QueryConstants.ELEMENT_UPDATE);
            preparedStatement.setString(1, vehicle.getName());
            preparedStatement.setDouble(2, vehicle.getX());
            preparedStatement.setDouble(3, vehicle.getY());
            preparedStatement.setLong(4, vehicle.getEnginePower());
            preparedStatement.setLong(5, vehicle.getNumberOfWheels());
            preparedStatement.setDouble(6, vehicle.getDistanceTravelled());
            if (vehicle.getFuelType() == null)
                preparedStatement.setNull(7, Types.VARCHAR);
            else preparedStatement.setString(7, vehicle.getFuelType().toString());
            preparedStatement.setLong(8, id);
            preparedStatement.setString(9, loggedUser.getLogin());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeVehicle(long id, User loggedUser) {
        try (Connection connection = DriverManager.getConnection(URL, user, password)) {
            PreparedStatement statement = connection.prepareStatement(QueryConstants.ELEMENT_SELECT_FOR_DELETION);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
                System.out.println(loggedUser.getLogin());
                if (!resultSet.getString("username").equals(loggedUser.getLogin()))
                    return false;
            }
            Statement statement1 = connection.createStatement();
            statement1.executeUpdate("DELETE FROM vehicle WHERE id = " + id + "AND username = '" + loggedUser.getLogin() + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void retrieveCollectionFromDB(StorageInteraction storageInteraction) {
        Vector<Vehicle> collection = new Vector<>();
        try (java.sql.Connection connection = DriverManager.getConnection(URL, user, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM vehicle");
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                Coordinates coordinates = new Coordinates(resultSet.getDouble(3), resultSet.getDouble(4));
                LocalDate creationDate = LocalDate.parse(String.valueOf(resultSet.getDate(5)));
                long enginePower = resultSet.getLong(6);
                long numberOfWheels = resultSet.getLong(7);
                float distanceTravelled = resultSet.getFloat(8);
                FuelType fuelType = null;
                if (!(resultSet.getString(8) == null))
                    fuelType = FuelType.valueOf(resultSet.getString(9));
                Vehicle vehicle = new Vehicle(id, name, coordinates, creationDate, enginePower, numberOfWheels, distanceTravelled, fuelType);
                collection.add(vehicle);
            }
            storageInteraction.clear();
            storageInteraction.addAll(collection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean clearCollection(User loggedUser) {
        try (java.sql.Connection connection = DriverManager.getConnection(URL, user, password)) {
            String query = "SELECT * FROM vehicle";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                PreparedStatement deletion = connection.prepareStatement(QueryConstants.ELEMENT_DELETE);
                deletion.setLong(1, id);
                deletion.setString(2, loggedUser.getLogin());
                deletion.execute();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createTable() {
        try (Connection connection = DriverManager.getConnection(URL, user, password)) {
            PreparedStatement statement = connection.prepareStatement(QueryConstants.ID_GENERATING_SEQUENCE);
            statement.execute();
            statement = connection.prepareStatement(QueryConstants.TABLE);
            statement.execute();
            statement = connection.prepareStatement(QueryConstants.USER_TABLE);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(String pwd) {
        password = pwd;
    }
}
