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
    private int portCount = 0;
    private int totalSmsSent = 0;

    public DatabaseOperation(String filePath) throws SQLException, FileNotFoundException {

        reader = new FileReader(filePath);
        txtIn = new BufferedReader(reader);

        url = "jdbc:sqlite:C://sqlite/SMSLog.db";
        conn = DriverManager.getConnection(url);
        stmt = conn.createStatement();
        ps = conn.prepareStatement("insert into sends (card, port, result, length, position) values (?,?,?,?,?)");
    }

    public DatabaseOperation() throws SQLException {

        url = "jdbc:sqlite:C://sqlite/SMSLog.db";
        conn = DriverManager.getConnection(url);
        stmt = conn.createStatement();

    }

    public void parseFileIntoDB() throws SQLException, IOException {

        initDatabase();

        String tmp = txtIn.readLine();
        conn.setAutoCommit(false);

        while (tmp != null) {

            line = tmp;

            recordsCount++;

            String errorArray[] = line.split("[|]");

            String card = (errorArray[1]);
            String port = (errorArray[2]);
            String result = (errorArray[5]);
            String smsLength = (errorArray[11].substring(4, 5));
            String simPosition = (errorArray[14]);

            checkForKeyword(card, port, result, smsLength, simPosition);

            tmp = txtIn.readLine();
        }

        executeBatchAndClose();

    }

    public void initDatabase() throws SQLException {

        conn.setAutoCommit(false);

        try {
            String sqlCommand = "delete from sends;";
            stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {

        }

        for (int card = 21; card <= 28; card++) {

            for (int port = 1; port <= 4; port++) {

                for (int pos = 1; pos <= 4; pos++) {

                    //String sqlCommand = "insert into sends values ('" + card + "','" + port + "','Nill','0','" + pos + "');";
                    ps.setInt(1, (card));
                    ps.setInt(2, (port));
                    ps.setString(3, ("Dave"));
                    ps.setInt(4, (0));
                    ps.setInt(5, (pos));
                    ps.addBatch();
                }
            }
        }
        ps.executeBatch();
        conn.commit();
        System.out.println("Finished");
    }

    private void checkForKeyword(String card, String port, String result, String smsLength, String simPosition) throws SQLException {

        if (result.contains("confirmation")) {
            return;
        }
        ps.setInt(1, Integer.parseInt(card));
        ps.setInt(2, Integer.parseInt(port));
        ps.setString(3, (result));
        ps.setInt(4, Integer.parseInt(smsLength));
        ps.setInt(5, Integer.parseInt(simPosition));
        ps.addBatch();

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

    public void sendDBQueries() {

        for (int card = 21; card <= 28; card++) {

            for (int port = 1; port <= 4; port++) {

                for (int pos = 1; pos <= 4; pos++) {

                    String sqlCommand = "select sum(length) from sends where card = '" + card + "' and port = '" +
                            port + "' and position = '" + pos + "';";

                    //System.out.println("sqlCommand = " + sqlCommand);
                    //sendPositionTotalToUI(sqlCommand);
                }
            }
        }
    }

    private void sendPositionTotalToUI(String sqlCommand) throws SQLException {

        boolean isEmpty = true;

        rs = stmt.executeQuery(sqlCommand);
        while (rs.next()) {
            isEmpty = false;

            portCount = +Integer.parseInt(rs.getString(1));
            totalSmsSent += portCount;
        }
        portCount = +0;


        System.out.println("smsCount = " + portCount + " / " + totalSmsSent);
    }
}
