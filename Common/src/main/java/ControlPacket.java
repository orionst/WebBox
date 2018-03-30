import com.google.gson.Gson;

public class ControlPacket extends Packet {

    public ControlPacket(ActionCommands actCommand, String userName, String password) {
        this.command = actCommand;
        this.userName = userName;
        this.f = password;
    }

    @Override
    public String prepareToSend() {
        return new Gson().toJson(this);
    }
}
