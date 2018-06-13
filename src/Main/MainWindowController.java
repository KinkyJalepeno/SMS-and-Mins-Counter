package Main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainWindowController {

    @FXML
    private TextField filePath;
    @FXML
    private Label recordsParsed;
    @FXML
    private Label totalMins;


    @FXML
    private void chooseFile() {

        System.out.println("Hello there");

    }

    @FXML
    private void parseFile() {

        System.out.println("parse file button");
    }

}
