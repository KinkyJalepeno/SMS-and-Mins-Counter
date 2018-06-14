package Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DatabaseOperation {

    private Connection conn;
    private Statement stmt;
    private PreparedStatement ps;
    private ResultSet rs;

    private FileReader reader;
    private BufferedReader txtIn;

    private String url;
    private String line;

    private int recordsCount = 0;
    private int totalSmsSent = 0;

    public DatabaseOperation(String filePath) throws SQLException, FileNotFoundException {

        reader = new FileReader(filePath);
        txtIn = new BufferedReader(reader);

        url = "jdbc:sqlite:C://sqlite/SMSLog.db";
        conn = DriverManager.getConnection(url);
        stmt = conn.createStatement();
        ps = conn.prepareStatement("insert into sends (card, port, position, length) values (?,?,?,?)");
    }

    public DatabaseOperation() throws SQLException {

        url = "jdbc:sqlite:C://sqlite/SMSLog.db";
        conn = DriverManager.getConnection(url);
        stmt = conn.createStatement();

    }

    public void flushDatabase() {

        String sqlCommand = "DELETE FROM sends;";
        try {
            stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {

        }

    }

    public void parseFileIntoDB() throws SQLException, IOException {

        flushDatabase();

        String tmp = txtIn.readLine();
        conn.setAutoCommit(false);

        while (tmp != null) {

            line = tmp;

            recordsCount++;

            String errorArray[] = line.split("[|]");

            String card = (errorArray[1]);
            String port = (errorArray[2]);
            String simPosition = (errorArray[11].substring(4, 5));
            String smsLength = (errorArray[14]);

            ps.setInt(1, Integer.parseInt(card));
            ps.setInt(2, Integer.parseInt(port));
            ps.setInt(3, Integer.parseInt(simPosition));
            ps.setInt(4, Integer.parseInt(smsLength));
            ps.addBatch();

            tmp = txtIn.readLine();
        }

        executeBatchAndClose();

    }

    private void executeBatchAndClose() throws SQLException, IOException {

        ps.executeBatch();
        conn.commit();

        ps.close();
        reader.close();
        txtIn.close();
        //conn.close();

        System.out.println("Job done !!!");

    }

    public int getCount() {

        return recordsCount;
    }

    public void sendDBQueries() throws SQLException {

        int card = 21, port = 1, position = 1, smsCount = 0;

        String sqlCommand = "select length from sends where card = '" + card + "' and port = '" +
                port + "' and position = '" + position + "';";

        System.out.println("sqlCommand = " + sqlCommand);

        rs = stmt.executeQuery(sqlCommand);

        while (rs.next()) {

            String smsSent = rs.getString(1);
            smsCount += Integer.parseInt(smsSent);
        }
        totalSmsSent += smsCount;
        System.out.println("smsCount = " + smsCount);

        sendPortTotalToUI(smsCount);
    }

    private void sendPortTotalToUI(int smsCount) {


    }
}
