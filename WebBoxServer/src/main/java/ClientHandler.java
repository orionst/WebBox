import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new ObjectInputStream(socket.getInputStream());
            this.out = new ObjectOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        //никуда дальше не идем пока не пройдена аутентификация
                    }
                    while (true) {
                        //основной цикл работы с потоком
                     }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
