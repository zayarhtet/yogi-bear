package view;

import resource.ResourceLoader;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import model.Game;
import model.CellItem;
import model.Position;
import model.Ranger;


public class Board extends JPanel {
    private Game game;
    private final Image basket, player, wall, empty, ranger;
    private double scale;
    private int scaled_size;
    private final int tile_size = 32;

    public Board(Game g) throws IOException {
        game = g;
        scale = 1.0;
        scaled_size = (int) (scale * tile_size);
        basket = ResourceLoader.loadImage("resource/basket.png");
        player = ResourceLoader.loadImage("resource/player.png");
        wall = ResourceLoader.loadImage("resource/wall.png");
        empty = ResourceLoader.loadImage("resource/empty.png");
        ranger = ResourceLoader.loadImage("resource/ranger.png");
    }

    public boolean setScale(double scale) {
        this.scale = scale;
        scaled_size = (int) (scale * tile_size);
        return refresh();
    }

    public boolean refresh() {
        if (!game.isLevelLoaded()) return false;
        Dimension dim = new Dimension(game.getLevelColNumber() * scaled_size, game.getLevelRowNumber() * scaled_size);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setSize(dim);
        repaint();
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!game.isLevelLoaded()) return;
        Graphics2D gr = (Graphics2D) g;
        int w = game.getLevelColNumber();
        int h = game.getLevelRowNumber();
        Position p = game.getPlayerPosition();
        List<Ranger> rangers = game.getRangers(); Iterator<Ranger> iter = rangers.iterator();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Image img = null;
                CellItem ci = game.getItem(y, x);
                switch (ci) {
                    case BASKET:
                        img = basket;
                        break;
                    case WALL:
                        img = wall;
                        break;
                    case EMPTY:
                        img = empty;
                        break;
                }
                if (p.x == x && p.y == y) img = player;
                if (img == null) continue;
                gr.drawImage(img, x * scaled_size, y * scaled_size, scaled_size, scaled_size, null);
            }
        }
        while (iter.hasNext()) {
            Ranger r = iter.next();
            int x = r.getPosition().x, y = r.getPosition().y;
            gr.drawImage(ranger, x * scaled_size, y * scaled_size, scaled_size, scaled_size, null);
        }
    }
}
