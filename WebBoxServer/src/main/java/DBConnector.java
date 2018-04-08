/*
 * Реализация взаимодействия с БД
 */

import java.sql.*;

public class DBConnector {
    private Connection connection;
    private Statement stmt;
    private PreparedStatement psNewUser;
    private PreparedStatement psSelectUser;

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:webbox.db");
        stmt = connection.createStatement();

        psNewUser = connection.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
        psSelectUser = connection.prepareStatement("SELECT id FROM users  WHERE login = ? AND password = ?");

    }

    public Integer getUserIdByLoginAndPass(String login, String pass){
        try {
            psSelectUser.setString(1, login);
            psSelectUser.setString(2, pass);
            ResultSet rs = psSelectUser.executeQuery();
            if (rs.next()){
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getUserIdByLogin(String login){
        try {
            psSelectUser.setString(1, login);
            ResultSet rs = psSelectUser.executeQuery();
            if (rs.next()){
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer regNewUser(String login, String pass) {
        try {
            psNewUser.setString(1, login);
            psNewUser.setString(2, pass);
            int res = psNewUser.executeUpdate();
            System.out.println(res);

            return getUserIdByLogin(login);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
