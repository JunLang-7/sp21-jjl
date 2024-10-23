package gitlet;

import java.io.Serializable;
import java.util.*;
import java.io.File;

import static gitlet.Utils.*;
import static gitlet.Repository.getStageDir;

/**
 * Represents the staging area of Gitlet,
 * including the staging addition area and
 * the staging removal area.
  */
public class StagingArea implements Serializable {
    /** The Map contains the filePath as key and the Blob UID as value. */
    private final Map<String, String> pathToBlobs;

    /** The stagingArea name, including the addition and the removal. */
    private final String stageName;

    StagingArea(String stageName) {
        this.stageName = stageName;
        pathToBlobs = new HashMap<>();
    }

    /**
     * add the Blob to the additionArea.
     * @param blob blob to add
     */
    public void addBlob(Blob blob) {
        String filePath = blob.getFilePath();
        String blobUID = blob.getUID();
        pathToBlobs.put(filePath, blobUID);
        save();
    }

    /**
     * add the Blob to the additionArea
     * @param filePath the file path
     * @param blobUID the UID of the blob
     */
    public void addBlob(String filePath, String blobUID) {
        pathToBlobs.put(filePath, blobUID);
        save();
    }

    /**
     * remove the Blob from the removalArea
     * @param filePath the file path
     */
    public void removeBlob(String filePath) {
        pathToBlobs.remove(filePath);
        save();
    }

    /**
     * Return the Map of Blobs
     * @return the Map of Blobs
     */
    public Map<String, String> getPathToBlobs() {
        return pathToBlobs;
    }

    public boolean isEmpty() {
        return pathToBlobs.isEmpty();
    }

    /**
     * Clear the staging area.
     */
    public void clear() {
        pathToBlobs.clear();
        save();
    }

    /**
     * Save this instance to the file addition or removal
     */
    public void save() {
        File file = join(getStageDir(), stageName);
        createNewFile(file);
        writeObject(file, this);
    }

    /**
     * print out all the files in the Map
     */
    public void print() {
        for (String filePath: pathToBlobs.keySet()) {
            File file = join(filePath);
            System.out.println(file.getName());
        }
    }

    /**
     * Judge if the file is in the Map.
     * @param filePath the file path
     * @return if the file is in the Blob Map
     */
    public Boolean containsFile(String filePath) {
        return this.pathToBlobs.containsKey(filePath);
    }
}
