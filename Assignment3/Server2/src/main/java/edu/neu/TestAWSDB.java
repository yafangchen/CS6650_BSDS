package edu.neu;

import java.sql.*;
import java.util.Properties;

public class TestAWSDB {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String databaseName = "mysqlDB";
    private static final String instanceConnectionName = "sound-invention-221801:us-west1:dbinstance";
    private static final String JDBC_DB_URL = String.format(
            "jdbc:mysql://google/%s?cloudSqlInstance=%s"
                    + "&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false",
            databaseName,
            instanceConnectionName);
    // JDBC Database Credentials
    // username="master" for AWS RDS
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "password";


    public static void main(String[] argv) throws SQLException {
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
