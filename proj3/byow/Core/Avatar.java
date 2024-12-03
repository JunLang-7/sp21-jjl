package byow.Core;

import byow.Core.Map.WorldMap;
import byow.TileEngine.Tileset;

import java.io.Serializable;

import static byow.Core.MyUtils.save;


public class Avatar implements Serializable {
    private final WorldMap worldMap;
    private int x, y;
    private boolean quit = false;
    private boolean gameOver = false;
    private final int[][] dirs = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

    public Avatar(WorldMap worldMap) {
        this.worldMap = worldMap;
        Position entry = worldMap.getEntry();
        x = entry.getX();
        y = entry.getY();
    }

    public void move(char direction) {
        switch (direction) {
            case 'w', 'W': goUp(); break;
            case 'a', 'A': goLeft(); break;
            case 's', 'S': goDown(); break;
            case 'd', 'D': goRight(); break;
            case ':': quit = true; break;
            case 'q', 'Q': if (quit) {
                worldMap.setEntry(new Position(x, y));
                save(worldMap);
                System.exit(0);
            }
            default: break;
        }
        gameOver = worldMap.getTile(x, y).equals(Tileset.LOCKED_DOOR);
        worldMap.setTiles(x, y, Tileset.AVATAR);
    }

    /**
     * The function that abstract the direction move
     * @param dir the direction
     */
    private void goDir(int dir) {
        quit = false;
        if (worldMap.getTile(x + dirs[dir][0], y + dirs[dir][1]).equals(Tileset.FLOOR)
                || worldMap.getTile(x + dirs[dir][0], y + dirs[dir][1]).equals(Tileset.LOCKED_DOOR)) {
            worldMap.setTiles(x, y, Tileset.FLOOR);
            switch (dir) {
                case 0: y++; break;
                case 1: x--; break;
                case 2: y--; break;
                case 3: x++; break;
                default: break;
            }
        }
    }

    private void goUp() {
       goDir(0);
    }

    private void goLeft() {
        goDir(1);
    }

    private void goDown() {
        goDir(2);
    }

    private void goRight() {
        goDir(3);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
