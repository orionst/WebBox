/*
 * Реализация взаимодействия с БД
 */

import java.sql.*;

public class DBConnector {
    private Connection connection;
    private Statement stmt;
    private PreparedStatement psNewUser;
    private PreparedStatement psFindUser;
    private PreparedStatement psSelectUser;

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:webbox.sl3");
        stmt = connection.createStatement();

        psNewUser = connection.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
        psFindUser = connection.prepareStatement("SELECT id FROM users WHERE login = ? AND password = ?");
        psSelectUser = connection.prepareStatement("SELECT id FROM users WHERE login = ?");

    }

    public Integer getUserIdByLoginAndPass(String login, String pass){
        try {
            psFindUser.setString(1, login);
            psFindUser.setString(2, pass);
            ResultSet rs = psFindUser.executeQuery();
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
