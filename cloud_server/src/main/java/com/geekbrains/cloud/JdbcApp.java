package com.geekbrains.cloud;

import java.sql.*;

public class JdbcApp {
        private static Connection connection;
        private static Statement stmt;
        private CloudServerNetty server;

        public JdbcApp(CloudServerNetty server) {
            try {
                connect();
                this.server = server;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void connect() throws SQLException {
            connection = DriverManager.getConnection("jdbc:sqlite:usersdata.db");
            stmt = connection.createStatement();
        }
        public static void disconnect() {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static String retNickName(String login, String password) {
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM users;")){
                while (rs.next()){
                    if (rs.getString("login").equals(login) && rs.getString("password").equals(password)){
                        return rs.getString("nickname");
                    }
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }

        public static void addNewData(String nickname, String login, String password) throws SQLException {
            try {
                stmt.executeUpdate("INSERT INTO users (login,password,nickname) VALUES (" + login + "," + password + "," + nickname + ");");
            } catch (SQLException e){
                e.printStackTrace();
            }
        }

    public void start(){
            System.out.println("База данных пользователей подключена");
        }
}
