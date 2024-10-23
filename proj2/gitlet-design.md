# Gitlet Design Document

**Name**: Junlang Jiang

## Classes and Data Structures

### Main
The entrance of the whole program, checking whether inputting the correct args(both the name and the number).

#### Fields
No Field

### Repository
The main class of the program, every command method and its helper method are in the class.

#### Fields

1. CWD             the current working directory
2. GITLET_DIR      the .gitlet repository that staged all the commits and files 
3. OBJECT_DIR      the directory staged all the commits and blobs
4. BLOBS_DIR       the directory staged all the blobs
5. COMMITS_DIR     the directory staged all the commits
6. STAGE_DIR       the directory staged the two staging area including addition adn removal
7. BRANCH_DIR      the directory staged all the branches
8. HEAD            the file staged the head commit UID
9. CURRENT_BRANCH  the file staged the current branch UID

### StagingArea
The class is to generate the addition area and the removal area, doing something with the command add, rm and commit

#### Fields

1. pathToBlobs  The Map contains the filePath as key and the Blob UID as value.
2. stageName    The stagingArea name, including the addition and the removal.

### Blob
The class represents the object of the file, with a unique name owing to the UID.

#### Fields

1. UID          The UID of the Blob with SHA-1
2. sourceFile   The File that writes to the blob
3. blobFile     The Blob file that saves the Blob
4. content      The content of the Blob

### Commit
The class represents a gitlet commit object. with the attribution of message, time, parents and UID.

#### Fields

1. message      The message of this Commit
2. timestamp    The commit time of this Commit
3. parents      The parent of this Commit
4. UID          The UID of this Commit
5. date         The date of this Commit
6. pathToBlobs  The Map that store the path to blob, the same as StagingArea
7. blobs        The UID of blobs that stores in

### Branch
The class represents the branch of the repository, with the branch name and the commit pointer

#### Fields

1. branchName      The branch Name
2. commitPointer   The UID of Commit that the branch points to

### Utils
The class is to help make some utility that can be used in the whole program

#### Field
1. UID_LENGTH    The length of a complete SHA-1 UID as a hexadecimal numeral


## Algorithms

1. Hashing
2. Map
3. Graph BFS

## Persistence

- Always remain the staging area in the Stage Dir
- Always store the head commit in the HEAD and the current branch in CURRENT_BRANCH