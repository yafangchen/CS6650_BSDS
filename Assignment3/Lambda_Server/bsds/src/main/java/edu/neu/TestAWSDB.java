package edu.neu;

import java.sql.*;
import java.util.Properties;

public class TestAWSDB {
    // User to connect to your database instance. By default, this is "root2".
    private static final String user = "master";
    // Password for the user.
    private static final String password = "password";
    // URI to your database server. If running on the same machine, then this is "localhost".
    private static final String hostName = "mydbinstance3.czcdtzsezsyd.us-west-2.rds.amazonaws.com";
    // Port to your database server. By default, this is 3307.
    private static final int port= 3306;
    // Name of the MySQL schema that contains your tables.
    private static final String schema = "mysqlDB";

    public static void main(String[] argv) throws SQLException {
        Connection connection = null;
        try {
            Properties connectionProperties = new Properties();
            connectionProperties.put("user", user);
            connectionProperties.put("password", password);
            // Ensure the JDBC driver is loaded by retrieving the runtime Class descriptor.
            // Otherwise, Tomcat may have issues loading libraries in the proper order.
            // One alternative is calling this in the HttpServlet init() override.
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException(e);
            }
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostName + ":" + port + "/" + schema,
                    connectionProperties);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

        String selectRow = "SELECT stepCount FROM Data WHERE userID=?;";
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try{
            selectStmt = connection.prepareStatement(selectRow);
            selectStmt.setInt(1, 1);
            results = selectStmt.executeQuery();
            if(results.next()) {
                int stepCount = results.getInt("stepCount");
                System.out.println(stepCount);
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }
}
