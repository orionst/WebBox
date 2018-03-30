import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Settings {

    private final String INI_FILE_NAME = "webbox.ini";

    private String userName;
    private int clientId;
    private String userPassword;
    private boolean autoLogon;

    private String serverName;
    private short serverPort;

    public Settings() {
        try {
            File iniFile = new File(INI_FILE_NAME);
            if (iniFile.exists()) {
                Ini ini = new Ini();
                ini.load(new FileReader(iniFile));
                java.util.prefs.Preferences prefs = new IniPreferences(ini);
                serverName = prefs.node("connetion").get("serverName", "localhost");
                serverPort = (short) Integer.parseInt(prefs.node("connetion").get("serverPort", "8189"));
                userName = prefs.node("account").get("user", "");
                userPassword = prefs.node("account").get("pass", "");
                clientId = Integer.parseInt(prefs.node("account").get("clientId", "0"));
                autoLogon = Boolean.parseBoolean(prefs.node("account").get("autologon", "false"));
            } else {
                System.out.println("File not found");
                serverName = "localhost";
                serverPort = 8189;
                userName = "";
                userPassword = null;
                clientId = 0;
                autoLogon = false;
            }
        } catch (Exception e) { }
        System.out.println(serverName);
        System.out.println(serverPort);
        System.out.println(userName);
        System.out.println(userPassword);
    }

    public void saveSettings() throws IOException{
        File iniFile = new File(INI_FILE_NAME);
        if (!iniFile.exists()) {
            iniFile.createNewFile();
        }
        Ini ini = new Ini(iniFile);
        ini.put("connetion", "serverName", serverName);
        ini.put("connetion", "serverPort", serverPort);
        ini.put("account", "user", userName);
        ini.put("account", "pass", userPassword);
        ini.put("account", "clientId", clientId);
        ini.put("account", "autologon", autoLogon);
        ini.store();
    }

    public boolean isAutoLogon() {
        return autoLogon;
    }

    public void setAutoLogon(boolean autoLogon) {
        this.autoLogon = autoLogon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public short getServerPort() {
        return serverPort;
    }

    public void setServerPort(short serverPort) {
        this.serverPort = serverPort;
    }
}
