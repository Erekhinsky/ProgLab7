package common;

public class QueryConstants {
    public final static String USER_INSERTION = "INSERT INTO users VALUES (?,?)";

    public final static String ELEMENT_INSERTION = "INSERT INTO vehicle (name, x, y, creationdate, enginepower, numberofwheels, distancetravelled, fueltype, " +
            "username) VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ?);";

    public final static String ELEMENT_UPDATE = "UPDATE vehicle SET name = ? , x = ? , y = ? , enginepower = ? , numberofwheels = ? , distancetravelled = ? , fueltype = ? , " +
            " WHERE id = ? AND username = ?;";

    public final static String ELEMENT_SELECT = "SELECT ? FROM vehicle WHERE id = ?;";

    public final static String ELEMENT_DELETE = "DELETE FROM vehicle WHERE id = ? AND username = ?;";

    public final static String ID_GENERATING_SEQUENCE = "CREATE SEQUENCE IF NOT EXISTS vehicleid START 1;";

    public final static String TABLE = "CREATE TABLE IF NOT EXISTS vehicle (id BIGINT NOT NULL UNIQUE DEFAULT nextval('vehicleid'), name VARCHAR (50) NOT NULL , " +
            "x DOUBLE PRECISION NOT NULL, y DOUBLE PRECISION NOT NULL, creationdate DATE NOT NULL, enginepower BIGINT NOT NULL CHECK (enginepower > 0), " +
            "numberofwheels BIGINT CHECK (numberofwheels > 0) , distancetravelled DOUBLE PRECISION CHECK (distancetravelled > 0), fueltype VARCHAR(50), username VARCHAR(50));";

    public final static String USER_TABLE = "CREATE TABLE IF NOT EXISTS users (username VARCHAR(50) UNIQUE, password VARCHAR(500));";
}

//1 name
//2 X double
//3 Y double
//4 Creation Date Local Date
//5 engine Power long
//6 Number of wheels long
//7 distance of travelled float
//8 fuel type
//9 User name