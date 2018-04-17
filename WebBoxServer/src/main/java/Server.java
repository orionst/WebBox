import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    final String ROOT_DIR_PATH = "/users";
    DBConnector dbService;
    FileCommander fileCommander;
    PacketsController packetCntrlr;

    public Server() {
        fileCommander = new FileCommander(ROOT_DIR_PATH);
        packetCntrlr = new PacketsController();
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            dbService = new DBConnector();
            dbService.connect();
            System.out.println("Server started... Waiting clients...");
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Client connected " + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort());
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Не удалось запустить сервис авторизации");
        } finally {
            dbService.disconnect();
        }

    }
}
