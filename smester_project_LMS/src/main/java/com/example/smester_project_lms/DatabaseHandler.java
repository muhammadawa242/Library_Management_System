//  https://www.jetbrains.com/help/idea/working-with-module-dependencies.html#remove-dependency
package com.example.smester_project_lms;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler{
    public static final String ADMIN = "admin";
    public static final String ADM_PASS = "123";

    public static ArrayList<String[]> execute(String statement, String tableName) {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:C:\\Users\\Barket trader\\smester_project_LMS\\src\\main\\java\\com\\example\\smester_project_lms\\lms.db";
            Connection c = DriverManager.getConnection(url);
            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery(statement);
            ArrayList<String[]> data = new ArrayList<>();

            while (rs.next()) {
                switch (tableName) {
                    case "users" -> data.add(new String[]{rs.getString("id"), rs.getString("name"), rs.getString("password")});
                    case "users_pending" -> data.add(new String[]{rs.getString("name"), rs.getString("password")});
                    case "books" -> data.add(new String[]{String.valueOf(rs.getInt("id")), rs.getString("image"), rs.getString("name"), rs.getString("author"), rs.getString("issuance"), rs.getString("dateField")});
                    case "book_issuance_pending", "books_issued" -> data.add(new String[]{String.valueOf(rs.getInt("user_id")), rs.getString("book_id")});
                }
            }

            stmt.close();
            c.close();

            return data;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}