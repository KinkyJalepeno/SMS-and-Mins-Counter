package Main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class MainWindowController {

    private String filePath;

    @FXML
    private TextField filePathLabel;
    @FXML
    private Label recordsParsed;
    @FXML
    private Label totalMins;
    @FXML
    private TextArea area1;
    @FXML
    private TextArea area2;
    @FXML
    private TextArea area3;
    @FXML
    private TextArea area4;
    @FXML
    private TextArea area5;
    @FXML
    private TextArea area6;
    @FXML
    private TextArea area7;
    @FXML
    private TextArea area8;

    @FXML
    private void browseToFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Log Files", "*.log"));

        fileChooser.setTitle("Open SMS CDR");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {

            filePath = (file.getAbsolutePath());
            filePathLabel.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void parseFile() throws SQLException, IOException {

        DatabaseOperation operation = new DatabaseOperation(filePath);
        operation.parseFileIntoDB();

        int count = operation.getCount();
        recordsParsed.setText(String.valueOf(count));

        retrieveMinutesUsed();

    }

    private void retrieveMinutesUsed() throws SQLException {

        TextArea[] areas = {area1, area2, area3, area4, area5, area6, area7, area8};

        DatabaseOperation operation = new DatabaseOperation();

    }
}
