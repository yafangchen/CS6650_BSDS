package edu.neu;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
<Resource name="jdbc/mysql" auth="Container" type="javax.sql.DataSource" driverClassName="com.mysql.jdbc.Driver"
url="jdbc:mysql://127.0.0.1:3306/mysqlDB" username="root" password="password" maxTotal="256" maxIdle="10" maxWaitMillis="-1"/>

<Resource name="jdbc/mysql" auth="Container" type="javax.sql.DataSource" driverClassName="com.mysql.jdbc.Driver"
url="jdbc:mysql://google/mysqlDB?cloudSqlInstance=sound-invention-221801:us-west1:dbinstance&amp;socketFactory=com.google.cloud.sql.mysql.SocketFactory&amp;useSSL=false"
username="root" password="password" maxTotal="256" maxIdle="10" maxWaitMillis="-1"/>
        */

public class SharedDao {
    //protected ConnectionPool jdbcObj;
    //protected DataSource dataSource;
    protected DataSource ds;

    private static SharedDao instance = null;

    protected SharedDao() throws Exception {
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            ds = (DataSource)envContext.lookup("jdbc/mysql");
            //jdbcObj = new ConnectionPool();
            //dataSource = jdbcObj.setUpPool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SharedDao getInstance() throws Exception {
        if(instance == null) {
            instance = new SharedDao();
        }
        return instance;
    }

    public String create(int userID, int day, int timeInterval, int stepCount) throws SQLException {
        ResultSet rsObj = null;
        Connection connObj = null;
        PreparedStatement pstmtObj = null;
        String res = "";

        try {
            //System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
            connObj = ds.getConnection();
            //jdbcObj.printDbStatus();

            //String insertRow = "INSERT IGNORE INTO Data(userID, day, timeInterval, stepCount) VALUES(?,?,?,?);";
            String insertRow = "INSERT INTO Data VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE stepCount=?;";
            //String insertRow = "REPLACE INTO Data SET userID=?, day=?, timeInterval=?, stepCount=?;";

            pstmtObj = connObj.prepareStatement(insertRow);
            pstmtObj.setInt(1, userID);
            pstmtObj.setInt(2, day);
            pstmtObj.setInt(3, timeInterval);
            pstmtObj.setInt(4, stepCount);
            pstmtObj.setInt(5, stepCount);
            pstmtObj.executeUpdate();
            //System.out.println("\n=====Releasing Connection Object To Pool=====\n");
            res = "data posted successfully!";
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
            res = "data post failure";
        } finally {
            // Closing ResultSet Object
            if (rsObj != null) {
                rsObj.close();
            }
            // Closing PreparedStatement Object
            if (pstmtObj != null) {
                pstmtObj.close();
            }
            // Closing Connection Object
            if (connObj != null) {
                connObj.close();
            }
            return res;
        }
    }

    public int getMostRecent(int userID) throws SQLException {
        ResultSet rsObj = null;
        Connection connObj = null;
        PreparedStatement pstmtObj = null;
        int res = 0;
        try {
            //System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
            connObj = ds.getConnection();
            //jdbcObj.printDbStatus();

            String selectRows = "SELECT stepCount FROM Data WHERE userID=?;";
            pstmtObj = connObj.prepareStatement(selectRows);
            pstmtObj.setInt(1, userID);
            rsObj = pstmtObj.executeQuery();
            while (rsObj.next()) {
                res  += rsObj.getInt("stepCount");
            }
            //System.out.println("\n=====Releasing Connection Object To Pool=====\n");
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            // Closing ResultSet Object
            if (rsObj != null) {
                rsObj.close();
            }
            // Closing PreparedStatement Object
            if (pstmtObj != null) {
                pstmtObj.close();
            }
            // Closing Connection Object
            if (connObj != null) {
                connObj.close();
            }
            return res;
        }
    }

    public int getCountsByDay (int userID, int day) throws SQLException {
        ResultSet rsObj = null;
        Connection connObj = null;
        PreparedStatement pstmtObj = null;
        int res = 0;
        try {
            //System.out.println("\n=====Making A New Connection Object For Db Transaction=====\n");
            connObj = ds.getConnection();
            //jdbcObj.printDbStatus();

            //String selectRows = "SELECT stepCount FROM Data WHERE userID=? and day=?;";
            String selectRows = "SELECT stepCount FROM Data WHERE userID=?;";
            pstmtObj = connObj.prepareStatement(selectRows);
            pstmtObj.setInt(1, userID);
            //pstmtObj.setInt(2, day);
            rsObj = pstmtObj.executeQuery();
            while (rsObj.next()) {
                res  += rsObj.getInt("stepCount");
            }
            //System.out.println("\n=====Releasing Connection Object To Pool=====\n");
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            // Closing ResultSet Object
            if (rsObj != null) {
                rsObj.close();
            }
            // Closing PreparedStatement Object
            if (pstmtObj != null) {
                pstmtObj.close();
            }
            // Closing Connection Object
            if (connObj != null) {
                connObj.close();
            }
            return res;
        }
    }
}
