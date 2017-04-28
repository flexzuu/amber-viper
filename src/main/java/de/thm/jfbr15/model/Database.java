package de.thm.jfbr15.model;

import java.sql.*;

/**
 * Created by Jonas on 27.04.2017.
 */
public class Database {
    private Connection conn;

    public Database(String connectionURL, String user, String password) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(connectionURL, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public ResultSet sendQuery(String query) throws SQLException {
        return conn.createStatement().executeQuery(query);
    }
    public void close() throws SQLException {
        conn.close();
    }
}
