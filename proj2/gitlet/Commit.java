package gitlet;

// TODO: any imports you need here
import static gitlet.Utils.*;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Junlang Jiang
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

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
    private final Map<String, String> pathToBlobs;

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
        return sha1(timestamp, message, parents.toString());
    }

    /**
     * Create the timestamp owing to the date,
     * which is the current date aside from the initial commit is
     * 00:00:00 UTC, Thursday, 1 January 1970
     * @return timestamp
     */
    private String createTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss 'UTC', EEEE, dd MMMM yyyy", Locale.ENGLISH);
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
     * @return
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Return the UID of the Commit.
     * @return
     */
    public String getUID() {
        return UID;
    }

    /**
     * Return the Date of the Commit.
     * @return
     */
    public Date getDate() {
        return date;
    }

    public static Commit fromFile(String id) {
        return readObject(join(Repository.COMMITS_DIR, id), Commit.class);
    }

    /**
     * Get the parent list of the Commit.
     * @return
     */
    public List<String> getParent() {
        return parents;
    }

    /**
     * Get the first parent of the Commit.
     * @return
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
     * @return
     */
    public Map<String, String> getPathToBlobs() {
        return pathToBlobs;
    }

    /**
     * Add another parent with the name.
     * @param parent
     */
    public void addParent(String parent) {
        parents.add(parent);
    }

    /**
     * Add the blob based on its path and UID to the Commit.
     * @param blobPath
     * @param blobUID
     */
    public void addBlob(String blobPath, String blobUID) {
        pathToBlobs.put(blobPath, blobUID);
        blobs.add(blobPath);
    }

    /**u
     * Remove the blob of the filePath
     * @param blobPath
     */
    public void removeBlob(String blobPath) {
        pathToBlobs.remove(blobPath);
        blobs.remove(pathToBlobs.get(blobPath));
    }

    /**
     * check if the file is modified or removed.
     * @param fileName the file name
     * @return return true if it's modified
     */
    public boolean checkUntracked(String fileName) {
        File file = join(Repository.CWD, fileName);
        // check if it's removed,
        if (!file.exists()) {
            return true;
        }
        // check if it's modified.
        String filePathCWD = file.getAbsolutePath();
        Blob blobCWD = new Blob(file);
        for (String filePath : pathToBlobs.keySet()) {
            if (filePath.equals(filePathCWD)) {
                return blobCWD.getUID().equals(pathToBlobs.get(filePath));
            }
        }
        return false;
    }
}
