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

    public DatabaseOperation(String filePath) throws SQLException, FileNotFoundException {

        reader = new FileReader(filePath);
        txtIn = new BufferedReader(reader);

        url = "jdbc:sqlite:C://sqlite/SMSLog.db";
        conn = DriverManager.getConnection(url);
        conn.setAutoCommit(false);
        stmt = conn.createStatement();
        ps = conn.prepareStatement("insert into sends (card, port, result, length, scid, position) values (?,?,?,?,?,?)");
    }

    public DatabaseOperation() throws SQLException {

        url = "jdbc:sqlite:C://sqlite/SMSLog.db";
        conn = DriverManager.getConnection(url);
        conn.setAutoCommit(false);
        stmt = conn.createStatement();

    }

    public void parseFileIntoDB() throws SQLException, IOException {

        initDatabase();

        String tmp = txtIn.readLine();

        while (tmp != null) {

            line = tmp;

            recordsCount++;

            String errorArray[] = line.split("[|]");

            String card = (errorArray[1]);
            String port = (errorArray[2]);
            String result = (errorArray[5]);
            String smsLength = (errorArray[11].substring(4));
            String scid = errorArray[13];
            String simPosition = (errorArray[14]);

            checkForKeyword(card, port, result, smsLength, scid, simPosition);

            tmp = txtIn.readLine();
        }
        executeBatch();
    }

    public void initDatabase() {

        String sqlCommand = "DELETE FROM sends;";
        try {
            stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {

        }
    }

    private void checkForKeyword(String card, String port, String result, String smsLength, String scid, String simPosition) throws SQLException {

        if (result.contains("confirmation") || scid.equals("fake")) {
            return;
        }
        String sqlCommand = "INSERT INTO sends VALUES ('" + card + "','" + port + "','" + result + "','" + smsLength +
                "','" + scid + "','" + simPosition + "');";

        stmt.addBatch(sqlCommand);

    }

    private void executeBatch() throws SQLException {

        stmt.executeBatch();
        conn.commit();
        System.out.println("Batch write to db complete !!!");

    }

    public int getCount() {

        return recordsCount;
    }

    public int sendDBQueries(String sqlCommand) {

        int results;

        try {
            rs = stmt.executeQuery(sqlCommand);
            results = rs.getInt(2);

        } catch (SQLException e) {
            return 0;
        }

        return results;

    }

    public String sendDBQueries2(String sqlCommand) {

        String simID;

        try {
            rs = stmt.executeQuery(sqlCommand);
            simID = rs.getString(1);
        } catch (SQLException e) {
            return "unknown";
        }

        return simID;
    }

    public void closeConnection() throws SQLException {

        conn.close();

    }
}
