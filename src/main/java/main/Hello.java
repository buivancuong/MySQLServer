package main;

import jdbc.ConnectionPoolExample;
import jdbc.JDBCConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Objects;

import static spark.Spark.*;

// non blocking

public class Hello {
    public static void main(String[] args) throws FileNotFoundException {

        FileOutputStream logFile = new FileOutputStream("Log");
        DataOutputStream dataLog = new DataOutputStream(logFile);

        final Logger logger = LoggerFactory.getLogger(Hello.class);

//        threadPool(8, 2, 10000);

        // GET All By CONNECTION POOL
        get("/employees", ((request, response) -> {
            ConnectionPoolExample connectionPoolExample = new ConnectionPoolExample();
            DataSource dataSource = connectionPoolExample.setUp();

            Connection connection = null;
            PreparedStatement preparedStatement = null;

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM employee");
            ResultSet resultSet = preparedStatement.executeQuery();

            Date date = new Date();
            dataLog.writeUTF("\n" + request.ip() + " [" + date + "] " + "GET " + request.url() + "\n");
            logger.info("\n" + request.ip() + " [" + date + "] " + "GET " + request.url() + "\n");

            StringBuilder result = new StringBuilder();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String position = resultSet.getString("position");
                String age = resultSet.getString("age");
                String gender = resultSet.getString("gender");
                String employeeInfo = id + " " + name + " " + position + " " + age + " " + gender;
                employeeInfo = "<br>" + employeeInfo + "</br>";

                result.append(employeeInfo);
            }

            return result;
        }));

        // GET ALL NORMAL
//        get("/employees", ((request, response) -> {
//
//            Statement statement = Objects.requireNonNull(JDBCConnection.getJDBCConnection()).createStatement();
//            String sqlQuery = "SELECT * FROM employee";
//            ResultSet resultSet = statement.executeQuery(sqlQuery);
//
//            Date date = new Date();
//            dataLog.writeUTF("\n" + request.ip() + " [" + date + "] " + "GET " + request.url() + "\n");
//            logger.info("\n" + request.ip() + " [" + date + "] " + "GET " + request.url() + "\n");
//
//            StringBuilder result = new StringBuilder();
//
//            while (resultSet.next()) {
//
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("name");
//                String position = resultSet.getString("position");
//                String age = resultSet.getString("age");
//                String gender = resultSet.getString("gender");
//                String employeeInfo = id + " " + name + " " + position + " " + age + " " + gender;
//                employeeInfo = "<br>" + employeeInfo + "</br>";
//
//                result.append(employeeInfo);
//
//            }
//            return result;
//        }));

        // GET
        get("/employees/:id", ((request, response) -> {

            ConnectionPoolExample connectionPoolExample = new ConnectionPoolExample();
            DataSource dataSource = connectionPoolExample.setUp();

            Connection connection = null;
            PreparedStatement preparedStatement = null;

            connection = dataSource.getConnection();
            String sqlQuery = "SELECT * FROM employee WHERE id = " + request.params(":id");
            preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            Date date = new Date();
            dataLog.writeUTF("\n" + request.ip() + " [" + date + "] " + "GET " + request.url() + "\n");
            logger.info("\n" + request.ip() + " [" + date + "] " + "GET " + request.url() + "\n");

            StringBuilder result = new StringBuilder();

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String position = resultSet.getString("position");
                String age = resultSet.getString("age");
                String gender = resultSet.getString("gender");
                String employeeInfo = id + " " + name + " " + position + " " + age + " " + gender;
                employeeInfo = "<br>" + employeeInfo + "</br>";

                result.append(employeeInfo);

            }
            return result;
        }));

        // POST
        post("/add_employee/name/*/position/*/age/*/gender/*", (request, response) -> {
            Statement statement = Objects.requireNonNull(JDBCConnection.getJDBCConnection()).createStatement();
            String queryAdd = "'" + request.splat()[0] + "'" + ", " + "'" + request.splat()[1] + "'" + ", " + "'" + request.splat()[2] + "'" + ", " + "'" + request.splat()[3] + "'";
            String sqlQuery = "INSERT INTO employee (name, position, age, gender) VALUES (" + queryAdd + ")";

            Date date = new Date();
            dataLog.writeUTF("\n" + request.ip() + " [" + date + "] " + "POST " + request.url() + "\n");
            logger.info("\n" + request.ip() + " [" + date + "] " + "POST " + request.url() + "\n");

            return statement.executeUpdate(sqlQuery);
        });

        // DELETE
        delete("del_employee/:id", ((request, response) -> {
            Statement statement = Objects.requireNonNull(JDBCConnection.getJDBCConnection()).createStatement();
            String sqlQuery = "DELETE FROM employee WHERE id = " + request.params(":id");

            Date date = new Date();
            dataLog.writeUTF("\n" + request.ip() + " [" + date + "] " + "DELETE " + request.url() + "\n");
            logger.info("\n" + request.ip() + " [" + date + "] " + "DELETE " + request.url() + "\n");

            return statement.executeUpdate(sqlQuery);
        }));

    }
}
// http://localhost:4567/hello