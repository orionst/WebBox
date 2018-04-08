import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Packet implements Serializable {

    private ActionCommands action;
    private String userName;
    private String password;
    private Map<String, File> data;

    private Packet(ActionCommands action, String userName, String password, File file) {
        this.action = action;
        this.userName = userName;
        this.password = password;
        this.data = data;
    }

    public ActionCommands getAction() {
        return action;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, File> getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Packet{" +
                "action=" + action +
                ", userName='" + userName + '\'' +
                ", password='" + password + " {" );
        data.forEach((key, value) -> {
            output.append(key+", ");
        });
        output.append("}}");
        return output.toString();
    }

    public static class PacketBuilder {
        private ActionCommands action;
        private String userName;
        private String password;
        private File file;
        private Map<String, File> data;

        public PacketBuilder() {
            this.file = null;
            this.data = new HashMap<String, File>();
        }
        public PacketBuilder setActionCommand(ActionCommands action) {
            this.action = action;
            return this;
        }
        public PacketBuilder setFile(File file) {
            this.file = file;
            return this;
        }
        public PacketBuilder setLogin(String userName) {
            this.userName = userName;
            return this;
        }
        public PacketBuilder setPassword(String password) {
            this.password = password;
            return this;
        }
        public PacketBuilder addFile(File file) {
            this.data.put(file.getName(), file);
            return this;
        }
        public PacketBuilder addFilePath(String filePath) {
            this.data.put(filePath, null);
            return this;
        }
        public Packet createPacket(){
            return new Packet(action, userName, password, file);
        }
    }

}
