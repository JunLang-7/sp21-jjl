package byow.Core.Map;

import byow.Core.Rectangle;
import byow.TileEngine.Tileset;
import byow.Core.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class BSPMapBuilder implements IMapBuilder {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;

    private final WorldMap worldMap;
    private final Random random;

    private final List<Rectangle> rooms = new ArrayList<>();
    private final List<Rectangle> rects = new ArrayList<>();

    public BSPMapBuilder(Random random) {
        this.random = random;
        this.worldMap = new WorldMap(WIDTH, HEIGHT);
    }

    /**
     * divides a given rectangle (rect) into four smaller sub-rectangles and adds them to the rects list.
     * @param rect the rectangle
     */
    private void addSubRects(Rectangle rect) {
        int halfWidth = Math.max(rect.getW() / 2, 1);
        int halfHeight = Math.max(rect.getH() / 2, 1);

        rects.add(new Rectangle(rect.getX(), rect.getY(), halfWidth, halfHeight));
        rects.add(new Rectangle(rect.getX() + halfWidth, rect.getY(), halfWidth, halfHeight));
        rects.add(new Rectangle(rect.getX(), rect.getY() + halfHeight, halfWidth, halfHeight));
        rects.add(new Rectangle(rect.getX() + halfWidth, rect.getY() + halfHeight, halfWidth, halfHeight));
    }

    /**
     * returns a random rectangle from the rects list. If there's only one rectangle, it returns that one.
     * @return a random rectangle from the rects list
     */
    private Rectangle getRandomRectangle() {
        if (rects.size() == 1) {
            return rects.get(0);
        }
        return rects.get(random.nextInt(rects.size()));
    }

    /**
     * generates a smaller sub-rectangle within the given rectangle (rect).
     * The dimensions of the sub-rectangle are randomly determined.
     * @param rect the given rectangle
     * @return a sub-rectangle within the given rectangle
     */
    private Rectangle getRandomSubRectangle(Rectangle rect) {

        int w = Math.max(3, random.nextInt(Math.min(rect.getW(), 10))) + 1;
        int h = Math.max(3, random.nextInt(Math.min(rect.getH(), 10))) + 1;

        int x = rect.getX() + random.nextInt(6);
        int y = rect.getY() + random.nextInt(6);

        return new Rectangle(x, y, w, h);
    }

    /**
     *  checks whether a given rectangle (rect) can be placed on the map
     *  without overlapping other rooms and within map boundaries.
     * @param rect the given rectangle
     * @return checks whether a given rectangle can be placed on the map
     */
    private boolean isPossible(Rectangle rect) {
        Rectangle expend = new Rectangle(rect.getX()-2, rect.getY()-2, rect.getW()+4, rect.getH()+4);

        for (Rectangle room : rooms) {
            if (room.isOverlapped(rect)) {
                return false;
            }
        }

        for (int y = expend.getY(); y <= expend.getY() + expend.getH(); y++) {
            for (int x = expend.getX(); x <= expend.getX() + expend.getW(); x++) {
                if (x > worldMap.width - 2) {
                    return false;
                } else if (y > worldMap.height - 2) {
                    return false;
                } else if (x < 1) {
                    return false;
                }else if (y < 1) {
                    return false;
                }
                if (!worldMap.tiles[x][y].equals(Tileset.WALL)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * constructs rooms on the map. It starts with a large rectangle,
     * subdivides it, and attempts to place rooms if they can be placed validly.
     * It also connects rooms using corridors.
     */
    private void buildRooms() {
        rects.clear();

        rects.add(new Rectangle(2, 2, worldMap.width - 7, worldMap.height - 7));
        addSubRects(rects.get(0));

        int nRooms = 0;
        while (nRooms < 240) {
            Rectangle rect = getRandomRectangle();
            Rectangle candidate = getRandomSubRectangle(rect);
            if (isPossible(candidate)) {
                if (random.nextBoolean()) {
                    MapBuilderUtils.applyRectangleRoomToWorld(worldMap, candidate);
                } else {
                    MapBuilderUtils.applyCircleRoomToWorld(worldMap, candidate);
                }
                rooms.add(candidate);
                addSubRects(rect);
            }
            nRooms += 1;
        }

        rooms.sort(Comparator.comparing(Rectangle::getX));

        for (int i = 0; i < rooms.size() - 1; i++) {
            Rectangle room = rooms.get(i);
            Rectangle nextRoom = rooms.get(i + 1);
            int startX = room.getX() + random.nextInt(room.getW());
            int startY = room.getY() + random.nextInt(room.getH());
            int endX = nextRoom.getX() + random.nextInt(nextRoom.getW());
            int endY = nextRoom.getY() + random.nextInt(nextRoom.getH());
            drawCorridor(startX, startY, endX, endY);
        }
    }

    /**
     * draws a corridor between two points (x1, y1) and (x2, y2)
     * by setting the tiles between the points to Tileset.FLOOR.
     * @param startX the x1
     * @param startY the y1
     * @param endX the x2
     * @param endY the y2
     */
    private void drawCorridor(int startX, int startY, int endX, int endY) {
        int x = startX, y = startY;

        while (x != endX || y != endY) {
            if (x < endX) {
                x += 1;
            } else if (x > endX) {
                x -= 1;
            }
            if (y < endY) {
                y += 1;
            } else if (y > endY) {
                y -= 1;
            }
            worldMap.tiles[x][y] = Tileset.FLOOR;
        }
    }

    /**
     * builds the entire map by resetting the world map, building rooms,
     * and placing special tiles (TREE) at the starting and ending points.
     */
    @Override
    public void buildMap() {
        MapBuilderUtils.resetWorld(worldMap);
        buildRooms();

        Position start = rooms.get(0).center();
        worldMap.tiles[start.x][start.y] = Tileset.TREE;

        Position stairs = rooms.get(rooms.size() - 1).center();
        worldMap.tiles[stairs.x][stairs.y] = Tileset.TREE;
    }

    /**
     * returns the current state of the world map
     * @return the current state of the world map
     */
    @Override
    public WorldMap getWorldMap() {
        return worldMap;
    }
}
