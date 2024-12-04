package byow.Core;

import byow.TileEngine.TETile;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestWorld {
    @Test
    public void testA() {
        Engine e1 = new Engine();
        Engine e2 = new Engine();
        TETile[][] world1 = e1.interactWithInputString("n8772076153521736045sawsasdsadwwwwsa");
        TETile[][] world2 = e2.interactWithInputString("n8772076153521736045sawsasdsadwwwwsa");
        assertArrayEquals(world1, world2);
    }

    @Test
    public void testB() {
        Engine e1 = new Engine();
        Engine e2 = new Engine();
        TETile[][] world1 = e1.interactWithInputString("n8772076153521736045sawsasdsadwwwwsa");
        TETile[][] world2 = e2.interactWithInputString("n7313251667695476404sasdw");
        assertNotEquals(world1, world2);
    }

    @Test
    public void testC() {
        Engine e1 = new Engine();
        Engine e2 = new Engine();
        TETile[][] world1 = e1.interactWithInputString("n6547766204324870169ssdswa:q");
        TETile[][] world2 = e2.interactWithInputString("l");
        assertArrayEquals(world1, world2);
    }
}
