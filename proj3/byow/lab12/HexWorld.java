package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static class Position {
        public int x;
        public int y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * add a hexagon to the tile world
     * @param tiles the tiles World
     * @param side the side length of the hexagon
     * @param seed the random seed
     * @param p the position of the center
     */
    public static void addHexgon(TETile[][] tiles, int side, long seed, Position p) {
        TETile randomTileType = randomTile();
        // draw the upper part
        int cnt = side;
        for (int i = 0; i < side; i++) {
            drawLine(tiles,side + 2 * i, randomTileType, p.x, p.y + cnt);
            cnt--;
        }
        // draw the lower part
        cnt = 0;
        for (int i = side-1; i >= 0; i--) {
            drawLine(tiles,side + 2 * i, randomTileType, p.x, p.y - cnt);
            cnt++;
        }
    }

    /**
     * Picks a RANDOM tile in the with the rand number
     * @return the Tileset type
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        switch (tileNum) {
            case 0: return Tileset.FLOWER;
            case 1: return Tileset.GRASS;
            case 2: return Tileset.MOUNTAIN;
            case 3: return Tileset.SAND;
            default: return Tileset.NOTHING;
        }
    }

    /**
     * Decompose the hexagon into line and draw the line
     * @param tiles the tiles World
     * @param line the line
     * @param type the random Tileset type
     * @param posX the middle x position
     * @param posY the middle y position
     */
    private static void drawLine(TETile[][] tiles, int line, TETile type, int posX, int posY) {
        for (int x = posX - line / 2; x <= posX + line / 2; x++) {
            tiles[x][posY] = type;
        }
    }

    /**
     * get the next right position of the Hexagon
     * @param pre the pre position
     * @param side the side of the hexagon
     * @return
     */
    private static Position nextRight(Position pre, int side) {
        return new Position(pre.x + 2 * side - 1, pre.y + side);
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        Position p = new Position(centerX, centerY);
        addHexgon(tiles, 3, SEED, p);
        p = nextRight(p, 3);
        addHexgon(tiles, 3, SEED, p);

        ter.renderFrame(tiles);
    }
}
