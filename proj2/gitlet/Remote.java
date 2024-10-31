package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.getRemoteDir;
import static gitlet.Utils.createNewFile;
import static gitlet.Utils.join;
import static gitlet.Utils.writeObject;

/**
 * The remote class. Stores the remote directory with the name.
 * @author Junlang-7
 */
public class Remote implements Serializable {
    private final String remoteName;
    private final String path;

    Remote(String remoteName, String path) {
        this.remoteName = remoteName;
        this.path = path;
    }

    /**
     * Get the remote name.
     * @return the remote name.
     */
    public String getRemoteName() {
        return remoteName;
    }

    /**
     * Get the remote path.
     * @return the remote path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Save the remote to the remote directory.
     */
    public void save() {
        File file = join(getRemoteDir(), remoteName);
        createNewFile(file);
        writeObject(file, this);
    }

    /**
     * Check if the remote exists.
     */
    public boolean exists() {
        File file = join(getRemoteDir(), remoteName);
        return file.exists();
    }

    public static Remote fromFile(String remoteName) {
        File remoteFile = join(getRemoteDir(), remoteName);
        return Utils.readObject(remoteFile, Remote.class);
    }
}
