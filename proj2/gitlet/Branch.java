package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;


public class Branch implements Serializable {
    /** The branch Name */
    private String branchName;

    /** The UID of current Commit */
    private String commitPointer;

    Branch(Commit commit) {
        branchName = "master";
        commitPointer = commit.getUID();
    }

    Branch(String branchName, Commit commit) {
        this.branchName = branchName;
        commitPointer = commit.getUID();
    }

    /**
     * save the Branch to file.
     */
    public void save() {
        File file = join(Repository.BRANCH_DIR, branchName);
        createNewFile(file);
        writeObject(file, this);
    }

    /**
     * Return the current Commit ID.
     * @return
     */
    public String getCommitPointer() {
        return commitPointer;
    }

    /**
     * set the commitPointer the to Commit
     * @param commit
     */
    public void setCommitPointer(Commit commit) {
        commitPointer = commit.getUID();
    }

    public static File fromFile(String branchName) {
        return join(Repository.BRANCH_DIR, branchName);
    }
}
