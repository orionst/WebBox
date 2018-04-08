import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

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
                        //никуда дальше не идем пока не пройдена аутентификация
                        Packet packet = (Packet) inStream.readObject();
                        if (packet.getAction() == ActionCommands.AUTH_USER) {
                            userId = this.server.dbService.getUserIdByLoginAndPass(packet.getUserName(), packet.getPassword());
                            if (userId != null) {
                                //авторизуем
                                //ОТПРАВЛЯЕМ ПОЛЬЗОВАТЕЛЮ СПИСОК ЕГО ФАЙЛОВ
                                break;
                            } else {
                                System.out.println("Ошибка авторизации пользователя. Запись о таком пользователе отсутствует на сервере.");
                            }

                        } else if (packet.getAction() == ActionCommands.NEW_USER) {
                            userId = this.server.dbService.getUserIdByLogin(packet.getUserName());
                            if (userId != null) {
                                System.out.println("Ошибка регистрации нового пользователя. Такой пользователь уже зарегистрирован на сервере.");
                            } else {
                                //регистрируем и сразу авторизуем
                                userId = this.server.dbService.regNewUser(packet.getUserName(), packet.getPassword());
                                //ОТПРАВЛЯЕМ ПОЛЬЗОВАТЕЛЮ СПИСОК ЕГО ФАЙЛОВ, ПОНЯТНО ЧТО ОН ПУСТОЙ

                                break;
                            }

                        }
                    }
                    while (true) {
                        //основной цикл работы с потоком
                        Packet packet = (Packet) inStream.readObject();
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

    public void sendPacket(Packet packet) {
        try {
            outStream.writeObject(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Packet createPacket(ActionCommands action) {
        if (action == ActionCommands.AUTH_USER) {

        }
    }
}

