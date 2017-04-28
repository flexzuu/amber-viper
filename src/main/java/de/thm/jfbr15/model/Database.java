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
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(connectionURL, user, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public Result sendQuery(String query) throws SQLException {
        Statement statement = conn.createStatement();
        boolean wasQuery = statement.execute(query);
        if (wasQuery) {
            return new Result(statement.getResultSet());
        } else {
            return new Result(statement.getUpdateCount());
        }

    }
    public void close() throws SQLException {
        conn.close();
    }
}
