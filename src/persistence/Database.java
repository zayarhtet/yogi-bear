package persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

public class Database {
    private final int maxScores;
    private final String tableName = "highscore";
    private final Connection conn;
    private PreparedStatement insertStatement;
    private PreparedStatement deleteStatement;
    private final List<HighScore> highScores = new ArrayList<>();

    public Database(int maxScores) throws SQLException {
        this.maxScores = maxScores;
        Connection c = null;
        try {
            Properties connectionProps = new Properties();
            // Add new user -> MySQL workbench (Menu: Server / Users and priviliges)
            //                             Tab: Administrative roles -> Check "DBA" option
            connectionProps.put("user", "root");
            connectionProps.put("password", "htet4410");
            connectionProps.put("serverTimezone", "UTC");
            String dbURL = "jdbc:mysql://localhost:3306/yogibear";
            c = DriverManager.getConnection(dbURL, connectionProps);

            String insertQuery = "INSERT INTO HIGHSCORE (NAME, LEVEL, SCORE) VALUES (?, ?, ?)";
            insertStatement = c.prepareStatement(insertQuery);
            String deleteQuery = "DELETE FROM HIGHSCORE WHERE SCORE=?"; // + score;
            deleteStatement = c.prepareStatement(deleteQuery);
        } catch (Exception ex) {
            System.out.println("No connection");
        }
        this.conn = c;
        loadHighScores();

    }

    private void loadHighScores() {
        try (Statement stmt = conn.createStatement()) {
            highScores.clear();
            ResultSet rs = stmt.executeQuery("SELECT * FROM highscore order by score desc");
            while (rs.next()) {
                String name = rs.getString("name");
                String level = rs.getString("level");
                int score = rs.getInt("score");
                highScores.add(new HighScore(name, level, score));
            }
        } catch (Exception e){ System.out.println("loadHighScores error: " + e.getMessage()); }
    }

    public List<HighScore> getHighScores() { loadHighScores(); return new ArrayList<>(highScores); }

    public void putHighScore(String name, String level, int score) throws SQLException {
        loadHighScores();
        if (highScores.size() < maxScores) {
            insertScore(name, level, score);
        } else {
            int leastScore = highScores.get(highScores.size() -1).score;
            if (score > leastScore) {
                deleteScore(leastScore);
                insertScore(name, level, score);
            }
        }
    }

    public void insertScore(String name, String level, int score) throws SQLException {
        System.out.println(name + ", " + level + ", " + score);
        insertStatement.setString(1, name);
        insertStatement.setString(2, level);
        insertStatement.setInt(3, score);
        insertStatement.executeUpdate();
    }

    public void deleteScore(int score) throws SQLException {
        deleteStatement.setInt(1, score);
        deleteStatement.executeUpdate();
    }

}

