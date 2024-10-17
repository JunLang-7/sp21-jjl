package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Utils.*;

/**
 * Represents the object of the file,
 * with a unique name owing to the UID.
 */
public class Blob implements Serializable {
    /** The UID of the Blob with SHA-1. */
    private final String UID;

    /** The File that writes to the blob. */
    private final File file;

    /** The Blob file that saves the Blob. */
    private final File blobFile;

    /** The content of the Blob. */
    private final byte[] content;

    public Blob(File sourceFile) {
        this.file = sourceFile;
        String filePath = file.getPath();
        content = readContents(sourceFile);
        UID = sha1(filePath, content);
        blobFile = join(Repository.BLOBS_DIR, UID);
    }

    /**
     * return the UID of Blob.
     * @return UID
     */
    public String getUID() {
        return UID;
    }

    /**
     * reads in and deserializes a dog from a file with the UID in OBJECTS_FOLDER.
     * @param blobUID the UID of the Blob
     * @return Blob read from the file
     */
    public static Blob fromFile(String blobUID) {
        File blobFile =join(Repository.BLOBS_DIR, blobUID);
        return readObject(blobFile, Blob.class);
    }

    /**
     * Saves a blob to a file for future use.
     */
    public void save() {
        createNewFile(blobFile);
        writeObject(blobFile, this);
    }

    /**
     * Get the Blob content and transform it into String encoding UTF-8.
     * @return the String content
     */
    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }

    /**
     * Return the filePath of the Blob
     * @return
     */
    public String getFilePath() {
        return file.getPath();
    }

    /**
     * Return the content of the Blob
     * @return
     */
    public byte[] getContent() {
        return content;
    }
}
