import java.io.File;
import java.util.List;

public class PacketsController {

    //Создает пакет с телом пересылаемого файла
    public Packet createFilePacket(File file) {
        Packet packet = new Packet.PacketBuilder().
                setActionCommand(ActionCommands.SEND_FILE).
                addFile(file).
                createPacket();
        return packet;
    }

    //Создает пакет со списком файлов пользователя
    public Packet createFileListPacket(List fileList) {
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder().setActionCommand(ActionCommands.GET_LIST);
        fileList.forEach(file -> packetBuilder.addFilePath((String) file));
        Packet packet = packetBuilder.createPacket();
        return packet;
    }

    //Создает пакет запроса файла с уазанным именем файла
    public Packet createGetFilePacket(String file_name) {
        Packet packet = new Packet.PacketBuilder().
                setActionCommand(ActionCommands.GET_FILE).
                addFilePath(file_name).
                createPacket();
        return packet;
    }

    //Создает пакет ответа на попытку авторизации/регистрации
    public Packet createAuthResultPacket(boolean result) {
        Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder();
        return packetBuilder.setActionCommand(ActionCommands.ANSW).setResult(result).createPacket();
    }


}
