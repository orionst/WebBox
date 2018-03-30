public class PacketFactory {

    public static Packet createPacket(ActionCommands actCommand, String userName, short clientId, Object data) {
        if (actCommand == ActionCommands.NEW_USER) {
            return new ControlPacket(actCommand, userName, data.toString());
        }
        if(actCommand == ActionCommands.AUTH_USER) {

        }
        return null;
    }

}
