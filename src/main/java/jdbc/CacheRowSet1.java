package jdbc;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.SQLException;

public class CacheRowSet1 {
    private static CacheRowSet1 ourInstance;

    private static StringBuilder result = new StringBuilder();

    public String getResult() {
        return result.toString();
    }

    public static synchronized CacheRowSet1 getInstance() {
        if (ourInstance == null) {
            ourInstance = new CacheRowSet1();
        }
        return ourInstance;
    }

    private CacheRowSet1() {

        try {
            CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
            cachedRowSet.setUrl("jdbc:mysql://localhost:3306/exampledb");
            cachedRowSet.setUsername("root");
            cachedRowSet.setPassword("cuongbv");
            cachedRowSet.setCommand("SELECT * FROM employee WHERE id = 1");
            cachedRowSet.execute();
            while (cachedRowSet.next()) {
                int id = cachedRowSet.getInt("id");
                String name = cachedRowSet.getString("name");
                String position = cachedRowSet.getString("position");
                String age = cachedRowSet.getString("age");
                String gender = cachedRowSet.getString("gender");
                String employeeInfo = id + " " + name + " " + position + " " + age + " " + gender;
                employeeInfo = "<br>" + employeeInfo + "</br>";

                result.append(employeeInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
