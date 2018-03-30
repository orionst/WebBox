import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML Button sendButton;
    @FXML Button deleteButton;
    @FXML Button settingsButton;
    @FXML SettingsWindowController settingsWindowController;

    private Settings settings;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;


    public void sendFileButtonAction(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            //sendFileToServer(selectedFile.getAbsoluteFile());
        }
    }

    public void deleteFileButtonAction(ActionEvent actionEvent){

    }

    public void openSettingsWindowButtonAction(ActionEvent actionEvent){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings_window.fxml"));
            Parent load = (Parent) loader.load();
            SettingsWindowController settingsWindowController = loader.getController();
            settingsWindowController.init(settings);
            Stage stage = new Stage();
            stage.setTitle("WebBox Settings");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Settings getSettings() {
        return new Settings();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settings = getSettings();
        if (socket == null || socket.isClosed()){
            connect();
        }
        if (settings.isAutoLogon()){
            try {
                out.writeUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
