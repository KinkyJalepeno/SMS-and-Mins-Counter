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
    private int totalSmsSent = 0;

    @FXML
    private TextField filePathLabel;
    @FXML
    private Label recordsParsed;
    @FXML
    private Label totalSmsSentLabel;
    @FXML
    private TextArea area1;

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

        DatabaseOperation operation = new DatabaseOperation();

        int results;

        for (int card = 21; card <= 28; card++) {

            for (int port = 1; port <= 4; port++) {

                area1.appendText("\n");

                for (int pos = 1; pos <= 4; pos++) {

                    String sqlCommand = "SELECT SUM(length) FROM sends WHERE card = '" + card + "' AND port = '" +
                            port + "' AND position = '" + pos + "';";

                    results = operation.sendDBQueries(sqlCommand);

                    totalSmsSent += results;
                    area1.appendText(card + " / " + port + " / " + pos + " - [count = " + results + "]\n");

                }
            }
        }
        totalSmsSentLabel.setText(String.valueOf(totalSmsSent));
    }
}
