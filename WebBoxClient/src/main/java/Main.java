import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/webboxclient.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
        stage.setTitle("WebBox Sync Client");
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
