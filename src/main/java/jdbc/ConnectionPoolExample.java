package jdbc;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConnectionPoolExample {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/exampledb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "cuongbv";

    private GenericObjectPool connectionPool = null;

    public DataSource setUp() throws Exception {
        // Load JDBC Driver class.
        Class.forName(ConnectionPoolExample.DRIVER);

        // Creates an instance of GenericObjectPool that holds our
        // pool of connections object.
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(8);

        // Creates a connection factory object which will be use by
        // the pool to create the connection object. We passes the
        // JDBC url info, username and password.
        ConnectionFactory cf = new DriverManagerConnectionFactory(
                ConnectionPoolExample.URL,
                ConnectionPoolExample.USERNAME,
                ConnectionPoolExample.PASSWORD);

        // Creates a PoolableConnectionFactory that will wraps the
        // connection object created by the ConnectionFactory to add
        // object pooling functionality.
        new PoolableConnectionFactory(cf, connectionPool,
                null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    private GenericObjectPool getConnectionPool() {
        return connectionPool;
    }

    public static void main(String[] args) throws Exception {

        long time1 = System.currentTimeMillis();

        ConnectionPoolExample demo = new ConnectionPoolExample();

        long time2 = System.currentTimeMillis();

        DataSource dataSource = demo.setUp();

        long time3 = System.currentTimeMillis();

        demo.printStatus();

        long time4 = System.currentTimeMillis();

        Connection conn = null;
        PreparedStatement stmt = null;

        long time5 = System.currentTimeMillis();

        long time6, time7, time8, time9, time10;

        try {

            time6 = System.currentTimeMillis();

            conn = dataSource.getConnection();

            time7 = System.currentTimeMillis();

            demo.printStatus();

            stmt = conn.prepareStatement("SELECT * FROM employee");

            time8 = System.currentTimeMillis();

            ResultSet rs = stmt.executeQuery();

            time9 = System.currentTimeMillis();

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String position = rs.getString("position");
                String age = rs.getString("age");
                String gender = rs.getString("gender");
                String employeeInfo = id + " " + name + " " + position + " " + age + " " + gender;
//                employeeInfo = "<br>" + employeeInfo + "</br>";

                System.out.println(employeeInfo);
            }

            time10 = System.currentTimeMillis();

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        long time11 = System.currentTimeMillis();

        demo.printStatus();

        System.out.println((time2 - time1) + " " + (time3 - time2) + " " + (time4 - time3) + " " + (time5 - time4) + " " + (time6 - time5) + " " + (time7 - time6) + " " + (time8 - time7) + " " + (time9 - time8) + (time10 - time9) + " " + (time11 - time10));
    }

    /**
     * Prints connection pool status.
     */
    private void printStatus() {
        System.out.println("Max   : " + getConnectionPool().getMaxActive() + "; " +
                "Active: " + getConnectionPool().getNumActive() + "; " +
                "Idle  : " + getConnectionPool().getNumIdle());
    }
}
