package byow.Core.Map;

import byow.Core.Position;
import byow.TileEngine.TETile;

import java.io.Serializable;

public class WorldMap implements Serializable {
    public final TETile[][] tiles;
    public final int width;
    public final int height;
    public Position entry;

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

    public void setTiles(Position tile, TETile type) {
        tiles[tile.x][tile.y] = type;
    }
}
