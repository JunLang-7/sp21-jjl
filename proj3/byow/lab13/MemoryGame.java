package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        Random rand = new Random(seed);
        this.rand = rand;
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            Character c = CHARACTERS[rand.nextInt(CHARACTERS.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.text((double) width / 2, (double) height / 2, s);
        StdDraw.show();

        if (!gameOver) {
            drawStatus();
        }
    }

    /**
     * draw the status menu
     */
    private void drawStatus() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(5, height - 2, "Round: " + round);
        String mode = playerTurn ? "Type!" : "Watch!";
        StdDraw.text((double) width / 2 - 3, height - 2, mode);
        StdDraw.text(width - 10,  height - 2,
                ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
        StdDraw.show();
    }

    public void flashSequence(String letters)  {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (Character c : letters.toCharArray()) {
            try {
                StdDraw.clear(Color.BLACK);
                StdDraw.show();
                drawFrame(Character.toString(c));
                Thread.sleep(1000);

                StdDraw.clear(Color.BLACK);
                StdDraw.show();
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        StringBuilder sb = new StringBuilder();
        while (sb.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                sb.append(c);
                drawFrame(Character.toString(c));
            }
        }
        return sb.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        playerTurn = false;

        //TODO: Establish Engine loop
        while (!gameOver) {
            try {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
                StdDraw.text((double) width / 2, (double) height / 2, "Round: " + round);
                StdDraw.show();
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String question = generateRandomString(round);
            playerTurn = false;
            flashSequence(question);
            playerTurn = true;
            String answer = solicitNCharsInput(round);
            if (question.equals(answer)) {
                round++;
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                gameOver = true;
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
                StdDraw.text((double) width / 2, (double) height / 2,
                        "Game Over! You made it to round: " + round);
                StdDraw.show();
            }
        }
    }

}
