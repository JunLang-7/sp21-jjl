package byow.Core;

import byow.Core.Map.WorldMap;
import edu.princeton.cs.introcs.StdDraw;

import java.io.*;
import java.nio.file.Paths;

public class MyUtils {
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File saveFile = join(CWD, "savefiles.txt");

    public static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    public static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /* SERIALIZATION UTILITIES */

    static <T extends Serializable> T readObject(File file, Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                 | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /**
     * Returns a byte array containing the serialized contents of OBJ.
     */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            excp.printStackTrace(); // 打印详细的异常信息
            throw new RuntimeException("Internal error serializing", excp);
        }
    }

    /**
     * Write OBJ to FILE.
     */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    static void writeContents(File file, byte[] contents) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(contents);
        } catch (IOException e) {
            e.printStackTrace(); // 打印详细的异常信息
            throw new RuntimeException("Internal error writing to file", e);
        }
    }

    /** ========================
     *  = Engine Util Function =
     * =========================
     */

    /**
     * to get the game start
     * @param input the keyboard input
     */
    public static void getStart(StringBuilder input) {
        boolean started = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (!started) {
                    if (c == 'n' || c == 'N') {
                        started = true;
                        input.append(c);
                    } else if (c == 'l' || c == 'L') {
                        input.append(c);
                        return;
                    } else if (c == 'q' || c == 'Q') {
                        System.exit(0);
                    }
                } else {
                    if (Character.isDigit(c)) {
                        input.append(c);
                    } else if (c == 's' || c == 'S') {
                        input.append(c);
                        break;
                    }
                }
            }
        }
    }

    /**
     * handle the input command to deal with the movement of the avatar and the logic of quit.
     * Some like `Vim`, it should type `:q` to quit, and if type `:` only and type other key would cancel the quit.
     * @param input the input command
     */
    public static void inputCommand(StringBuilder input) {
        boolean quit = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char ch = StdDraw.nextKeyTyped();
                switch (ch) {
                    case 'w', 'W', 's', 'S', 'a', 'A', 'd', 'D' -> {
                        if (quit) {
                            input.deleteCharAt(0);
                        }
                        input.append(ch);
                        return;
                    }
                    case ':' -> {
                        input.append(ch);
                        quit = true;
                    }
                    case 'q', 'Q' -> {
                        if (quit) {
                            input.append(ch);
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Translate the input into a formal form, including all the character is upper case
     * and delete the invalid input, including the front input before 'L' or 'N', and space
     * @param  input the input String
     * @return the fixed input String
     */
    public static String fixInput(String input) {
        // use StringBuffer instead of StringBuilder to fix the bug of output 2 times length of expected String
        StringBuffer sb = new StringBuffer();
        boolean loadFlag = false;
        boolean startFlag = false;
        boolean moveFlag = false;
        char[] in = input.toCharArray();
        for (int i = 0; i < input.length(); i++) {
            switch (in[i]) {
                case 'l', 'L' -> {
                    loadFlag = true;
                    moveFlag = true;
                    sb.append(in[i]);
                }
                case 'n', 'N' -> {
                    if (!loadFlag) {
                        startFlag = true;
                        sb.append(in[i]);
                    }
                }
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    if (startFlag && !loadFlag) {
                        sb.append(in[i]);
                    }
                }
                case 's', 'S' -> {
                    if (startFlag && !loadFlag
                            && (input.length() - input.lastIndexOf('n') > 1
                            || input.length() - input.lastIndexOf('N') > 1)) {
                        sb.append(in[i]);
                        startFlag = false;
                        moveFlag = true;
                    } else if (moveFlag || Engine.gameStarted) {
                        sb.append(in[i]);
                    }
                }
                case 'w', 'W', 'a', 'A', 'd', 'D' -> {
                    if (moveFlag || Engine.gameStarted) {
                        sb.append(in[i]);
                    }
                }
                case ':' -> {
                    if (Engine.gameStarted && in[i + 1] == 'q' || in[i + 1] == 'Q') {
                        sb.append(':');
                        sb.append('Q');
                        return sb.toString().toUpperCase();
                    }
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * serialize the worldMap into a .txt file
     * @param worldMap the worldMap to be serialized
     */
    public static void save(WorldMap worldMap) {
        writeObject(saveFile, worldMap);
    }

    /**
     * load the worldMap from .txt file
     * @return the WorldMap been serialized
     */
    public static WorldMap load(){
        return readObject(saveFile, WorldMap.class);
    }

    public static void main(String[] args) {
        System.out.println(CWD);
    }
}
