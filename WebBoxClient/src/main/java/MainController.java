import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML TreeView filesTree;
    @FXML Button sendButton;
    @FXML Button deleteButton;
    @FXML Button settingsButton;
    @FXML SettingsWindowController settingsWindowController;

    private Settings settings;

    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;


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
            settingsWindowController.init(settings, outStream, inStream);
            Stage stage = new Stage();
            stage.setTitle("WebBox Settings");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            Scene scene = new Scene(load);
            stage.setScene(scene);
            stage.showAndWait();

            if (settingsWindowController.needReconnect) {
                socket.close();
                connect(settingsWindowController.getSignInPacket());
            }
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
            if (settings.isAutoLogon()){
                Packet.PacketBuilder packetBuilder = new Packet.PacketBuilder();
                connect(packetBuilder.setActionCommand(ActionCommands.AUTH_USER).setLogin(settings.getUserName()).setPassword(settings.getUserPassword()).createPacket());
            }
        }
    }

    public void connect(Packet initialPacket){
        try {
            socket = new Socket(settings.getServerName(), settings.getServerPort());
            inStream = new ObjectInputStream(socket.getInputStream());
            outStream = new ObjectOutputStream(socket.getOutputStream());

            //первым отсылаем пакет авторизации или регистрации
            outStream.writeObject(initialPacket);

            Thread t = new Thread(() -> {
                try {
                    //ждем ответа о результате авторизации
                    while(true){
                        Packet packet = (Packet) inStream.readObject();
                        if (packet.getAction() == ActionCommands.AUTH_USER){
                            //setAuthorized(true);
                            break;
                        }
                    }
                    //сервер авторизировал, ждем пакеты с данными
                    while(true){
                        Packet packet = (Packet) inStream.readObject();
                        System.out.println(packet.toString());
//                        if (s.startsWith("/")){
//                            if (s.startsWith("/clientslist ")){
//                                String[] data = s.split("\\s");
//                                Platform.runLater(() -> {
//                                    filesTree..clear();
//                                    for (int i = 1; i < data.length; i++) {
//                                        clientList.addAll(data[i]);
//                                    }
//                                });
//                            }
//                        } else{
//                            textArea.appendText(s + "\n");
//                        }
                    }
                } catch (IOException e) {
                    showAlert("Сервер перестал отвечать");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    //setAuthorized(false);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            showAlert("Не удалось подключиться к серверу. Проверьте сетевое соединение.");
        }
    }

    public void showAlert(String msg){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Возникли проблемы");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

}
