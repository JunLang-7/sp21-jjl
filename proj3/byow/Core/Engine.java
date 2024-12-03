package byow.Core;

import byow.Core.Map.BSPMapBuilder;
import byow.Core.Map.IMapBuilder;
import byow.Core.Map.WorldMap;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.Random;

import static byow.Core.MyUtils.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 60;
    public static final int HEIGHT = 45;
    private static long seed;
    private static Random random;
    private IMapBuilder mapBuilder;
    static boolean gameStarted = false;
    private Avatar avatar;
    private final UserInterface userInterface = new UserInterface();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        random = new Random();
        userInterface.drawMenu();

        while (true) {
            StringBuilder input = new StringBuilder();
            if (!gameStarted) {
                getStart(input);
            } else {
                inputCommand(input);
            }
            TETile[][] tiles = interactWithInputString(input.toString());
            if (tiles == null) {
                return;
            }
            ter.renderFrame(tiles);
            userInterface.drawHUD(tiles);

             // check if the avatar reach the locked door
            if (avatar.isGameOver()) {
                userInterface.drawGameOver();
                return;
            }

            // update HUD even without keyboard input
//            while (!StdDraw.hasNextKeyTyped()) {
//                userInterface.drawHUD(tiles);
//                // pause for a while to less use cpu
//                StdDraw.pause(100);
//            }
        }
    }



    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww"). The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.


        // handle the bad input
        input = fixInput(input);

        if (!gameStarted) {
            if (input.contains("L")) {
                // load
                random = new Random();
                mapBuilder = new BSPMapBuilder(random);
                WorldMap loadedMap = load();
                if (loadedMap != null) {
                    mapBuilder.setWorldMap(loadedMap);
                    avatar = new Avatar(mapBuilder.getWorldMap());
                    input = input.substring(1);
                } else {
                    throw new RuntimeException("Failed to load the game.");
                }
            } else {
                // Create the world randomly based on the seed
                int end = input.indexOf("S");
                seed = Long.parseLong(input.substring(1, end));
                random = new Random(seed);
                mapBuilder = new BSPMapBuilder(random);
                mapBuilder.buildMap();
                input = input.substring(end);
                avatar = new Avatar(mapBuilder.getWorldMap());
            }
            gameStarted = true;
        }

        // move the avatar
        for (char step : input.toCharArray()) {
            avatar.move(step);
        }

        return mapBuilder.getWorldMap().tiles;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        Engine engine = new Engine();
        TETile[][] world = engine.interactWithInputString("Lw");

        ter.renderFrame(world);
    }
}