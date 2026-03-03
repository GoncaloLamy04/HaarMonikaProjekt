package infrastructure;

import exceptions.DataAccessException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnector {

    private static SQLConnector instance;

    private final String url;
    private final String user;
    private final String password;

    private SQLConnector() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {

            Properties props = new Properties();
            props.load(input);

            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

        } catch (IOException e) {
            throw new DataAccessException("Could not load db.properties", e);
        }
    }

    public static SQLConnector getInstance() {
        if (instance == null) {
            instance = new SQLConnector();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}