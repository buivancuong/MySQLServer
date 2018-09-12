package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// cache database with hashmap

public class JDBCConnection {

    public static Connection getJDBCConnection() {

        final String url = "jdbc:mysql://localhost:3306/exampledb"; // ?autoReconnect=true&useSSL=false
        final String driver = "com.mysql.jdbc.Driver";
        final String user = "root";
        final String password = "cuongbv";

        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
//
    public static void main(String args[]) {
        Connection connection = getJDBCConnection();
        if (connection != null) {
            System.out.println("Goodgame");
        } else {
            System.out.println("Badgame");
        }
    }

}

//    compile 'mysql:mysql-connector-java:5.1.41'
//    compile 'commons-dbcp:commons-dbcp:1.4'
