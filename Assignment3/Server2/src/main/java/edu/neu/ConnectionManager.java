package edu.neu.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    /*
    // User to connect to your database instance. By default, this is "root2".
    private final String user = "master";
    // Password for the user.
    private final String password = "password";
    // URI to your database server. If running on the same machine, then this is "localhost".
    private final String hostName = "mydbinstance3.czcdtzsezsyd.us-west-2.rds.amazonaws.com";
    // Port to your database server. By default, this is 3307.
    private final int port= 3306;
    // Name of the MySQL schema that contains your tables.
    private final String schema = "mysqlDB"; */

    private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final String databaseName = "mysqlDB";
    private final String instanceConnectionName = "sound-invention-221801:us-west1:dbinstance";
    private final String JDBC_DB_URL = String.format(
            "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                    + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
            databaseName,
            instanceConnectionName);
    // JDBC Database Credentials
    // username="master" for AWS RDS
    private final String JDBC_USER = "root";
    private final String JDBC_PASS = "password";


    /** Get the connection to the database instance. */
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return connection;
    }

    /** Close the connection to the database instance. */
    public void closeConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}