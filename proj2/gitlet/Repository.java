package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  The repository structure:
 *  .gitlet/
 *      - objects/
 *          - commits/
 *              - ...(files of commits)
 *          - blobs/
 *              - ...(files of blobs)
 *      - branches/
 *          - master
 *          - ...(other branches)
 *      - HEAD
 *      - Stages/
 *          - addStage
 *          - removeStage
 *  does at a high level.
 *
 *  @author Junlang
 */
public class Repository {
    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    private static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * The objects, commits, blobs directory.
     */
    private static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    private static final File COMMIT_DIR = join(OBJECT_DIR, "commits");
    private static final File BLOB_DIR = join(OBJECT_DIR, "blobs");

    /**
     * The branches' directory.
     */
    private static final File BRANCH_DIR = join(GITLET_DIR, "branches");

    /**
     * The stages' directory.
     */
    private static final File STAGE_DIR = join(GITLET_DIR, "stages");

    /**
     * The remotes' directory.
     */
    private static final File REMOTE_DIR = join(GITLET_DIR, "remotes");

    /**
     * The HEAD pointer.
     */
    private static final File HEAD = join(GITLET_DIR, "HEAD");

    /**
     * The current commit.
     */
    private static Commit currentCommit;

    /**
     * The current branch.
     */
    private static final File CURRENT_BRANCH = join(GITLET_DIR, "BRANCH");

    public static File getCWD() {
        return CWD;
    }

    public static File getCommitDir() {
        return COMMIT_DIR;
    }

    public static File getBlobDir() {
        return BLOB_DIR;
    }

    public static File getBranchDir() {
        return BRANCH_DIR;
    }

    public static File getStageDir() {
        return STAGE_DIR;
    }

    public static File getRemoteDir() {
        return REMOTE_DIR;
    }

    /**
     * The init command
     */
    public void init() {
        checkIfTheDirectoryNotExist();
        GITLET_DIR.mkdirs();
        OBJECT_DIR.mkdirs();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        BRANCH_DIR.mkdir();
        STAGE_DIR.mkdir();
        REMOTE_DIR.mkdir();
        Commit initCommit = new Commit();
        initCommit.save();
        currentCommit = initCommit;
        setHEAD();
        initMaster();
        initStage();
    }

    /**
     * Check if the .gitlet/ directory exists. If yes, exit the program.
     * Only serving for the init command.
     */
    private void checkIfTheDirectoryNotExist() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists "
                    + "in the current directory.");
            System.exit(0);
        }
    }

    /**
     * Check if the .gitlet/ directory exists. If no, exit the program.
     * In contradiction with the checkIfTheDirectoryNotExist method.
     */
    private void checkIfTheDirectoryExist() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /**
     * Set the HEAD pointer, pointing at the currentCommit.
     */
    private void setHEAD() {
        createNewFile(HEAD);
        writeContents(HEAD, currentCommit.getUID());
    }

    /**
     * Initial the first branch master, and point it at the initCommit.
     * Only serving for the init command.
     */
    private void initMaster() {
        Branch master = new Branch(currentCommit);
        master.save();
        createNewFile(CURRENT_BRANCH);
        writeContents(CURRENT_BRANCH, "master");
    }

    /**
     * The Initial stages, including addStage and removeStage.
     */
    private void initStage() {
        StagingArea addStage = new StagingArea("addStage");
        StagingArea removeStage = new StagingArea("removeStage");
        addStage.save();
        removeStage.save();
    }

    /**
     * Set the branch pointer at the current commit.
     */
    private void setBranch() {
        File branchFile = join(BRANCH_DIR, readContentsAsString(CURRENT_BRANCH));
        Branch branch = readObject(branchFile, Branch.class);
        branch.setCommitPointer(currentCommit);
        branch.save();
    }

    /**
     * The add [fileName] command
     *
     * @param fileName the name of the file to be added.
     */
    public void add(String fileName) {
        checkIfTheDirectoryExist();
        File file = join(CWD, fileName);
        if (readRemoveStage().getPathToBlobs().containsKey(file.getPath())) {
            readRemoveStage().removeBlob(file.getPath());
        } else {
            checkFileExist(file);
            checkTheSame(file);
            Blob fileBlob = new Blob(file);
            fileBlob.save();
            StagingArea addStage = readAddStage();
            addStage.addBlob(fileBlob);
//            addStage.save();
        }
    }

    private void checkTheSame(File file) {
        if (readHEAD().getPathToBlobs().containsKey(file.getPath())) {
            Blob fileBlob = new Blob(file);
            if (Objects.equals(fileBlob.getUID(),
                    readHEAD().getPathToBlobs().get(file.getPath()))) {
                System.exit(0);
            }
        }
    }

    /**
     * Return the addStage.
     */
    private StagingArea readAddStage() {
        File addStageFile = join(STAGE_DIR, "addStage");
        return readObject(addStageFile, StagingArea.class);
    }

    /**
     * Return the removeStage.
     */
    private StagingArea readRemoveStage() {
        File removeStageFile = join(STAGE_DIR, "removeStage");
        return readObject(removeStageFile, StagingArea.class);
    }

    /**
     * Check if the file exists. If not, exit the program.
     *
     * @param file the file to be checked
     */
    private void checkFileExist(File file) {
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
    }

    /**
     * The commit [message] command.
     *
     * @param message the commit message.
     */
    public void commit(String message) {
        checkIfTheDirectoryExist();
        checkStageIsEmpty();
        Commit newCommit = new Commit(message, readHEAD());
        commitHelper(newCommit);
        newCommit.save();
        currentCommit = newCommit;
        setHEAD();
        setBranch();
        clearStage();
    }

    /**
     * Set the commit map.
     *
     * @param newCommit the new commit
     */
    private void commitHelper(Commit newCommit) {
        Map<String, String> addBlobs = readAddStage().getPathToBlobs();
        Map<String, String> removeBlobs = readRemoveStage().getPathToBlobs();
        Map<String, String> commitBlobs = newCommit.getPathToBlobs();
        List<String> toAdd = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();
        for (String addBlobPath : addBlobs.keySet()) {
            if (!commitBlobs.containsKey(addBlobPath)) {
                newCommit.addBlob(addBlobPath, addBlobs.get(addBlobPath));
            }
            for (String blobPath : commitBlobs.keySet()) {
                if (Objects.equals(blobPath, addBlobPath)
                        && !Objects.equals(commitBlobs.get(blobPath), addBlobs.get(addBlobPath))) {
                    toRemove.add(blobPath);
                    toAdd.add(blobPath);
                }
            }
        }
        for (String removeBlobPath : removeBlobs.keySet()) {
            for (String blobPath : commitBlobs.keySet()) {
                if (Objects.equals(removeBlobPath, blobPath)) {
                    toRemove.add(removeBlobPath);
                }
            }
        }
        for (String blobToRemove : toRemove) {
            newCommit.removeBlob(blobToRemove);
        }
        for (String blobToAdd : toAdd) {
            newCommit.addBlob(blobToAdd, addBlobs.get(blobToAdd));
        }
    }

    /**
     * Return the commit that HEAD pointed at.
     */
    private Commit readHEAD() {
        File headCommit = join(COMMIT_DIR, readContentsAsString(HEAD));
        return readObject(headCommit, Commit.class);
    }

    /**
     * Check if the stage area is empty, if yes, exit the program.
     */
    private void checkStageIsEmpty() {
        if (readAddStage().isEmpty() && readRemoveStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
    }

    /**
     * Clear the stage area.
     */
    private void clearStage() {
        readAddStage().clear();
        readRemoveStage().clear();
    }

    /**
     * The rm [fileName] command
     *
     * @param fileName the name of the file to be removed
     */
    public void remove(String fileName) {
        checkIfTheDirectoryExist();
        checkRemove(fileName);
        Map<String, String> addBlobs = readAddStage().getPathToBlobs();
        File file = join(CWD, fileName);
        String filePath = file.getPath();
        if (addBlobs.containsKey(filePath)) {
            readAddStage().removeBlob(filePath);
        } else {
            Map<String, String> currentCommitBlobs = readHEAD().getPathToBlobs();
            if (currentCommitBlobs.containsKey(filePath)) {
                String id = currentCommitBlobs.get(filePath);
                readRemoveStage().addBlob(filePath, id);
                readHEAD().removeBlob(filePath);
                if (file.exists()) {
                    restrictedDelete(file);
                }
            }
        }
    }

    /**
     * Check whether the rm command is legal.
     */
    private void checkRemove(String fileName) {
        Map<String, String> addBlobs = readAddStage().getPathToBlobs();
        Map<String, String> currentCommitBlobs = readHEAD().getPathToBlobs();
        String filePath = join(CWD, fileName).getPath();
        if (!addBlobs.containsKey(filePath) && !currentCommitBlobs.containsKey(filePath)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    /**
     * The log command.
     */
    public void log() {
        checkIfTheDirectoryExist();
        currentCommit = readHEAD();
        while (!currentCommit.getParents().isEmpty()) {
            currentCommit.print();
            File next = join(COMMIT_DIR, currentCommit.getFirstParent());
            currentCommit = readObject(next, Commit.class);
        }
        currentCommit.print();
    }

    /**
     * The global-log command.
     */
    public void globalLog() {
        checkIfTheDirectoryExist();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        assert commits != null;
        for (String commitId : commits) {
            File commitFile = join(COMMIT_DIR, commitId);
            readObject(commitFile, Commit.class).print();
        }
    }

    /**
     * The find [commitMessage] command.
     *
     * @param commitMessage the message that the printed commits contain
     */
    public void find(String commitMessage) {
        checkIfTheDirectoryExist();
        List<String> commitToPrint = new ArrayList<>();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        assert commits != null;
        for (String commitId : commits) {
            File commitFile = join(COMMIT_DIR, commitId);
            String message = readObject(commitFile, Commit.class).getMessage();
            if (Objects.equals(commitMessage, message)) {
                commitToPrint.add(commitId);
            }
        }
        if (commitToPrint.isEmpty()) {
            System.out.println("Found no commit with that message.");
        } else {
            for (String commitId : commitToPrint) {
                System.out.println(commitId);
            }
        }
    }

    /**
     * The status command.
     */
    public void status() {
        checkIfTheDirectoryExist();
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        System.out.println("=== Branches ===");
        assert branches != null;
        for (String branchName : branches) {
            if (Objects.equals(branchName, currentBranch)) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println("\n=== Staged Files ===");
        readAddStage().print();
        System.out.println("\n=== Removed Files ===");
        readRemoveStage().print();
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        modifiedFiles();
        System.out.println("\n=== Untracked Files ===");
        untrackedFiles();
        System.out.println("\n");
    }

    /**
     * Print out the modified files.
     */
    private void modifiedFiles() {
        Map<String, String> currentFiles = readHEAD().getPathToBlobs();
        for (String filePath : currentFiles.keySet()) {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!readRemoveStage().containsFile(filePath)) {
                    System.out.println(file.getName() + " (deleted)");
                }
                break;
            }
            Blob fileBlob = new Blob(file);
            if (!Objects.equals(fileBlob.getUID(), currentFiles.get(filePath))) {
                if (!readAddStage().containsFile(filePath)) {
                    System.out.println(file.getName() + " (modified)");
                }
            }
        }
        for (String filePath : readAddStage().getPathToBlobs().keySet()) {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println(file.getName() + " (deleted)");
                break;
            }
            Blob fileBlob = new Blob(file);
            if (!Objects.equals(fileBlob.getUID(),
                    readAddStage().getPathToBlobs().get(filePath))) {
                System.out.println(file.getName() + " (modified)");
            }
        }
    }

    /**
     * Print out the untracked files.
     */
    private void untrackedFiles() {
        List<String> files = plainFilenamesIn(CWD);
        if (files != null) {
            for (String fileName : files) {
                File file = join(CWD, fileName);
                String filePath = file.getPath();
                if (!readHEAD().getPathToBlobs().containsKey(filePath)
                        && !readAddStage().containsFile(filePath)
                        && !readRemoveStage().containsFile(filePath)) {
                    System.out.println(fileName);
                }
            }
        }
    }

    /**
     * The branch [branchName] command.
     *
     * @param branchName the new branch name
     */
    public void branch(String branchName) {
        checkIfTheDirectoryExist();
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
     * The rm-branch [branchName] command.
     *
     * @param branchName the name of the branch to be removed.
     */
    public void removeBranch(String branchName) {
        checkIfTheDirectoryExist();
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        assert branches != null;
        if (!branches.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        if (currentBranch.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File branchFile = join(BRANCH_DIR, branchName);
        branchFile.delete();
    }

    /**
     * The checkout -- [fileName] command.
     *
     * @param fileName the name of the file
     */
    public void checkoutHeadFile(String fileName) {
        checkIfTheDirectoryExist();
        checkoutFileHelper(readHEAD(), fileName);
    }

    /**
     * The checkout [commitId] -- [fileName] command.
     *
     * @param commitId the commit id
     * @param fileName the name of the file
     */
    public void checkoutCommitFile(String commitId, String fileName) {
        checkIfTheDirectoryExist();
        File commitFile;
        if (commitId.length() < UID_LENGTH) {
            commitFile = shortIdCommit(commitId);
        } else {
            commitFile = join(COMMIT_DIR, commitId);
        }

        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        } else {
            Commit commit = readObject(commitFile, Commit.class);
            checkoutFileHelper(commit, fileName);
        }
    }

    /**
     * @param shortId the short id of the commit
     * @return the commit file
     */
    private File shortIdCommit(String shortId) {
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        int length = shortId.length();
        assert commits != null;
        for (String commitId : commits) {
            if (commitId.substring(0, length).equals(shortId)) {
                return join(COMMIT_DIR, commitId);
            }
        }
        return null;
    }

    /**
     * The checkout [branchName] command.
     *
     * @param branchName the name of the branch
     */
    public void checkoutBranch(String branchName) {
        checkIfTheDirectoryExist();
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        if (currentBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Branch branch = readObject(branchFile, Branch.class);
        File newCommitFile = join(COMMIT_DIR, branch.getCommitPointer());
        currentCommit = readObject(newCommitFile, Commit.class);
        checkoutBranchHelper(currentCommit, readHEAD());
        writeContents(CURRENT_BRANCH, branchName);
        setHEAD();
        clearStage();
    }

    /**
     * Change the CWD status, from the old commit to the new commit.
     *
     * @param newCommit the new commit that will be checked out
     * @param oldCommit the old commit
     */
    private void checkoutBranchHelper(Commit newCommit, Commit oldCommit) {
        Map<String, String> oldBlobs = oldCommit.getPathToBlobs();
        Map<String, String> newBlobs = newCommit.getPathToBlobs();
        for (String filePath : newBlobs.keySet()) {
            File file = new File(filePath);
            if (!oldBlobs.containsKey(filePath)) {
                if (file.exists()) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        for (String filePath : newBlobs.keySet()) {
            File file = new File(filePath);
            checkoutFileHelper(newCommit, file.getName());
        }
        for (String filePath : oldBlobs.keySet()) {
            if (!newBlobs.containsKey(filePath)) {
                File deleteFile = new File(filePath);
                deleteFile.delete();
            }
        }
    }

    /**
     * Check out the file in the special commit, serving for the checkout command.
     *
     * @param commit   the special commit
     * @param fileName the name of the file
     */
    private void checkoutFileHelper(Commit commit, String fileName) {
        File file = join(CWD, fileName);
        String filePath = file.getPath();
        if (!commit.getPathToBlobs().containsKey(filePath)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File blobFile = join(BLOB_DIR, commit.getPathToBlobs().get(filePath));
        Blob blob = readObject(blobFile, Blob.class);
        byte[] fileContent = blob.getContent();
        if (file.exists()) {
            file.delete();
        }
        createNewFile(file);
        writeContents(file, fileContent);
    }

    /**
     * The reset [commitId] command.
     *
     * @param commitId the id of the commit which we will reset to
     */
    public void reset(String commitId) {
        File commitFile = join(COMMIT_DIR, commitId);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit newCommit = readObject(commitFile, Commit.class);
        Commit oldCommit = readHEAD();
        checkoutBranchHelper(newCommit, oldCommit);
        currentCommit = newCommit;
        setHEAD();
        setBranch();
        clearStage();
    }


    /**
     * The merge [branchName] command.
     *
     * @param branchName the name of the branch which will be merged
     */
    public void merge(String branchName) {
        checkIfTheDirectoryExist();
        checkMerge(branchName);
        Commit spiltCommit = getSpiltPoint(branchName);
        File branchFile = join(BRANCH_DIR, branchName);
        Branch branch = readObject(branchFile, Branch.class);
        String branchCommitId = branch.getCommitPointer();
        File branchCommitFile = join(COMMIT_DIR, branchCommitId);
        Commit branchCommit = readObject(branchCommitFile, Commit.class);
        easyMerge(branchCommit, spiltCommit);
        Map<String, String> result = mergeResult(spiltCommit, branchCommit, readHEAD());
        Boolean conflictFlag = checkMergeOperations(result);
        mergeOperations(result, branchCommit);
        mergeCommit(branchName, branchCommit);
        if (conflictFlag) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Check if the merge operation is legal.
     *
     * @param branchName the name of the branch which will be merged
     */
    private void checkMerge(String branchName) {
        if (!readAddStage().isEmpty() || !readRemoveStage().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File branchFile = join(BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(CURRENT_BRANCH);
        if (Objects.equals(currentBranch, branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    /**
     * @param branchName the name of the branch to be merged
     * @return the spilt point commit of the head branch and the given branch
     */
    private Commit getSpiltPoint(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        Branch branch = readObject(branchFile, Branch.class);
        String commitId = branch.getCommitPointer();
        File commitFile = join(COMMIT_DIR, commitId);
        Commit commitA = readObject(commitFile, Commit.class);
        Commit commitB = readHEAD();
        Map<String, Integer> routeA = getRouteToInit(commitA);
        Map<String, Integer> routeB = getRouteToInit(commitB);
        String spiltPointCommitId = "";
        int minValue = Integer.MAX_VALUE;
        for (String commit : routeA.keySet()) {
            if (routeB.containsKey(commit)) {
                if (routeB.get(commit) < minValue) {
                    spiltPointCommitId = commit;
                    minValue = routeB.get(commit);
                }
            }
        }
        File spiltPointCommit = join(COMMIT_DIR, spiltPointCommitId);
        return readObject(spiltPointCommit, Commit.class);
    }

    /**
     * @param commit the start commit
     * @return a map, including the route to the initial commit
     */
    private Map<String, Integer> getRouteToInit(Commit commit) {
        Map<String, Integer> route = new TreeMap<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(commit.getUID());
        route.put(commit.getUID(), 0);
        while (!queue.isEmpty()) {
            String commitId = queue.poll();
            File commitFile = join(COMMIT_DIR, commitId);
            Commit thisCommit = readObject(commitFile, Commit.class);
            for (String parentCommit : thisCommit.getParents()) {
                if (route.containsKey(parentCommit)) {
                    break;
                } else {
                    queue.add(parentCommit);
                    route.put(parentCommit, route.get(commitId) + 1);
                }
            }
        }
        return route;
    }

    /**
     * @param branchCommit the commit that branch pointed at
     * @param spiltCommit  the spilt point commit
     */
    private void easyMerge(Commit branchCommit, Commit spiltCommit) {
        if (Objects.equals(spiltCommit.getUID(), readHEAD().getUID())) {
            checkoutBranchHelper(branchCommit, readHEAD());
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        if (Objects.equals(spiltCommit.getUID(), branchCommit.getUID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
    }

    /**
     * @param spiltCommit  the spilt point commit
     * @param branchCommit the commit that branch pointed at
     * @param current      the current commit, a.k.a. the HEAD commit
     * @return the merge result, representing in a map. The key is file path,
     * the value is the blob id,
     * and the "0" represents not exist,
     * the "conflict" represents there exists conflicts
     */
    private Map<String, String> mergeResult(Commit spiltCommit,
                                                   Commit branchCommit,
                                                   Commit current) {
        Map<String, String> allFiles = new TreeMap<>();
        Map<String, String> spiltCommitFiles = spiltCommit.getPathToBlobs();
        Map<String, String> branchCommitFiles = branchCommit.getPathToBlobs();
        Map<String, String> currentCommitFiles = current.getPathToBlobs();
        Map<String, String> result = new TreeMap<>();
        for (String filePath : spiltCommitFiles.keySet()) {
            allFiles.put(filePath, spiltCommitFiles.get(filePath));
        }
        for (String filePath : branchCommitFiles.keySet()) {
            allFiles.put(filePath, branchCommitFiles.get(filePath));
        }
        for (String filePath : currentCommitFiles.keySet()) {
            allFiles.put(filePath, currentCommitFiles.get(filePath));
        }
        for (String filePath : allFiles.keySet()) {
            if (!spiltCommitFiles.containsKey(filePath)) {
                spiltCommitFiles.put(filePath, "0");
            }
            if (!branchCommitFiles.containsKey(filePath)) {
                branchCommitFiles.put(filePath, "0");
            }
            if (!currentCommitFiles.containsKey(filePath)) {
                currentCommitFiles.put(filePath, "0");
            }
        }
        for (String filePath : allFiles.keySet()) {
            if (Objects.equals(spiltCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))
                    && !Objects.equals(spiltCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))) {
                result.put(filePath, currentCommitFiles.get(filePath));
            } else if (!Objects.equals(spiltCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))
                    && Objects.equals(spiltCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))) {
                result.put(filePath, branchCommitFiles.get(filePath));
            } else if (Objects.equals(currentCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))) {
                result.put(filePath, currentCommitFiles.get(filePath));
            } else if (!Objects.equals(spiltCommitFiles.get(filePath),
                    branchCommitFiles.get(filePath))
                    && !Objects.equals(spiltCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))
                    && !Objects.equals(branchCommitFiles.get(filePath),
                    currentCommitFiles.get(filePath))) {
                result.put(filePath, "conflict");
            }
        }
        return result;
    }

    /**
     * Check if the merge result is legal.
     *
     * @param result the map that contains the merge result
     */
    private Boolean checkMergeOperations(Map<String, String> result) {
        Map<String, String> currentCommitFiles = readHEAD().getPathToBlobs();
        for (String filePath : result.keySet()) {
            if (!currentCommitFiles.containsKey(filePath)) {
                File file = new File(filePath);
                if (file.exists()) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
        return result.containsValue("conflict");
    }

    /**
     * Do the merge operations.
     *
     * @param result the map that contains the merge result
     */
    private void mergeOperations(Map<String, String> result,
                                        Commit branchCommit) {
        Map<String, String> currentCommitFiles = readHEAD().getPathToBlobs();
        Map<String, String> branchCommitFiles = branchCommit.getPathToBlobs();
        for (String filePath : result.keySet()) {
            switch (result.get(filePath)) {
                case "0":
                    if (currentCommitFiles.containsKey(filePath)) {
                        File file = new File(filePath);
                        remove(file.getName());
                    }
                    break;
                case "conflict":
                    String currentBlobId = currentCommitFiles.get(filePath);
                    String branchBlobId = branchCommitFiles.get(filePath);
                    String content;
                    String current;
                    String branch;
                    if (!Objects.equals(currentBlobId, "0")
                            && !Objects.equals(branchBlobId, "0")) {
                        Blob currentBlob = readObject(join(BLOB_DIR,
                                currentBlobId), Blob.class);
                        Blob branchBlob = readObject(join(BLOB_DIR,
                                branchBlobId), Blob.class);
                        byte[] currentContents = currentBlob.getContent();
                        byte[] branchContents = branchBlob.getContent();
                        current = new String(currentContents,
                                StandardCharsets.UTF_8);
                        branch = new String(branchContents,
                                StandardCharsets.UTF_8);
                    } else if (Objects.equals(currentBlobId, "0")
                            && !Objects.equals(branchBlobId, "0")) {
                        Blob branchBlob = readObject(join(BLOB_DIR,
                                branchBlobId), Blob.class);
                        byte[] branchContents = branchBlob.getContent();
                        current = "";
                        branch = new String(branchContents,
                                StandardCharsets.UTF_8);
                    } else if (!Objects.equals(currentBlobId, "0")
                            && Objects.equals(branchBlobId, "0")) {
                        Blob currentBlob = readObject(join(BLOB_DIR,
                                currentBlobId), Blob.class);
                        byte[] currentContents = currentBlob.getContent();
                        current = new String(currentContents, StandardCharsets.UTF_8);
                        branch = "";
                    } else {
                        branch = "";
                        current = "";
                    }
                    content = "<<<<<<< HEAD\n" + current + "=======\n"
                            + branch + ">>>>>>>\n";
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    createNewFile(file);
                    writeContents(file, content);
                    Blob fileBlob = new Blob(file);
                    fileBlob.save();
                    readAddStage().addBlob(filePath, fileBlob.getUID());
                    break;
                default:
                    if (Objects.equals(currentCommitFiles.get(filePath), "0")) {
                        Blob blob = readObject(join(BLOB_DIR,
                                result.get(filePath)), Blob.class);
                        mergeFileHelper(blob);
                    } else if (!Objects.equals(result.get(filePath),
                            currentCommitFiles.get(filePath))) {
                        Blob blob = readObject(join(BLOB_DIR,
                                result.get(filePath)), Blob.class);
                        mergeFileHelper(blob);
                    } else {
                        break;
                    }
            }
        }
    }

    /**
     * Help create the file and add it to the add stage.
     *
     * @param fileBlob the blob of the file
     */
    private void mergeFileHelper(Blob fileBlob) {
        File file = new File(fileBlob.getFilePath());
        byte[] contents = fileBlob.getContent();
        if (file.exists()) {
            file.delete();
        }
        createNewFile(file);
        writeContents(file, contents);
        readAddStage().addBlob(file.getPath(), fileBlob.getUID());
    }

    /**
     * Make the merge commit.
     *
     * @param branchName   the branch name
     * @param branchCommit the branch commit
     */
    private void mergeCommit(String branchName, Commit branchCommit) {
        String message = "Merged " + branchName + " into "
                + readContentsAsString(CURRENT_BRANCH) + ".";
        Commit mergeCommit = new Commit(message, readHEAD());
        commitHelper(mergeCommit);
        mergeCommit.addParent(branchCommit.getUID());
        mergeCommit.save();
        currentCommit = mergeCommit;
        setHEAD();
        setBranch();
        clearStage();
    }
}