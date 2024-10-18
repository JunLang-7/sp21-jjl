package gitlet;

// TODO: any imports you need here
import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  with the attribution of message, time, parents and UID.
 *  @author Junlang Jiang
 */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private final String message;

    /** The commit time of this Commit. */
    private final String timestamp;

    /** The parent of this Commit. */
    public final List<String> parents;

    /** The UID of this Commit. */
    private final String UID;

    /** The date of this Commit. */
    private final Date date;

    /** The Map that store the path to blob, the same as StagingArea. */
    private Map<String, String> pathToBlobs;

    /** The UID of blobs that stores in */
    private final List<String> blobs;

    /* TODO: fill in the rest of this class. */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parents = new ArrayList<>();
        this.timestamp = createTimestamp();
        this.UID = createUID();
        this.pathToBlobs = new HashMap<>();
        this.blobs = new ArrayList<>();
    }

    public Commit(String message, Commit parent) {
        this.message = message;
        this.parents = new ArrayList<>();
        this.parents.add(parent.getUID());
        this.date = new Date();
        this.timestamp = createTimestamp();
        this.UID = createUID();
        this.pathToBlobs = parent.pathToBlobs;
        this.blobs = parent.blobs;
    }

    /**
     * Return a unique UID with SHA-1 hashing.
     * @return the UID
     */
    private String createUID() {
        return sha1(timestamp, message, parents.toString(), blobs.toString());
    }

    /**
     * Create the timestamp owing to the date,
     * which is the current date aside from the initial commit is
     * 00:00:00 UTC, Thursday, 1 January 1970
     * @return timestamp
     */
    private String createTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    /**
     * return the message of this Commit.
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * return the timestamp of this Commit.
     * @return the timestamp in format
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Return the UID of the Commit.
     * @return the UID
     */
    public String getUID() {
        return UID;
    }

    /**
     * Get the Commit from file based on its commit UID
     * @param commitUID the commit UID
     * @return the Commit from file
     */
    public static Commit fromFile(String commitUID) {
        return readObject(join(Repository.COMMITS_DIR, commitUID), Commit.class);
    }

    /**
     * Get the parent list of the Commit.
     * @return the list of the parents of the commit
     */
    public List<String> getParents() {
        return parents;
    }

    /**
     * Get the first parent of the Commit.
     * @return the first parent of the commit
     */
    public String getFirstParent() {
        return parents.get(0);
    }

    /**
     * Save the commit to the file with the name UID.
     */
    public void save() {
        File commitFile = join(Repository.COMMITS_DIR, getUID());
        createNewFile(commitFile);
        writeObject(commitFile, this);
    }

    /**
     * Return the pathToBlobs of the Commit.
     * @return the Map of Blobs in the Commit
     */
    public Map<String, String> getPathToBlobs() {
        return pathToBlobs;
    }

    /**
     * Add another parent with the name.
     * @param parent the parent commit UID of the commit
     */
    public void addParent(String parent) {
        parents.add(parent);
    }

    /**
     * Add the blob based on its path and UID to the Commit.
     * @param blobPath the blob path
     * @param blobUID the blob UID
     */
    public void addBlob(String blobPath, String blobUID) {
        pathToBlobs.put(blobPath, blobUID);
        blobs.add(blobPath);
    }

    /**
     * Remove the blob of the filePath
     * @param blobPath the blob path
     */
    public void removeBlob(String blobPath) {
        pathToBlobs.remove(blobPath);
        blobs.remove(pathToBlobs.get(blobPath));
    }

    public void checkPath() {
        Map<String, String> newPathToBlobs = new HashMap<>();
        for (String oldPath : pathToBlobs.keySet()) {
            File oldFile = new File(oldPath);
            String fileName = oldFile.getName();
            File newFile = join(Repository.CWD, fileName);
            newPathToBlobs.put(newFile.getPath(), pathToBlobs.get(oldPath));
        }
        pathToBlobs = newPathToBlobs;
    }
}
