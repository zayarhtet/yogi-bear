package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameLevel {
    public final String        gameID;
    public final int           totalRow, totalCol;
    public final CellItem[][]  level;
    public       List <Ranger> rangers = new ArrayList<>();
    public       Position      player = new Position(0,0);
    private      Position      originalPlayerPosition = new Position(0,0);
    private      int           collectedBasket, elapsedTime = 0, numBaskets;

    public GameLevel(List<String> gameLevelRows, String id) {
        this.gameID = id;
        int c = 0;
        for (String s : gameLevelRows) if (s.length() > c) c = s.length();
        totalRow = gameLevelRows.size(); totalCol = c;
        level = new CellItem[totalRow][totalCol];
        numBaskets = 0; collectedBasket = 0;

        for (int i = 0; i < totalRow; i++) {
            String s = gameLevelRows.get(i);
            for (int j = 0; j < s.length(); j++) {
                switch (s.charAt(j)) {
                    case '@': player = new Position(j, i); originalPlayerPosition = new Position(j,i);
                              level[i][j] = CellItem.EMPTY; break;
                    case '#': level[i][j] = CellItem.WALL; break;
                    case '*': level[i][j] = CellItem.BASKET;
                              numBaskets++; break;
                    case 'H': rangers.add(new Ranger(Ranger.Axis.HORIZONTAL, new Position(j,i)));
                              level[i][j] = CellItem.EMPTY; break;
                    case 'V': rangers.add(new Ranger(Ranger.Axis.VERTICAL, new Position(j,i)));
                              level[i][j] = CellItem.EMPTY; break;
                    default:  level[i][j] = CellItem.EMPTY; break;
                }
            }
            for (int j = s.length(); j<totalCol; j++) {
                level[i][j] = CellItem.EMPTY;
            }
        }
    }

    public GameLevel(GameLevel gl) {
        gameID = gl.gameID;
        totalRow = gl.totalRow;
        totalCol = gl.totalCol;
        numBaskets = gl.numBaskets;
        collectedBasket = gl.collectedBasket;
        originalPlayerPosition = new Position (gl.originalPlayerPosition);
        level = new CellItem[totalRow][totalCol];
        player = new Position(gl.player);
        rangers = new ArrayList<> (gl.rangers);
        for (int i = 0; i < totalRow; i++) {
            System.arraycopy(gl.level[i], 0, level[i], 0, totalCol);
        }
    }

    public boolean isGameEnded() { return numBaskets <= collectedBasket;  }
    public boolean isValidPosition(Position p) { return (p.x >= 0 && p.y >= 0 && p.x < totalCol && p.y < totalRow && level[p.y][p.x] != CellItem.WALL); }
    public void moveRanger(Game gl) {
        Iterator <Ranger> iter = rangers.iterator();
        while (iter.hasNext()) {
            Ranger r = iter.next();
            Position pos = r.getPosition();
            boolean d = (r.getDirection() == Ranger.Axis.HORIZONTAL);

            Position next = pos.translate(d?Direction.LEFT:Direction.UP);
            if (r.isGoingLEFTorUP && isValidPosition(next)) r.setPosition(next);
            else r.isGoingLEFTorUP = false;

            next = pos.translate(d?Direction.RIGHT:Direction.DOWN);
            if ((!r.isGoingLEFTorUP) && isValidPosition(next)) r.setPosition(next);
            else r.isGoingLEFTorUP = true;

        }
        if (isCollided()) collided(gl);
    }
    public void collided(Game gl) {
        if (gl.lives == 0){
            System.out.println("Passed");
            return;
        }
        if (--gl.lives > 0) player = new Position(originalPlayerPosition);
        // ask for the name and end the game
    }

    public boolean movePlayer(Direction d, Game g) {
        if (isGameEnded()) return false;

        Position next = player.translate(d);
        if (!isValidPosition(next)) return false;

        if (isCollided(next)) {
            collided(g);
            return true;
        }

        player = next;
        if (level[next.y][next.x] == CellItem.BASKET) {
            level[next.y][next.x] = CellItem.EMPTY;
            collectedBasket++;
        }
        return true;
    }

    public boolean isCollided(Position next) {
        Iterator<Ranger> iter = rangers.iterator();
        while(iter.hasNext()) {
            Ranger r = iter.next();
            if (r.getPosition().x == next.x && r.getPosition().y == next.y) return true;
        }
        return false;
    }

    public boolean isCollided() {
        return isCollided(player);
    }

    public void printLevel(){
        int x = player.x, y = player.y;
        for (int i = 0; i < totalRow; i++){
            for (int j = 0; j < totalCol; j++){
                if (i == y && j == x)
                    System.out.print('@');
                else 
                    System.out.print(level[i][j].representation);
            }
            System.out.println("");
        }
    }

    public int getCollectedBasket() { return collectedBasket; }

    public int getNumBasket() { return numBaskets; }

    public int getElapsedTime() { return elapsedTime++; }

}
