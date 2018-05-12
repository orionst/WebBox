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
        try {
            fileCommander = new FileCommander(ROOT_DIR_PATH);
            packetCntrlr = new PacketsController();
            ServerSocket serverSocket = new ServerSocket(8189);
            dbService = new DBConnector();
            dbService.connect();
            System.out.println("Сервер запущен... Ожидание подключения клиентов...");
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Подключился клиент " + socket.getInetAddress() + " " + socket.getPort() + " " + socket.getLocalPort());
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
