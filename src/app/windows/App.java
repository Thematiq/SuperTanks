package app.windows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Javafx main class
 * @author Mateusz Praski
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/gameOptions.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Super hot tanks");
        stage.getIcons().add(new Image(getClass().getResource("resources/icon.png").toURI().toString()));
        stage.setScene(scene);
        stage.show();
    }

    public static void run(String[] args) {
        launch(args);
    }
}
