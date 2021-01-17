package app.windows.controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Game parameters window controller
 * @author Mateusz Praski
 */
public class GameOptionsController implements Initializable {
    @FXML
    private TextField hpText;
    @FXML
    private TextField enemyText;
    @FXML
    private TextField obstacleText;
    @FXML
    private Button startButton;

    @FXML
    private void startClick() throws IOException {
        Stage currentStage = (Stage) this.enemyText.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/game.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        GameController gc = loader.getController();
        gc.spawnWorld(Integer.parseInt(this.enemyText.getText()),
                Integer.parseInt(this.obstacleText.getText()),
                Integer.parseInt(this.hpText.getText()));
        currentStage.setScene(scene);
        scene.setOnKeyPressed(gc::inputHandler);
    }

    private final Pattern numericPattern = Pattern.compile("^([1-9][0-9]*)?$");

    private TextFormatter.Change numericChange(TextFormatter.Change change) {
        if (numericPattern.matcher(change.getControlNewText()).matches()) {
            return change;
        } else {
            return null;
        }
    }

    private void checkOptions(ObservableValue<? extends String> observableValue, String number, String number1) {
        this.startButton.setDisable(this.obstacleText.getText().equals("") ||
                this.enemyText.getText().equals("") ||
                this.hpText.getText().equals(""));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.hpText.setTextFormatter(new TextFormatter<>(this::numericChange));
        this.hpText.textProperty().addListener(this::checkOptions);
        this.enemyText.setTextFormatter(new TextFormatter<>(this::numericChange));
        this.enemyText.textProperty().addListener(this::checkOptions);
        this.obstacleText.setTextFormatter(new TextFormatter<>(this::numericChange));
        this.obstacleText.textProperty().addListener(this::checkOptions);
        this.startButton.setDisable(true);
    }
}
