import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    boolean isAuthorized;

    private Integer userId;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.inStream = new ObjectInputStream(socket.getInputStream());
            this.outStream = new ObjectOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        parseIncomingPacket((Packet) inStream.readObject());
                    }
                } catch (SocketException e) {
                    System.out.println("Клиент разорвал связь");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        System.out.println("Закрыли отключившийся сокет");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseIncomingPacket(Packet packet) throws IOException {
        System.out.println("Получено: " + packet.toString());
        Map<String, File> mapFiles = packet.getData();
        switch (packet.getAction()) {
            case NEW_USER:
                if (isAuthorized) break;
                userId = server.dbService.getUserIdByLogin(packet.getUserName());
                if (userId != null) {
                    this.server.packetCntrlr.createAuthResultPacket(ActionCommands.NEW_USER, false).sendPacket(outStream);
                    System.out.println("Ошибка регистрации нового пользователя. Такой пользователь уже зарегистрирован на сервере.");
                } else {
                    //регистрируем и сразу авторизуем
                    userId = server.dbService.regNewUser(packet.getUserName(), packet.getPassword());
                    server.fileCommander.createUserDir(userId);
                    server.packetCntrlr.createAuthResultPacket(ActionCommands.NEW_USER, false).sendPacket(outStream);
                    isAuthorized = true;
                }
                break;
            case AUTH_USER:
                if (isAuthorized) break;
                userId = server.dbService.getUserIdByLoginAndPass(packet.getUserName(), packet.getPassword());
                if (userId != null) {
                    //авторизуем
                    server.packetCntrlr.createAuthResultPacket(ActionCommands.AUTH_USER,true).sendPacket(outStream);
                    server.packetCntrlr.createFileListPacket(server.fileCommander.getUsersFileList(userId)).sendPacket(outStream);
                    isAuthorized = true;
                } else {
                    this.server.packetCntrlr.createAuthResultPacket(ActionCommands.AUTH_USER,false).sendPacket(outStream);
                    System.out.println("Ошибка авторизации пользователя. Запись о таком пользователе отсутствует на сервере.");
                }
                break;
            case AUTH_OFF_USER:
                if (!isAuthorized) break;
                server.packetCntrlr.createAuthResultPacket(ActionCommands.AUTH_OFF_USER,true).sendPacket(outStream);
                isAuthorized = false;
                System.out.println("Клиент отключился");
            case SEND_FILE:
                if (!isAuthorized) break;
                for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                    server.fileCommander.saveFileToUsersDir(userId, entry.getValue());
                }
                server.packetCntrlr.createFileListPacket(server.fileCommander.getUsersFileList(userId)).sendPacket(outStream);
                break;
            case GET_FILE:
                if (!isAuthorized) break;
                for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                    server.packetCntrlr.createFilePacket(server.fileCommander.getUserFile(userId, entry.getKey())).sendPacket(outStream);
                }
                break;
            case DELETE_FILE:
                if (!isAuthorized) break;
                for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                    server.fileCommander.deleteUsersFile(userId, entry.getKey());
                }
                server.packetCntrlr.createFileListPacket(server.fileCommander.getUsersFileList(userId)).sendPacket(outStream);
                break;
            case GET_LIST:
                if (!isAuthorized) break;
                server.packetCntrlr.createFileListPacket(server.fileCommander.getUsersFileList(userId)).sendPacket(outStream);
                break;
            default:
                System.out.println("Входящий неизвестный пакет: " + packet.toString());
                break;
        }
    }

}

