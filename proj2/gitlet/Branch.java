package gitlet;

import java.io.File;
import java.io.Serializable;
import static gitlet.Utils.*;

/**
 * Represents the branch of the repository,
 * with the branch name and the commit pointer
 */
public class Branch implements Serializable {
    /** The branch Name */
    private final String branchName;

    /** The UID of Commit that the branch points to */
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
     * Return the commit UID of the branch
     * @return the commit UID of the branch
     */
    public String getCommitPointer() {
        return commitPointer;
    }

    /**
     * set the commitPointer the to Commit
     * @param commit the Commit
     */
    public void setCommitPointer(Commit commit) {
        commitPointer = commit.getUID();
    }

    public static File fromFile(String branchName) {
        return join(Repository.BRANCH_DIR, branchName);
    }
}
