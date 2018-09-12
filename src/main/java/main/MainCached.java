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
import java.util.HashMap;
import java.util.Objects;

import static spark.Spark.*;

public class MainCached {

    public static void main(String args[]) throws FileNotFoundException {
        port(8080);
        FileOutputStream logFile = new FileOutputStream("Log");
        DataOutputStream dataLog = new DataOutputStream(logFile);

        final Logger logger = LoggerFactory.getLogger(Hello.class);

        HashMap<String, String> cache =new HashMap<>();
        get("/employees/:id", ((request, response) -> {
            String userId = request.params(":id");
//            System.out.println("UserId: " + userId);
            if (cache.containsKey(userId)){
//                System.out.println("Cache hit");
                return cache.get(userId);
            }
            else {
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
                cache.put(userId, result.toString());
                connection.close();
                return result.toString();
            }
        }));

        // POST [Accept, Connection, Content-Length, Content-Type, Host, User-Agent]
        post("/employee", (request, response) -> {

            System.out.println("test " + request.body());

            Statement statement = Objects.requireNonNull(JDBCConnection.getJDBCConnection()).createStatement();
            String sqlQuery = "INSERT INTO employee (name) VALUES (" + request.params(":name") + ")";
            statement.executeUpdate(sqlQuery);

            Date date = new Date();
            dataLog.writeUTF("\n" + request.ip() + " [" + date + "] " + "POST " + request.url() + "\n");
            logger.info("\n" + request.ip() + " [" + date + "] " + "POST " + request.url() + "\n");

            System.out.println("body " + response.body());

            return response + "goodgame";
        });

        // PUT
        put("/employee/:name", ((request, response) -> {



            return response.status();
        }));

        // DELETE
        delete("employee/:id", ((request, response) -> {
            Statement statement = Objects.requireNonNull(JDBCConnection.getJDBCConnection()).createStatement();
            String sqlQuery = "DELETE FROM employee WHERE id = " + request.params(":id");
            statement.executeUpdate(sqlQuery);

            Date date = new Date();
            dataLog.writeUTF("\n" + request.ip() + " [" + date + "] " + "DELETE " + request.url() + "\n");
            logger.info("\n" + request.ip() + " [" + date + "] " + "DELETE " + request.url() + "\n");

            return response.status();
        }));

    }

}
