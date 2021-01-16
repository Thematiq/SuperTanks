package app.windows;

import app.windows.controllers.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/game.fxml"));
        Parent root = loader.load();
        GameController gc = loader.getController();
        Scene scene = new Scene(root);
        stage.setTitle("Super hot tanks");
        stage.getIcons().add(new Image(getClass().getResource("resources/icon.png").toURI().toString()));
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(gc::inputHandler);
    }

    public static void run(String[] args) {
        launch(args);
    }
}