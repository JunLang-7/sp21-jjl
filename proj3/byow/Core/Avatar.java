package byow.Core;

import byow.Core.Map.WorldMap;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

import static byow.Core.MyUtils.save;


public class Avatar implements Serializable {
    private final WorldMap worldMap;
    private int x, y;
    private boolean quit = false;
    private boolean gameOver = false;

    public Avatar(WorldMap worldMap) {
        this.worldMap = worldMap;
        Position entry = worldMap.getEntry();
        x = entry.x;
        y = entry.y;
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
        gameOver = worldMap.tiles[x][y].equals(Tileset.LOCKED_DOOR);
        worldMap.setTiles(new Position(x, y), Tileset.AVATAR);
    }

    private void goUp() {
        quit = false;
        if (worldMap.tiles[x][y + 1].equals(Tileset.FLOOR)
                || worldMap.tiles[x][y + 1].equals(Tileset.LOCKED_DOOR)) {
            worldMap.setTiles(new Position(x, y), Tileset.FLOOR);
            y++;
        }
    }

    private void goLeft() {
        quit = false;
        if (worldMap.tiles[x - 1][y].equals(Tileset.FLOOR)
                || worldMap.tiles[x - 1][y].equals(Tileset.LOCKED_DOOR)) {
            worldMap.setTiles(new Position(x, y), Tileset.FLOOR);
            x--;
        }
    }

    private void goDown() {
        quit = false;
        if (worldMap.tiles[x][y - 1].equals(Tileset.FLOOR)
                || worldMap.tiles[x][y - 1].equals(Tileset.LOCKED_DOOR)) {
            worldMap.setTiles(new Position(x, y), Tileset.FLOOR);
            y--;
        }
    }

    private void goRight() {
        quit = false;
        if (worldMap.tiles[x + 1][y].equals(Tileset.FLOOR)
                || worldMap.tiles[x + 1][y].equals(Tileset.LOCKED_DOOR)) {
            worldMap.setTiles(new Position(x, y), Tileset.FLOOR);
            x++;
        }
    }

    public boolean checkCollision(TETile tile) {
        return tile.description().equals("LOCKED_DOOR");
    }

    // 获取avatar当前位置的方法
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isGameOver() { return gameOver; }
}
