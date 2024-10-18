package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Junliang-7
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The object directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    /** The blob directory. */
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");

    /** The commit directory. */
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");

    /** The stagingArea directory. */
    public static final File STAGE_DIR = join(GITLET_DIR, "stages");

    /** The branch directory. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branches");

    /** The HEAD pointer. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");

    /** The current branch. */
    private static final File CURRENT_BRANCH = join(GITLET_DIR, "BRANCH");

    /* TODO: fill in the rest of this class. */

    /** Creates a new Gitlet version-control system in the current directory.
     * This system will automatically start with one commit: a commit that contains no files
     * and has the commit message initial commit (just like that, with no punctuation).
     * It will have a single branch: master, which initially points to this initial commit,
     * and master will be the current branch. The timestamp for this initial commit will be
     * 00:00:00 UTC, Thursday, 1 January 1970 in whatever format you choose for dates (this
     * is called “The (Unix) Epoch”, represented internally by the time 0.) Since the initial
     * commit in all repositories created by Gitlet will have exactly the same content,
     * it follows that all repositories will automatically share this commit (they will all
     * have the same UID) and all commits in all repositories will trace back to it.*/
    public void init() {
        if (isInit()) {
            message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        // Create the required directory.
        initMkdir();

        // Create the initial Commit.
        Commit commit = new Commit();
        commit.save();

        // set the HEAD and branch.
        updateHead(commit.getUID());
        initStagingArea();
        initMaster(commit);
    }

    /**Adds a copy of the file as it currently exists to the staging area (see the description
     * of the commit command). For this reason, adding a file is also called staging the file
     * for addition. Staging an already-staged file overwrites the previous entry in the staging
     * area with the new contents. The staging area should be somewhere in .gitlet. If the
     * current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already
     * there (as can happen when a file is changed, added, and then changed back to its original
     * version). The file will no longer be staged for removal (see gitlet rm), if it was at
     * the time of the command.*/
    public void add(String fileName) {
        // if not init, print the error message
        if (!isInit()) {
            printNotInitAndExit();
        }
        File fileToAdd = getFileFromCWD(fileName);

        if (readRemovalArea().getPathToBlobs().containsKey(fileName)) {
            readRemovalArea().getPathToBlobs().remove(fileToAdd.getPath());
            return;
        }

        // double check to ensure that the file can undoubtedly add to the AdditionArea
        checkFileExists(fileToAdd);
        checkIdentical(fileToAdd);

        // add to the AdditionArea
        Blob blob = new Blob(fileToAdd);
        blob.save();
        StagingArea additionArea = readAdditionArea();
        additionArea.addBlob(blob);
    }

    /**Description: Saves a snapshot of tracked files in the current commit
     * and staging area so they can be restored at a later time, creating a new commit.
     * The commit is said to be tracking the saved files. By default, each commit’s
     * snapshot of files will be exactly the same as its parent commit’s snapshot of files;
     * it will keep versions of files exactly as they are, and not update them.
     * A commit will only update the contents of files it is tracking that have been staged
     * for addition at the time of commit, in which case the commit will now include the
     * version of the file that was staged instead of the version it got from its parent.
     * A commit will save and start tracking any files that were staged for addition but weren’t
     * tracked by its parent. Finally, files tracked in the current commit may be untracked
     * in the new commit as a result being staged for removal by the rm command (below).*/
    public void commit(String commitMessage) {
        // if not init, print the error message
        if (!isInit()) {
            printNotInitAndExit();
        }

        // check if the staging area is empty
        if (readAdditionArea().isEmpty() && readRemovalArea().isEmpty()) {
            message("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = new Commit(commitMessage, readHEAD());

        commitHelper(commit);

        commit.save();

        // update Head and the current branch
        updateHead(commit.getUID());
        updateBranch(commit);

        // Clear the two StagingArea, including the AdditionArea and the RemovalArea.
        clearStagingArea();
    }

    /**
     * Clear the two StagingArea, including the AdditionArea and the RemovalArea.
     */
    private void clearStagingArea() {
        readAdditionArea().clear();
        readRemovalArea().clear();
    }

    /**
     * add all the files in staged area and remove all the files in removal area
     * @param commit the Commit needed to be commited
     */
    private void commitHelper(Commit commit) {
        Map<String, String> addBlobs = readAdditionArea().getPathToBlobs();
        Map<String, String> removeBlobs = readRemovalArea().getPathToBlobs();
        Map<String, String> commitBlobs = commit.getPathToBlobs();

        // two list to store the toAdd and toRemove blobs in the new Commit
        List<String> toAdd = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();

        // Check for addition blobs
        for (String addBlobPath : addBlobs.keySet()) {
            // Check if the toAddBlob is in the pre Commit
            if (!commitBlobs.containsKey(addBlobPath)) {
                commit.addBlob(addBlobPath, addBlobs.get(addBlobPath));
            }
            // Check if the toAddBlob is identical to the one in pre Commit
            for (String blobPath : commitBlobs.keySet()) {
                if (blobPath.equals(addBlobPath) &&
                        !commitBlobs.get(blobPath).equals(addBlobs.get(addBlobPath))) {
                    // add all the modified blobs and remove the old ones
                    toRemove.add(blobPath);
                    toAdd.add(blobPath);
                }
            }
        }

        // Check for removal blobs
        for (String removeBlobPath : removeBlobs.keySet()) {
            for (String blobPath : commitBlobs.keySet()) {
                if (blobPath.equals(removeBlobPath)) {
                    toRemove.add(removeBlobPath);
                }
            }
        }

        // add all the new or modified files to the Commit
        for (String addBlobPath : toAdd) {
            commit.addBlob(addBlobPath, addBlobs.get(addBlobPath));
        }

        // remove all the rm or the old-versioned modified files from the Commit
        for (String removeBlobPath : toRemove) {
            commit.removeBlob(removeBlobPath);
        }

    }

    /** Unstage the file if it is currently staged for addition. If the file is tracked
     * in the current commit, stage it for removal and remove the file from the working directory
     * if the user has not already done so (do not remove it unless it is tracked in the current commit).*/
    public void rm(String fileName) {
        // if not init, print the error message
        if (!isInit()) {
            printNotInitAndExit();
        }

        // check if the file exists.
        checkRemove(fileName);

        File fileToRemove = getFileFromCWD(fileName);
        String filePath = fileToRemove.getPath();

        // if it is in the AdditionArea, just simply remove it from the AdditionArea
        if (readAdditionArea().getPathToBlobs().containsKey(filePath)) {
            readAdditionArea().removeBlob(filePath);
            return;
        }

        // if it is in the current Commit, remove it from the Commit, and check if it exists in the use_dir
        Map<String, String> currentCommitBlobs = readHEAD().getPathToBlobs();
        if (currentCommitBlobs.containsKey(filePath)) {
            String id = currentCommitBlobs.get(filePath);
            readRemovalArea().addBlob(filePath, id);
            readHEAD().removeBlob(filePath);

            if (fileToRemove.exists()) {
                restrictedDelete(fileToRemove);
            }
        }
    }


    /**Starting at the current head commit, display information about each commit backwards
     * along the commit tree until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits. (In regular Git, this is what you get
     * with git log --first-parent). This set of commit nodes is called the commit’s history.
     * For every node in this history, the information it should display is the commit id,
     * the time the commit was made, and the commit message. */
    public void log() {
        if (!isInit()) {
            printNotInitAndExit();
        }

        // get the head Commit
        Commit currentCommit = readHEAD();

        // iterate through the commit history
        while (!currentCommit.getParents().isEmpty()) {
            // print out the log
            printCommit(currentCommit);

            File next = join(COMMITS_DIR, currentCommit.getFirstParent());
            currentCommit = readObject(next, Commit.class);
        }
        printCommit(currentCommit);
    }

    /** Like log, except displays information about all commits ever made.
     * The order of the commits does not matter. Hint: there is a useful method
     * in gitlet.Utils that will help you iterate over files within a directory.*/
    public void global_log() {
        if (!isInit()) {
            printNotInitAndExit();
        }

        // get all the commit in the COMMIT_DIR
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);

        if (commitList == null || commitList.isEmpty()) {
            System.exit(0);
        }

        for (String commitID : commitList) {
            Commit commit = Commit.fromFile(commitID);
            printCommit(commit);
        }
    }

    /**
     * Print out the specified Commit information, including the commit id, Date and commit message.
     * e.g.
     * ===
     * commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
     * Date: Thu Nov 9 20:00:05 2017 -0800
     * A commit message.
     * @param commit the specified commit
     */
    private void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getUID());
        if (commit.getParents().size() > 1) {
            System.out.println("Merge: " + commit.getParents().get(0).substring(0, 7)
                    + " " + commit.getParents().get(1).substring(0, 7));
        }
        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println();
    }

    /** Prints out the ids of all commits that have the given commit message,
     *  one per line. If there are multiple such commits, it prints the ids out on separate lines.
     *  The commit message is a single operand; to indicate a multiword message,
     *  put the operand in quotation marks, as for the commit command below
     * @param message the commit message
     *  */
    public void find(String message) {
        // get all the commit in the COMMIT_DIR
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);

        if (commitList == null || commitList.isEmpty()) {
            message("Found no commit with that message.");
            System.exit(0);
        }

        // add a flag to detect whether is matched
        boolean found = false;

        for (String commitID : commitList) {
            Commit commit = Commit.fromFile(commitID);
            if (commit.getMessage().equals(message)) {
                printCommit(commit);
                found = true;
            }
        }

        if (!found) {
            message("Found no commit with that message.");
        }
    }

    /** Displays what branches currently exist, and marks the current branch with a *.
     *  Also displays what files have been staged for addition or removal.
     */
    public void status() {
        if (!isInit()) {
            printNotInitAndExit();
        }
        printBranches();
        printStageFiles();
        printRemovedFiles();
        printNotStaged();
        printUntracked();
    }

    /**
     * Print out all the untracked files.
     */
    private void printUntracked() {
        System.out.println("=== Untracked Files ===");
        List<String> files = plainFilenamesIn(CWD);
        if (files != null) {
            for (String fileName : files) {
                File file = getFileFromCWD(fileName);
                String filePath = file.getPath();
                if (!readHEAD().getPathToBlobs().containsKey(filePath) &&
                !readAdditionArea().getPathToBlobs().containsKey(filePath) &&
                !readRemovalArea().getPathToBlobs().containsKey(filePath)) {
                    System.out.println(fileName);
                }
            }
        }
        System.out.println();
    }

    /**
     * Print out all the modified but not staged files
     */
    private void printNotStaged() {
        System.out.println("=== Modifications Not Staged For Commit ===");

        // check for the CWD
        Map<String, String> currentFiles = readHEAD().getPathToBlobs();
        for (String filePath : currentFiles.keySet()) {
            // check if deleted
            File file = getFileFromCWD(filePath);
            if (!file.exists()) {
                if (!readRemovalArea().containsFile(filePath)) {
                    System.out.println(file.getName() + " (deleted)");
                }
                continue;
            }
            // check if modified
            Blob blob = new Blob(file);
            if (!blob.getUID().equals(currentFiles.get(filePath))) {
                if (!readAdditionArea().containsFile(filePath)) {
                    System.out.println(file.getName() + " (modified)");
                }
            }
        }

        // check for staging area
        for (String filePath : readAdditionArea().getPathToBlobs().keySet()) {
            File file = getFileFromCWD(filePath);
            if (!file.exists()) {
                System.out.println(file.getName() + " (deleted)");
                continue;
            }
            Blob blob = new Blob(file);
            if (!blob.getUID().equals(readAdditionArea().getPathToBlobs().get(filePath))) {
                System.out.println(file.getName() + " (modified)");
            }
        }

        System.out.println();
    }

    /**
     * Print out all the files in removal area.
     */
    private void printRemovedFiles() {
        System.out.println("=== Removed Files ===");
        readRemovalArea().print();
        System.out.println();
    }

    /**
     * Print out all the files in addition area.
     */
    private void printStageFiles() {
        System.out.println("=== Staged Files ===");
        readAdditionArea().print();
        System.out.println();
    }

    /**
     * Print out all the branches, and * the current branch at the front.
     */
    private void printBranches() {
        System.out.println("=== Branches ===");
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        assert branches != null;
        for (String branch : branches) {
            if (readContentsAsString(CURRENT_BRANCH).equals(branch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();
    }

    /**
     * Initialize the two StagingArea, including the addition and the removal
     */
    private void initStagingArea() {
        StagingArea additionArea = new StagingArea("addition");
        StagingArea removalArea = new StagingArea("removal");
        additionArea.save();
        removalArea.save();
    }

    /**
     * Initialize the master branch.
     */
    private void initMaster(Commit currentCommit) {
        Branch master = new Branch(currentCommit);
        master.save();
        createNewFile(CURRENT_BRANCH);
        writeContents(CURRENT_BRANCH, "master");
    }

    /**
     * Return the additionArea.
     */
    private static StagingArea readAdditionArea() {
        File addStageFile = join(STAGE_DIR, "addition");
        return readObject(addStageFile, StagingArea.class);
    }

    /**
     * Return the removalArea.
     */
    private static StagingArea readRemovalArea() {
        File removeStageFile = join(STAGE_DIR, "removal");
        return readObject(removeStageFile, StagingArea.class);
    }

    /**
     * Check if the file exist. if the file does not exist at all, print an error message
     * @param file the file
     */
    private void checkFileExists(File file) {
        if (!file.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
    }

    /**
     * if the file is identical as the previous one, terminate the program.
     * @param file the file
     */
    private void checkIdentical(File file) {
        if (readHEAD().getPathToBlobs().containsKey(file.getPath())) {
            Blob fileBlob = new Blob(file);
            if (fileBlob.getUID().equals(readHEAD().getPathToBlobs().get(file.getPath()))) {
                System.exit(0);
            }
        }
    }

    private void checkRemove(String fileName) {
        Map<String, String> addBlobs = readAdditionArea().getPathToBlobs();
        Map<String, String> currentCommitBlobs = readHEAD().getPathToBlobs();
        String filePath = getFileFromCWD(fileName).getPath();
        if (!addBlobs.containsKey(filePath) && !currentCommitBlobs.containsKey(filePath)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /**
     * deserialize the HEAD as Commit
     * @return the head Commit
     */
    private static Commit readHEAD() {
        File head = join(COMMITS_DIR, readContentsAsString(HEAD));
        return readObject(head, Commit.class);
    }

    /**
     * return the file of the file name in the CWD.
     * @param fileName the filename in the CWD
     * @return the required file
     */
    public static File getFileFromCWD(String fileName) {
        return Paths.get(fileName).isAbsolute() ? new File(fileName) : join(CWD, fileName);
    }

    /**
     * Create the required directory.
     */
    private void initMkdir() {
        /*the current working directory.
         *
         * <pre>
         * .gitlet
         * ├── HEAD
         * └── objects
         *     └── blobs
         *     └── commits
         *     └── trees
         *     └── staging
         * └── refs
         * └── logs
         *     └── heads
         * </pre>
         */
        GITLET_DIR.mkdirs();
        OBJECTS_DIR.mkdirs();
        BLOBS_DIR.mkdirs();
        COMMITS_DIR.mkdirs();
        STAGE_DIR.mkdirs();
        BRANCH_DIR.mkdirs();
    }

    /**
     * Set the HEAD to the current UID
     * @param current the current UID
     */
    private void updateHead(String current) {
        createNewFile(HEAD);
        writeContents(HEAD, current);
    }

    /**
     * Set the branch point to the Commit
     * @param commit the commit
     */
    private void updateBranch(Commit commit) {
        File branchFile = join(BRANCH_DIR, readContentsAsString(CURRENT_BRANCH));
        Branch branch = readObject(branchFile, Branch.class);
        branch.setCommitPointer(commit);
        branch.save();
    }

    /**
     * Return the repository is init or not.
     * @return whether is init
     */
    private boolean isInit() {
        return GITLET_DIR.exists();
    }

    /**
     * Print out the error that there is no init repository in the directory and exit the program.
     */
    private void printNotInitAndExit() {
        System.out.println("Not in an initialized Gitlet directory.");
        System.exit(0);
    }

    /**
     * Takes the version of the file as it exists in the head commit
     * and puts it in the working directory, overwriting the version
     * of the file that’s already there if there is one. The new version
     * of the file is not staged.
     * @param fileName the fileName to check out
     * */
    public void checkout(String fileName) {
        if (!isInit()) {
            printNotInitAndExit();
        }
        checkoutFile(readHEAD(), fileName);
    }

    /**
     * Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. The new version of the file is not staged.
     * @param commitID the commit UID to check out back
     * @param fileName the fileName to check out
     * */
    public void checkout(String commitID, String fileName) {
        if (!isInit()) {
            printNotInitAndExit();
        }

        Commit commit = getCommitFromID(commitID);
        checkoutFile(commit, fileName);
    }

    private Commit getCommitFromID(String commitID) {
        File commitFile;
        if (commitID.length() < UID_LENGTH) {
            commitFile = shortIDCommit(commitID);
        }
        else {
            commitFile = join(COMMITS_DIR, commitID);
        }

        // if the commit id doesn't exist
        assert commitFile != null;
        if (!commitFile.exists()) {
            message("No commit with that id exists.");
            System.exit(0);
        }
        return readObject(commitFile, Commit.class);
    }

    /**
     * Check out the file from the Commit using its file name
     * @param commit the commit
     * @param fileName the file name
     */
    private void checkoutFile(Commit commit, String fileName) {
        File file = join(CWD, fileName);
        String filePath = file.getPath();
        if (!commit.getPathToBlobs().containsKey(filePath)) {
            message("File does not exist in that commit.");
            System.exit(0);
        }
        String blobUID = commit.getPathToBlobs().get(filePath);
        Blob blob = Blob.fromFile(blobUID);
        if (file.exists()) {
            file.delete();
        }
        createNewFile(file);
        writeContents(file, blob.getContent());
    }

    /**
     * Return the Commit File with the short Commit ID.
     * @param commitID the short commit ID
     * @return the correlate commit file
     */
    private static File shortIDCommit(String commitID) {
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        int length = commitID.length();
        assert commitList != null;
        for (String commit : commitList) {
            if (commit.substring(0, length).equals(commitID)) {
                return join(COMMITS_DIR, commit);
            }
        }
        return null;
    }


    /**
     * Takes all files in the commit at the head of the given branch, and puts them
     * in the working directory, overwriting the versions of the files that are
     * already there if they exist. Also, at the end of this command, the given
     * branch will now be considered the current branch (HEAD). Any files that are
     * tracked in the current branch but are not present in the checked-out branch
     * are deleted. The staging area is cleared, unless the checked-out branch
     * is the current branch
     * @param branchName the branch to check out to
     */
    public void checkoutBranch(String branchName) {
        if (!isInit()) {
            printNotInitAndExit();
        }
        // find if the branch exists
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            message("No such branch exists.");
            System.exit(0);
        }

        // check if the branch is the current branch
        if (readContentsAsString(CURRENT_BRANCH).equals(branchName)) {
            message("No need to checkout the current branch. ");
            System.exit(0);
        }

        Branch branch = readObject(branchFile, Branch.class);
        Commit commit = Commit.fromFile(branch.getCommitPointer());

        // change the CWD
        checkoutBranchHelper(commit, readHEAD());

        // update the current branch and head
        writeContents(CURRENT_BRANCH, branchName);
        updateHead(commit.getUID());

        clearStagingArea();
    }

    /**
     * Change the CWD status, from the old commit to the new commit.
     * @param newCommit the new Commit
     * @param oldCommit the old Commit
     */
    private void checkoutBranchHelper(Commit newCommit, Commit oldCommit) {
        Map<String, String> oldBlobs = oldCommit.getPathToBlobs();
        Map<String, String> newBlobs = newCommit.getPathToBlobs();

        // Check if there are untracked files
        for (String filePath : newBlobs.keySet()) {
            File file = new File(filePath);
            if (!oldBlobs.containsKey(filePath)) {
                if (file.exists()) {
                    message("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }

        // add new files to the new Commit
        for (String filePath : newBlobs.keySet()) {
            File file = new File(filePath);
            checkoutFile(newCommit, file.getName());
        }

        // remove the old files from the old Commit
        for (String filePath : oldBlobs.keySet()) {
            if (!newBlobs.containsKey(filePath)) {
                File deletedFile = new File(filePath);
                deletedFile.delete();
            }
        }
    }

    /**
     * Creates a new branch with the given name, and points it at the current head commit.
     * A branch is nothing more than a name for a reference (an SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly created branch (just as in real Git).
     * Before you ever call branch, your code should be running with a default branch called “master”.
     * @param branchName the name of the new branch to create
     */
    public void branch(String branchName) {
        if (!isInit()) {
            printNotInitAndExit();
        }
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        assert branches != null;
        if (branches.contains(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }

        Branch branch = new Branch(branchName, readHEAD());
        branch.save();
    }

    /**
     * Deletes the branch with the given name. This only means to delete the pointer
     * associated with the branch; it does not mean to delete all commits that were
     * created under the branch, or anything like that.
     * @param branchName the name of the branch to delete
     */
    public void rm_branch(String branchName) {
        if (!isInit()) {
            printNotInitAndExit();
        }
        // if it's current branch, do not remove it.
        if (readContentsAsString(CURRENT_BRANCH).equals(branchName)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }

        // find the branch name, and remove it.
        boolean found = false;
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        assert branches != null;
        for (String branch : branches) {
            if (branch.equals(branchName)) {
                Branch.fromFile(branch).delete();
                found = true;
                break;
            }
        }
        // if not found, print out an error message.
        if (!found) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    /**
     * Checks out all the files tracked by the given commit. Removes tracked
     * files that are not present in that commit. Also moves the current
     * branch’s head to that commit node. See the intro for an example of what
     * happens to the head pointer after using reset. The [commit id] may be
     * abbreviated as for checkout. The staging area is cleared. The command
     * is essentially checkout of an arbitrary commit that also changes the
     * current branch head.
     * @param commitID the Commit ID, which can be short as well
     */
    public void reset(String commitID) {
        if (!isInit()) {
            printNotInitAndExit();
        }
        Commit commit = Commit.fromFile(commitID);
        checkoutBranchHelper(commit, readHEAD());

        updateHead(commit.getUID());
        updateBranch(commit);
        clearStagingArea();
    }

    /**
     * Merges files from the given branch into the current branch.
     * @param branchName the branch name
     */
    public void merge(String branchName) {
        if (!isInit()) {
            printNotInitAndExit();
        }

        // check if the branch exists,
        checkBranchExists(branchName);
        // check if it is the current branch
        if (branchName.equals(readContentsAsString(CURRENT_BRANCH))) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        // check if the staging area is empty
        if (!readAdditionArea().isEmpty() || !readRemovalArea().isEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }

        // get the split commit
        Commit splitCommit = getSplitCommit(branchName);

        // get the branch commit(other commit)
        Branch branch = readObject(Branch.fromFile(branchName), Branch.class);
        String branchCommitID = branch.getCommitPointer();
        Commit branchCommit = Commit.fromFile(branchCommitID);

        // if the split point is the same commit as the given branch, then we do nothing
        if (splitCommit.getUID().equals(branchCommit.getUID())) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }

        // If the split point is the current branch, then the effect is to check out the given branch
        if (splitCommit.getUID().equals(readHEAD().getUID())) {
            checkoutBranchHelper(branchCommit, readHEAD());
            message("Current branch fast-forwarded.");
            System.exit(0);
        }

        // time for the main process to merge
        // get the merge result
        Map<String, String> mergeResult = getMergeResult(splitCommit, branchCommit, readHEAD());
        boolean conflictFlag = checkMerge(mergeResult);

        // merge operation
        mergeHelper(mergeResult, branchCommit);

        // merge the two commit
        mergeCommit(branchName, branchCommit);

        if (conflictFlag) {
            message("Encountered a merge conflict.");
            System.exit(0);
        }
    }

    /**
     * Help finish the merge operations, judge the value of the mergeResult Map
     * and deal with the not existed files and conflict.
     * @param mergeResult the merge result
     * @param branchCommit the branch commit
     */
    private void mergeHelper(Map<String, String> mergeResult, Commit branchCommit) {
        Map<String, String> headCommitFiles = readHEAD().getPathToBlobs();
        Map<String, String> branchCommitFiles = branchCommit.getPathToBlobs();

        for (String filePath : mergeResult.keySet()) {
            switch (mergeResult.get(filePath)) {
                case "0":
                    if (headCommitFiles.containsKey(filePath)) {
                        File file = new File(filePath);
                        rm(file.getName());
                    }
                    break;
                case "conflict":
                    String headBlobUID = headCommitFiles.get(filePath);
                    String branchBlobUID = branchCommitFiles.get(filePath);
                    String content, current, branch;
                    if (headBlobUID.equals("0") && !branchBlobUID.equals("0")) {
                        Blob branchBlob = Blob.fromFile(branchBlobUID);
                        byte[] branchContent = branchBlob.getContent();
                        current = "";
                        branch = new String(branchContent, StandardCharsets.UTF_8);
                    } else if (!headBlobUID.equals("0") && branchBlobUID.equals("0")) {
                        Blob headBlob = Blob.fromFile(headBlobUID);
                        byte[] headContent = headBlob.getContent();
                        current = new String(headContent, StandardCharsets.UTF_8);
                        branch = "";
                    } else if (!headBlobUID.equals("0") && !branchBlobUID.equals("0")) {
                        Blob headBlob = Blob.fromFile(headBlobUID);
                        Blob branchBlob = Blob.fromFile(branchBlobUID);
                        byte[] headContent = headBlob.getContent();
                        byte[] branchContent = branchBlob.getContent();
                        current = new String(headContent, StandardCharsets.UTF_8);
                        branch = new String(branchContent, StandardCharsets.UTF_8);
                    }else {
                        current = "";
                        branch = "";
                    }
                    content = "<<<<<<< HEAD\n" + current + "=======\n" + branch + ">>>>>>>\n";
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    createNewFile(file);
                    writeContents(file, content);
                    Blob newBlob = new Blob(file);
                    newBlob.save();
                    readAdditionArea().addBlob(filePath, newBlob.getUID());
                    break;
                default:
                    // if in the branch commit
                    if (headCommitFiles.get(filePath).equals("0")
                    || !mergeResult.get(filePath).equals(headCommitFiles.get(filePath))) {
                        Blob blob = Blob.fromFile(mergeResult.get(filePath));
                        mergeFileHelper(blob);
                    }
                    break;
            }
        }
    }

    /**
     * Help merge the file modified in the other commit and add it to the addition area
     * @param blob the blob to add
     */
    private void mergeFileHelper(Blob blob) {
        File file = new File(blob.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        createNewFile(file);
        byte[] content = blob.getContent();
        writeContents(file, content);
        // add to the staging area
        readAdditionArea().addBlob(file.getPath(), blob.getUID());
    }

    /**
     * make the merge Commit
     * @param branchName the branch name
     * @param branchCommit the branch commit
     */
    private void mergeCommit(String branchName, Commit branchCommit) {
        // create the merge Commit
        String commitMessage = "Merged " + branchName + " into " + readContentsAsString(CURRENT_BRANCH) + ".";
        Commit commit = new Commit(commitMessage, readHEAD());
        commit.addParent(branchCommit.getUID());
        commitHelper(commit);
        commit.save();

        // update Head and the current branch
        updateHead(commit.getUID());
        updateBranch(commit);

        // Clear the two StagingArea, including the AdditionArea and the RemovalArea.
        clearStagingArea();
    }

    /**
     * check if there is untracked file and return the flag if there is conflict
     * @param mergeResult the merge result
     * @return if there is conflict
     */
    private boolean checkMerge(Map<String, String> mergeResult) {
        Map<String, String> headCommitFiles = readHEAD().getPathToBlobs();
        for (String filePath : mergeResult.keySet()) {
            if (!headCommitFiles.containsKey(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    message("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        return mergeResult.containsValue("conflict");
    }

    /**
     * get the merge result as a Map, when the file doesn't exist, set its value to "0",
     * when the file encounters conflict, set its value to "conflict"
     * @param splitCommit the split Commit
     * @param branchCommit the given Commit
     * @param headCommit the head Commit
     * @return the Map of merge result
     */
    private Map<String, String> getMergeResult(Commit splitCommit, Commit branchCommit, Commit headCommit) {
        Map<String, String> splitCommitFiles = splitCommit.getPathToBlobs();
        Map<String, String> branchCommitFiles = branchCommit.getPathToBlobs();
        Map<String, String> headCommitFiles = headCommit.getPathToBlobs();
        Map<String, String> allFiles = new HashMap<>();
        Map<String, String> result = new HashMap<>();

        // add all files to the allFiles Map
        for (String fileName : splitCommitFiles.keySet()) {
            allFiles.put(fileName, splitCommitFiles.get(fileName));
        }
        for (String fileName : branchCommitFiles.keySet()) {
            allFiles.put(fileName, branchCommitFiles.get(fileName));
        }
        for (String fileName : headCommitFiles.keySet()) {
            allFiles.put(fileName, headCommitFiles.get(fileName));
        }

        // add all the not existed file to the commit Map, with the blob's ID is "0"
        for (String fileName : allFiles.keySet()) {
            if (!splitCommitFiles.containsKey(fileName)) {
                splitCommitFiles.put(fileName, "0");
            }
            if (!branchCommitFiles.containsKey(fileName)) {
                branchCommitFiles.put(fileName, "0");
            }
            if (!headCommitFiles.containsKey(fileName)) {
                headCommitFiles.put(fileName, "0");
            }
        }

        // judge equal
        for (String fileName : allFiles.keySet()) {
            // head is modified but given is not modified
            if (splitCommitFiles.get(fileName).equals(branchCommitFiles.get(fileName))
                    && !splitCommitFiles.get(fileName).equals(headCommitFiles.get(fileName))) {
                result.put(fileName, headCommitFiles.get(fileName));
            }

            // given is modified but head is not modified
            else if (!splitCommitFiles.get(fileName).equals(branchCommitFiles.get(fileName))
                    && splitCommitFiles.get(fileName).equals(headCommitFiles.get(fileName))) {
                result.put(fileName, branchCommitFiles.get(fileName));
            }

            // both is modified
            else if (!splitCommitFiles.get(fileName).equals(branchCommitFiles.get(fileName))
                    && !splitCommitFiles.get(fileName).equals(headCommitFiles.get(fileName))) {
                // if they are modified in the same way
                if (headCommitFiles.get(fileName).equals(branchCommitFiles.get(fileName))) {
                    result.put(fileName, headCommitFiles.get(fileName));
                }
                // if they are modified differently, add it to the result and the blob's ID set to "conflict"
                else {
                    result.put(fileName, "conflict");
                }
            }
        }
        return result;
    }

    /**
     * Get the split commit of HEAD and the other commit
     * @param branchName the split commit of the branch and the HEAD
     * @return the split commit
     */
    private Commit getSplitCommit(String branchName) {
        // get the branch commit
        File branchFile = Branch.fromFile(branchName);
        Branch branch = readObject(branchFile, Branch.class);
        String commitID = branch.getCommitPointer();
        Commit branchCommit = Commit.fromFile(commitID);

        Map<String, Integer> routeBranch = getRouteToInit(branchCommit);
        Map<String, Integer> routeHEAD = getRouteToInit(readHEAD());

        String splitCommitID = "";
        int minValue = Integer.MAX_VALUE;
        for (String commit : routeBranch.keySet()) {
            if (routeHEAD.containsKey(commit)) {
                if (routeBranch.get(commit) < minValue) {
                    splitCommitID = commit;
                    minValue = routeBranch.get(commit);
                }
            }
        }
        return Commit.fromFile(splitCommitID);
    }

    /**
     * Return the route from the start commit to the initial commit, using BFS
     * @param commit the start commit
     * @return the whole Map of the route to the initial commit
     */
    private Map<String, Integer> getRouteToInit(Commit commit) {
       Map<String, Integer> route = new HashMap<>();
       Queue<String> q = new ArrayDeque<>();
       route.put(commit.getUID(), 0);
       q.add(commit.getUID());
       while (!q.isEmpty()) {
           String commitID = q.poll();
           Commit thisCommit = Commit.fromFile(commitID);
           for (String parentCommit : thisCommit.getParents()) {
               if (!route.containsKey(parentCommit)) {
                   route.put(parentCommit, route.get(commitID) + 1);
                   q.add(parentCommit);
               }
               else {
                   break;
               }
           }
       }
       return route;
    }

    /**
     * check if the branch exists
     * @param branch the branch name to be merged
     */
    private void checkBranchExists(String branch) {
        // check if the branch exists
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        boolean found = false;
        assert branches != null;
        for (String branchName : branches) {
            if (branch.equals(branchName)) {
                found = true;
                break;
            }
        }
        if (!found) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
    }

}
