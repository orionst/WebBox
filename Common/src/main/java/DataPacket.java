import com.google.gson.Gson;

public class DataPacket extends Packet {

    public DataPacket() {
    }

    @Override
    public String prepareToSend() {
        return new Gson().toJson(this);
    }
}
