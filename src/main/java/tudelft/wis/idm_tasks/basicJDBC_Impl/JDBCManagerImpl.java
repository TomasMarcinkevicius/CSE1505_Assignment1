package tudelft.wis.idm_tasks.basicJDBC_Impl;

import tudelft.wis.idm_tasks.basicJDBC.interfaces.JDBCManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCManagerImpl implements JDBCManager {
    private Connection connection;
    private final String connectionString = "jdbc:postgresql://localhost:5432/imdb";
    private final String username = "postgres";
    private final String password = "postgres";

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", username);
            connectionProperties.put("password", password);
            connection = DriverManager.getConnection(connectionString, connectionProperties);
        }

        return connection;
    }
}