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
        conn.setAutoCommit(false);
        stmt = conn.createStatement();
        ps = conn.prepareStatement("insert into sends (card, port, result, length, position) values (?,?,?,?,?)");
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
            String smsLength = (errorArray[11].substring(4, 5));
            String simPosition = (errorArray[14]);

            checkForKeyword(card, port, result, smsLength, simPosition);

            tmp = txtIn.readLine();
        }

        executeBatch();

        //sendDBQueries();
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

    private void checkForKeyword(String card, String port, String result, String smsLength, String pos) throws SQLException {

        if (result.contains("confirmation")) {
            return;
        }
        String sqlCommand = "INSERT INTO sends VALUES ('" + card + "','" + port + "','" + result + "','" + smsLength +
                "','" + pos + "');";
        //System.out.println("sqlCommand = " + sqlCommand);
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

    public int sendDBQueries(String sqlCommand) throws SQLException {


        rs = stmt.executeQuery(sqlCommand);
        int results = rs.getInt(1);

        return results;
    }
}
