import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ClientHandler {
    private Server server;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private Integer userId;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.inStream = new ObjectInputStream(socket.getInputStream());
            this.outStream = new ObjectOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        //никуда дальше не идем пока не пройдена аутентификация
                        Packet packet = (Packet) inStream.readObject();
                        if (packet.getAction() == ActionCommands.AUTH_USER) {
                            userId = this.server.dbService.getUserIdByLoginAndPass(packet.getUserName(), packet.getPassword());
                            if (userId != null) {
                                //авторизуем
                                sendPacket(createAnswerPacket(true));
                                sendPacket(createFileListPacket(this.server.fileCommander.getUsersFileList(userId)));
                                break;
                            } else {
                                sendPacket(createAnswerPacket(false));
                                System.out.println("Ошибка авторизации пользователя. Запись о таком пользователе отсутствует на сервере.");
                            }

                        } else if (packet.getAction() == ActionCommands.NEW_USER) {
                            userId = this.server.dbService.getUserIdByLogin(packet.getUserName());
                            if (userId != null) {
                                sendPacket(createAnswerPacket(false));
                                System.out.println("Ошибка регистрации нового пользователя. Такой пользователь уже зарегистрирован на сервере.");
                            } else {
                                //регистрируем и сразу авторизуем
                                userId = this.server.dbService.regNewUser(packet.getUserName(), packet.getPassword());
                                this.server.fileCommander.createUserDir(userId);
                                sendPacket(createAnswerPacket(false));
                                break;
                            }

                        }
                    }
                    while (true) {
                        //основной цикл работы с потоком
                        Packet packet = (Packet) inStream.readObject();
                        parseIncomingPacket(packet);
                     }
                } catch (IOException | ClassNotFoundException e) {
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

    private void sendPacket(Packet packet) {
        try {
            outStream.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseIncomingPacket(Packet packet) throws IOException {
        System.out.println("Получено: " + packet.toString());
        Map<String, File> mapFiles = packet.getData();
        switch (packet.getAction()) {
            case SEND_FILE:
                for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                    this.server.fileCommander.saveFileToUsersDir(userId, entry.getValue());
                }
                sendPacket(createFileListPacket(this.server.fileCommander.getUsersFileList(userId)));
                break;
            case GET_FILE:
                for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                    sendPacket(createFilePacket(entry.getKey()));
                }
                break;
            case DELETE_FILE:
                for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                    this.server.fileCommander.deleteUsersFile(userId, entry.getKey());
                }
                sendPacket(createFileListPacket(this.server.fileCommander.getUsersFileList(userId)));
                break;
            case GET_LIST:
                sendPacket(createFileListPacket(this.server.fileCommander.getUsersFileList(userId)));
                break;
            default:
                System.out.println("Входящий неизвестный пакет: " + packet.toString());
                break;
        }
    }

    private Packet createAnswerPacket(boolean result) {
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder();
        return packetBuilder.setActionCommand(ActionCommands.ANSW).setResult(result).createPacket();
    }

    private Packet createFileListPacket(List fileList) {
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder().setActionCommand(ActionCommands.GET_LIST);
        fileList.forEach(file -> packetBuilder.addFilePath((String) file));
        Packet packet = packetBuilder.createPacket();
        packet.toString();
        return packet;
    }

    private Packet createFilePacket(String file_name) throws IOException {
        Packet packet = new Packet.PacketBuilder().
                            setActionCommand(ActionCommands.SEND_FILE).
                            addFile(this.server.fileCommander.getUserFile(userId, file_name)).
                            createPacket();
        return packet;
    }
}

