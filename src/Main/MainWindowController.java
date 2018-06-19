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

    TextArea[] area = {area1, area2, area3, area4, area5, area6, area7, area8};
    int[] values = new int[128];

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
        int resultCount = 0;
        int results = 0;

        for (int card = 21; card <= 28; card++) {

            for (int port = 1; port <= 4; port++) {

                for (int pos = 1; pos <= 4; pos++) {

                    String sqlCommand = "SELECT SUM(length) FROM sends WHERE card = '" + card + "' AND port = '" +
                            port + "' AND position = '" + pos + "';";

                    results = operation.sendDBQueries(sqlCommand);

                    totalSmsSent += results;
                    //System.out.println(card + "/" + port + "/" + pos + " [count = " + results + "]");


                    values[resultCount] = results;
                    resultCount++;

                }
            }
        }
        sendToTextAreas();
        totalSmsSentLabel.setText(String.valueOf(totalSmsSent));
    }

    private void sendToTextAreas() {

        for (int i = 0; i < values.length; i++) {
            System.out.println(values[i]);
        }


    }
}
