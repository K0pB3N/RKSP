package org.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnector {

    private final Connection connection;

    public DatabaseConnector() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:postgresql://localhost/practice", "postgres", "Huser999111");
        init();
    }

    private void init() throws SQLException {
        connection.createStatement().execute("create table if not exists my_data (id serial primary key, data varchar(255));");
    }

    public void addData(String data) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("insert into my_data (data) values (?)");
        ps.setString(1, data);
        ps.execute();
    }

    public void deleteData(String data) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("delete from my_data where data = ?");
        ps.setString(1, data);
        ps.execute();
    }

    public boolean isDataExist(String data) {
        try {
            PreparedStatement ps = connection.prepareStatement("select * from my_data where data = ?;");
            ps.setString(1, data);
            return ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
