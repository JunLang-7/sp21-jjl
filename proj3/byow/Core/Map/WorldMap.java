package byow.Core.Map;

import byow.TileEngine.TETile;

public class WorldMap {
    public final TETile[][] tiles;
    public final int width;
    public final int height;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TETile[width][height];

    }
}
