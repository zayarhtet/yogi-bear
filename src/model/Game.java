package model;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import persistence.Database;
import persistence.HighScore;
import resource.ResourceLoader;

public class Game {
    private final HashMap<String, GameLevel>  gameLevelDatabase;
    private       Iterator <String>           gameIDIter;
    private       GameLevel                   currentGameLevel = null;
    private final Database                    database;
    private       boolean                     isBetterHighScore = false;
    private       int                         timer = 0;
    public int lives = 3;

    public Game() throws SQLException {
        gameLevelDatabase = new HashMap<>();
        database = new Database(10);
        readMaps();
    }

    public void loadGame() {
        if (!isAllEnded()) loadGame(gameIDIter.next());
    }

    public void resetGameIDIter() {
        gameIDIter = gameLevelDatabase.keySet().iterator();
        timer = 0; isBetterHighScore = false; lives = 3;
    }

    public void loadGame(String id) {
        currentGameLevel = new GameLevel(gameLevelDatabase.get(id));
        isBetterHighScore = false;
    }

    public void printGameLevel() { currentGameLevel.printLevel(); }

    public boolean step(Direction d) {
        return currentGameLevel.movePlayer(d, this);
    }

    public Collection<String> getLevelsOfDifficulty() { return gameLevelDatabase.keySet();}
    public boolean isLevelLoaded() { return currentGameLevel != null; }
    public int getLevelRowNumber() { return currentGameLevel.totalRow; }
    public int getLevelColNumber() { return currentGameLevel.totalCol; }
    public CellItem getItem(int row, int col) { return currentGameLevel.level[row][col];}
    public String getGameID() { return (currentGameLevel != null) ? currentGameLevel.gameID : null; }
    public int getCollectedBasket() { return (currentGameLevel != null)? currentGameLevel.getCollectedBasket() : 0; }
    public int getLives() { return lives;}
    public int getElapsedTime() { return (currentGameLevel != null) ? currentGameLevel.getElapsedTime() : 0; }
    public int getOverallTime() { return (currentGameLevel != null) ? timer++ : 0;}
    public boolean isGameEnded() { return currentGameLevel != null && (currentGameLevel.isGameEnded() || lives == 0); }
    public boolean isBetterHighScore() { return isBetterHighScore; }
    public Position getPlayerPosition() { return new Position(currentGameLevel.player.x, currentGameLevel.player.y); }
    public List<Ranger> getRangers() { return new ArrayList<Ranger>(currentGameLevel.rangers); }
    public List<HighScore> getHighScores() { return database.getHighScores(); }
    public boolean isAlive() { return lives > 0; }
    public boolean isAllEnded() { return !gameIDIter.hasNext(); }
    public void saveScore(String name) throws SQLException {
        database.putHighScore(name, currentGameLevel.gameID, calculateScore());
    }

    public int calculateScore() {
        return (10000-timer)*Integer.parseInt(currentGameLevel.gameID);
    }

    public void roamRangers() {
        currentGameLevel.moveRanger(this);
    }

    private void readMaps() {
        InputStream is;
        is = ResourceLoader.loadResource("resource/levels.txt");

        try (Scanner sc = new Scanner(is)) {
            String line = readNextLine(sc);
            List<String> gameLevelRows = new ArrayList<>();

            while(!line.isEmpty()) {
                String id = readGameID(line);
                if (id == null) return;

                gameLevelRows.clear();
                line = readNextLine(sc);
                while (!line.isEmpty() && line.trim().charAt(0) != ';') {
                    gameLevelRows.add(line);
                    line = readNextLine(sc);
                }
                gameLevelDatabase.put(id, new GameLevel(gameLevelRows, id));
            }
        } catch (Exception e) {
            System.out.println("Error while loading the maps");
        }
        resetGameIDIter();
    }

    private String readNextLine(Scanner sc) {
        String line = "";
        while (sc.hasNextLine() && line.trim().isEmpty()) {
            line = sc.nextLine();
        }
        return line;
    }

    private String readGameID(String line) {
        line = line.trim();
        if (line.isEmpty() || line.charAt(0) != ';') return null;
        Scanner s = new Scanner(line);
        s.next();
        return s.next();
    }

}
