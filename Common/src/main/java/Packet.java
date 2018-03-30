import java.io.File;
import java.io.Serializable;

public abstract class Packet implements Serializable {

    public ActionCommands command;
    public String control;
    public File file; // в зависимости от типа команды может быть либо файлом, либо иной информацией

    public abstract String prepareToSend();

}
