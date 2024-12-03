package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class UserInterface {
    private final double w = Engine.WIDTH / 2.0;
    private final double h = Engine.HEIGHT / 2.0;

    public void drawMenu() {
        Font font = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(w, h + 5, "BYoW");

        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(w, h - 1, "NEW GAME(N)");
        StdDraw.text(w, h - 4, "LOAD(L)");
        StdDraw.text(w, h - 7, "QUIT(Q)");
        StdDraw.show();
    }

    /**
     * draw the Head Up Display(HUD)
     * @param world the tiles to describe
     */
    public void drawHUD(TETile[][] world) {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX >= 0 && mouseX < Engine.WIDTH && mouseY >= 0 && mouseY < Engine.HEIGHT) {
            TETile tile = world[mouseX][mouseY];
            String description = tile.description();

            // clear the previous words on HUD
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.filledRectangle(5, Engine.HEIGHT - 1, Engine.WIDTH, 1);

            // draw new word on HUD
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textLeft(1, Engine.HEIGHT - 1, description);
            StdDraw.show();
        }
    }

    /**
     * draw the Game over menu
     */
    public void drawGameOver() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 60));
        StdDraw.text(w, h, "You Win!");
        StdDraw.show();
    }
}
