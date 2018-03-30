import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsWindowController implements Initializable {

    @FXML CheckBox autoLogon;
    @FXML PasswordField userPass;
    @FXML TextField userName;
    @FXML TextField serverPort;
    @FXML TextField serverName;
    @FXML Button cancelButton;
    @FXML Button okButton;

    private Settings settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void init(Settings settings) {
        this.settings = settings;
        serverName.setText(settings.getServerName());
        serverPort.setText(Short.toString(settings.getServerPort()));
        userName.setText(settings.getUserName());
        serverPort.setText(Short.toString(settings.getServerPort()));
        autoLogon.setSelected(settings.isAutoLogon());
    }

    @FXML
    private void cancelAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void okAction() {
        //записать изменения
        settings.setServerName(serverName.getText());
        settings.setServerPort(Short.parseShort(serverPort.getText()));
        settings.setUserName(userName.getText());
        if (autoLogon.isSelected()) {
            settings.setUserPassword(userPass.getText());
        } else {
            settings.setUserPassword("");
        }
        settings.setAutoLogon(autoLogon.isSelected());
        try {
            settings.saveSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void signUpAction() {
        //регистрация
    }

    @FXML
    private void signInAction() {
        //авторизация
    }

}
