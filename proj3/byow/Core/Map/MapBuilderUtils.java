package byow.Core.Map;

import byow.Core.Position;
import byow.Core.Rectangle;
import byow.TileEngine.Tileset;

public class MapBuilderUtils {
    /**
     * resets the world map by filling every tile in the map with Tileset.WALL
     * @param map the world map
     */
    public static void resetWorld(WorldMap map) {
        for (int i = 0; i < map.width; i++) {
            for (int j = 0; j < map.height; j++) {
                map.tiles[i][j] = Tileset.WALL;
            }
        }
    }


    /**
     * adds a rectangular room to the world map by filling the area
     * defined by the room rectangle with Tileset.FLOOR.
     * @param map the world map
     * @param room the rectangular room
     */
    public static void applyRectangleRoomToWorld(WorldMap map, Rectangle room) {
        for (int y = room.getY() + 1; y <= room.getY() + room.getH(); y++) {
            for (int x = room.getX() + 1; x <= room.getX() + room.getW(); x++) {
                map.tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    /**
     * adds a circular room to the world map. It calculates the radius
     * based on the dimensions of the room rectangle and fills the area
     * within that radius from the center of the rectangle with Tileset.FLOOR.
     * @param map the world map
     * @param room the circular room
     */
    public static void applyCircleRoomToWorld(WorldMap map, Rectangle room) {
        double radius = Math.min(room.getW(), room.getH()) / 2.0f;
        Position center = room.center();
        for (int y = room.getY(); y <= room.getY() + room.getH(); y++) {
            for (int x = room.getX(); x <= room.getX() + room.getW(); x++) {
                double distance = center.distance(new Position(x, y));
                if (distance <= radius) {
                    map.tiles[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    /**
     * creates a horizontal tunnel on the world map.
     * It sets the tiles between startX and endX at the vertical position y to Tileset.FLOOR.
     * @param map the world map
     * @param startX the start x-axis
     * @param endX the end y-axis
     * @param y the y-axis
     */
    public static void applyHorizontalTunnel(WorldMap map, int startX, int endX, int y) {
        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
            if (x < map.width && x > 0) {
                map.tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    /**
     * creates a vertical tunnel on the world map.
     * It sets the tiles between startY and endY at the horizontal position x to Tileset.FLOOR.
     * @param map the world map
     * @param startY the start y-axis
     * @param endY the end y-axis
     * @param x the x-axis
     */
    public static void applyVerticalTunnel(WorldMap map, int startY, int endY, int x) {
        for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
            if (y < map.height && y > 0) {
                map.tiles[x][y] = Tileset.FLOOR;
            }
        }
    }
}
