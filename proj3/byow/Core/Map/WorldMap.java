package byow.Core.Map;

import byow.Core.Position;
import byow.TileEngine.TETile;

import java.io.Serializable;

public class WorldMap implements Serializable {
    private final TETile[][] tiles;
    private final int width;
    private final int height;
    private Position entry;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TETile[width][height];
    }

    public void setEntry(Position entry) {
        this.entry = entry;
    }

    public Position getEntry() {
        return entry;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setTiles(int x, int y, TETile type) {
        tiles[x][y] = type;
    }

    public TETile getTile(int x, int y) {
        return tiles[x][y];
    }

    public TETile[][] getTiles() {
        return tiles;
    }
}
