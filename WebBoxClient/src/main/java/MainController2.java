import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController2 implements Initializable {

    @FXML TextField serverNameField;
    @FXML TextField serverPortField;
    @FXML Button refreshFileListButton;
    @FXML Button sendButton;
    @FXML Button downloadFile;
    @FXML Button deleteButton;

    @FXML TextField loginField;
    @FXML PasswordField passField;
    @FXML CheckBox autoLogon;
    @FXML Button buttonRegister;
    @FXML Button buttonSignIn;
    @FXML Button saveSettingsButton;

    @FXML ListView<String> filesView;

    private Settings settings;

    private Socket socket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    boolean isAuthorized;

    private PacketsController packetCntrlr;

    private ObservableList<String> fileList;

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
        loginField.setDisable(authorized);
        passField.setDisable(authorized);
        buttonRegister.setDisable(authorized);
        buttonSignIn.setDisable(authorized);
        refreshFileListButton.setDisable(!authorized);
        sendButton.setDisable(!authorized);
        downloadFile.setDisable(!authorized);
        deleteButton.setDisable(!authorized);
        serverNameField.setDisable(authorized);
        serverPortField.setDisable(authorized);
        saveSettingsButton.setDisable(authorized);
        autoLogon.setDisable(authorized);

        if (authorized) {
            buttonSignIn.setText("Отключиться");
            buttonSignIn.setDisable(false);
        } else {
            buttonSignIn.setText("Авторизация");
        }
    }

    private void fillGUIfields(Settings settings) {
        serverNameField.setText(settings.getServerName());
        serverPortField.setText(Short.toString(settings.getServerPort()));
        loginField.setText(settings.getUserName());
        passField.setText(settings.getUserPassword());
    }

    @FXML
    private void refreshFileListButtonAction(ActionEvent actionEvent) {
        Packet packet = new Packet.PacketBuilder().
                            setActionCommand(ActionCommands.GET_LIST).
                            createPacket();
        packet.sendPacket(outStream);
    }

    @FXML
    private void sendFileButtonAction(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для отправки на сервер");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            packetCntrlr.createFilePacket(selectedFile).sendPacket(outStream);
        }
    }

    @FXML
    public void downloadFileAction(ActionEvent actionEvent) {
        if (filesView.getFocusModel().getFocusedItem() == null) {
            showAlert("В списке файлов необходимо выбрать скачиваемый файл.");
            return;
        }
        packetCntrlr.createGetFilePacket(filesView.getFocusModel().getFocusedItem()).sendPacket(outStream);
    }

    @FXML
    private void deleteFileButtonAction(ActionEvent actionEvent){
        Packet packet = new Packet.PacketBuilder().
                            setActionCommand(ActionCommands.DELETE_FILE).
                            addFilePath(filesView.getFocusModel().getFocusedItem()).
                            createPacket();
        packet.sendPacket(outStream);
    }

    @FXML
    private void signUpAction(ActionEvent actionEvent) {
        Packet packet = new Packet.PacketBuilder().setActionCommand(ActionCommands.NEW_USER).setLogin(loginField.getText()).setPassword(passField.getText()).createPacket();
        packet.sendPacket(outStream);
    }

    @FXML
    private void signInAction(ActionEvent actionEvent) {
        Packet packet = null;
        if (!isAuthorized)
            packet = new Packet.PacketBuilder().setActionCommand(ActionCommands.AUTH_USER).setLogin(loginField.getText()).setPassword(passField.getText()).createPacket();
        else
            packet = new Packet.PacketBuilder().setActionCommand(ActionCommands.AUTH_OFF_USER).setLogin(loginField.getText()).createPacket();
        packet.sendPacket(outStream);
    }

    @FXML
    public void saveSettingsAction(ActionEvent actionEvent) {
        //записать изменения
        settings.setServerName(serverNameField.getText());
        settings.setServerPort(Short.parseShort(serverPortField.getText()));
        settings.setUserName(loginField.getText());
        if (autoLogon.isSelected()) {
            settings.setUserPassword(passField.getText());
        } else {
            settings.setUserPassword("");
        }
        settings.setAutoLogon(autoLogon.isSelected());
        try {
            settings.saveSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Settings getSettings() {
        return new Settings();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settings = getSettings();
        fillGUIfields(settings);
        setAuthorized(false);
        fileList = FXCollections.observableArrayList();
        filesView.setItems(fileList);
        filesView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty){
                            setText(item);
                        }else{
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
            }
        });

        socket = new Socket();
        connect();
    }

    private void showAlert(String msg){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Возникли проблемы");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    private void saveFileFromServer(Map<String, File> mapFiles) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                try {
                    DirectoryChooser dirChooser = new DirectoryChooser();
                    dirChooser.setTitle("Выберите каталог для сохранения файла");
                    File selectedDir = dirChooser.showDialog(null);
                    if (selectedDir != null) {
                        for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
                            Path path_to_file = Paths.get(selectedDir.getPath() + File.separator + entry.getValue().getName());
                            Files.copy(entry.getValue().toPath(), path_to_file, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void connect(){
        packetCntrlr = new PacketsController();
        try {
            socket = new Socket(settings.getServerName(), settings.getServerPort());
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());

            Thread t = new Thread(() -> {
                try {
                    //ждем ответа о результате авторизации
                    while(true){
                        Packet packet = (Packet) inStream.readObject();
                        if (packet.getAction() == ActionCommands.ANSW){
                            if (packet.isOk()) {
                                System.out.println("Успешно залогинились");
                                setAuthorized(true);
                                break;
                            }
                            else
                                showAlert("Ошибка регистрации/входа в учетную запись.");
                        }
                    }
                    //сервер авторизировал, ждем пакеты с данными
                    while(true){
                        Packet packet = (Packet) inStream.readObject();
                        parsePacket(packet);
                    }
                } catch (IOException e) {
                    showAlert("Сервер перестал отвечать");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
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

    private void parsePacket(Packet packet) {
        Map<String, File> mapFiles = packet.getData();
        switch (packet.getAction()) {
            case ANSW:
                if (packet.isOk()) {
                    setAuthorized(false);
                } else {
                    showAlert("Ошибка отключения от сервера.");
                }
                break;
            case SEND_FILE:
                saveFileFromServer(mapFiles);
                break;
            case GET_LIST:
                Map<String, File> finalMapFiles = mapFiles;
                Platform.runLater(() -> {
                    fileList.clear();
                    for (Map.Entry<String, File> entry : finalMapFiles.entrySet()) {
                        fileList.addAll(entry.getKey());
                    }
                });
                break;
            default:
                System.out.println("Входящий неизвестный пакет: "+packet.toString());
                break;
        }

    }

}
