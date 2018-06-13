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
    private TextArea textArea;
    @FXML
    private TextField filePathLabel;
    @FXML
    private Label recordsParsed;
    @FXML
    private Label totalMins;

//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//
//        DatabaseOperation operation = null;
//        try {
//            operation = new DatabaseOperation();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        operation.flushDatabase();
//    }

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

        textArea.clear();

        DatabaseOperation operation = new DatabaseOperation(filePath);
        operation.parseFileIntoDB();

        int count = operation.getCount();
        recordsParsed.setText(String.valueOf(count));
    }


}
