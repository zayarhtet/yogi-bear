package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import model.Game;
import model.Direction;

public class MainWindow extends JFrame {
    private final Game      game;
    private       Board     board;
    private final JLabel    livesLabel,
                            collectedBasketLabel,
                            elapsedTimeLabel,
                            overallTimerLabel;
    private final Timer     rangerTimer;
    private final Timer     elapsedTimer;

    public MainWindow() throws IOException, SQLException {
        game = new Game();

        setTitle("Yogi Bear");
        setSize(600, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        URL url = MainWindow.class.getClassLoader().getResource("resource/player.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(url));

        JMenuBar menuBar = new JMenuBar();
        JMenu menuGame = new JMenu("Options");
        JMenu menuGameLevel = new JMenu("Level");
        JMenu menuGameScale = new JMenu("Scale");
        createGameLevelMenuItems(menuGameLevel);
        createScaleMenuItems(menuGameScale, 1.0, 2.0, 0.5);
        
        
       JMenuItem menuScoreBoard = new JMenuItem(new AbstractAction("Scoreboard") {
           @Override
           public void actionPerformed(ActionEvent e) {
               new HighScoreWindow(game.getHighScores(), MainWindow.this);
           }
       });

        JMenuItem menuGameExit = new JMenuItem(new AbstractAction("Kilépés") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuGame.add(menuGameLevel);
        menuGame.add(menuGameScale);
        menuGame.add(menuScoreBoard);
        menuGame.addSeparator();
        menuGame.add(menuGameExit);
        menuBar.add(menuGame);

        setJMenuBar(menuBar);
        setLayout(new BorderLayout(0, 10));

        /**
         * add the game statistics panel (Grid)
         */
        JPanel gameStatPanel = new JPanel();
        gameStatPanel.setLayout(new GridLayout(2,2));

        livesLabel = new JLabel();
        collectedBasketLabel = new JLabel();
        elapsedTimeLabel = new JLabel(); overallTimerLabel = new JLabel();
        refreshGameStatLabel();
        gameStatPanel.add(livesLabel); gameStatPanel.add(collectedBasketLabel);
        gameStatPanel.add(elapsedTimeLabel); gameStatPanel.add(overallTimerLabel);
        add(gameStatPanel, BorderLayout.NORTH);

        /**
         * add a board panel
         */
        try { add(board = new Board(game), BorderLayout.CENTER); } catch (IOException ex) {}

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke);
                if (!game.isLevelLoaded()) return;
                
                int kk = ke.getKeyCode();
                Direction d = null;
                if (kk == KeyEvent.VK_W || kk == KeyEvent.VK_UP) d = Direction.UP;
                else if (kk == KeyEvent.VK_S || kk == KeyEvent.VK_DOWN) d = Direction.DOWN;
                else if (kk == KeyEvent.VK_A || kk == KeyEvent.VK_LEFT) d = Direction.LEFT;
                else if (kk == KeyEvent.VK_D || kk == KeyEvent.VK_RIGHT) d = Direction.RIGHT;
                else if (kk == KeyEvent.VK_ESCAPE) game.loadGame(game.getGameID());

                board.repaint();
                if (d != null && game.step(d)) {
                    if (game.isGameEnded()) {
                        try {
                            if (game.isAllEnded()) {
                                String msg = "Congrats! You won all the level!";
                                if (game.isBetterHighScore()) msg += " You are in the highscore list!";
                                prompt(msg, "All Done!", true);
                                game.resetGameIDIter();
                            } else {
                                if (game.isAlive()) prompt("Congrats! You have finished level "+ game.getGameID(),"Finished", false);
                                else {
                                    rangerTimer.stop();
                                    deadAction();
                                    rangerTimer.start();
                                    game.resetGameIDIter();
                                }
                            }
                        } catch (SQLException e) { throw new RuntimeException(e); }
                        game.loadGame(); board.refresh(); pack();
                    }
                }
            }
        });

        rangerTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (game.isLevelLoaded()) {
                    game.roamRangers();
                    try {
                        deadAction();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    board.repaint();
                }
            }
        });

        elapsedTimer = new Timer (1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                refreshGameStatLabel();
            }
        });

        setResizable(false);
        setLocationRelativeTo(null);
        game.loadGame();
        rangerTimer.start(); elapsedTimer.start();
        board.setScale(2.0);
        pack();
        refreshGameStatLabel();
        setVisible(true);
    } // MainWindow()

    private void prompt(String msg, String title, boolean askForName) throws SQLException {
        elapsedTimer.stop();
        JOptionPane.showMessageDialog(MainWindow.this, msg, title, JOptionPane.INFORMATION_MESSAGE);
        if (askForName) askForNameAndStore();
        elapsedTimer.start();
    }
    private void deadAction() throws SQLException {
        if (!game.isAlive()) {
            String msg = "Oops! You are dead!";
            prompt("Oops! You are dead!", "Game Over", true);
            game.resetGameIDIter();
            elapsedTimer.start();
            game.loadGame(); board.refresh(); pack();
        }
    }
    private void askForNameAndStore() throws SQLException {
        EditDlg editDlg = new EditDlg(MainWindow.this, "Enter your name");
        editDlg.setSize(250,100);
        while ( editDlg.getButtonCode() != OKCancelDialog.OK ) editDlg.setVisible(true);

        String playerName = editDlg.getValue();

        System.out.println("player name: "+ playerName);
        game.saveScore(playerName);
    }

    private void createGameLevelMenuItems(JMenu menuGameLevel) {
        for (String id: game.getLevelsOfDifficulty()) {
            JMenuItem menuItem = new JMenuItem(new AbstractAction("Level - " + id) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.loadGame(id);
                    board.refresh();
                    pack();
                }
            });
            menuGameLevel.add(menuItem);
        }
    }

    private void refreshGameStatLabel() {
        livesLabel.setText("Lives: " + game.getLives());
        collectedBasketLabel.setText("Collected Basket: " + game.getCollectedBasket());
        elapsedTimeLabel.setText("Elapsed Time: " + game.getElapsedTime());
        overallTimerLabel.setText("Overall Elapsed Time: " + game.getOverallTime());
    }

    private void createScaleMenuItems(JMenu menu, double from, double to, double by) {
        while (from <= to) {
            final double scale = from;
            JMenuItem item = new JMenuItem(new AbstractAction(from +"x") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (board.setScale(scale)) pack();
                }
            });
            menu.add(item);

            if (from == to) break;
            from += by;
            if (from > to) from = to;
        }
    }
    
}
