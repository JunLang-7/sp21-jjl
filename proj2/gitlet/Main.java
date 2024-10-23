package gitlet;

import static gitlet.Utils.message;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Junlang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        Repository repo = new Repository();
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init" -> {
                validateArgsNum(args, 1);
                repo.init();
            }
            case "add" -> {
                validateArgsNum(args, 2);
                repo.add(args[1]);
            }
            case "commit" -> {
                if (args.length != 2 || args[1].isEmpty()) {
                    message("Please enter a commit message.");
                    System.exit(0);
                }
                repo.commit(args[1]);
            }
            case "log" -> {
                validateArgsNum(args, 1);
                repo.log();
            }
            case "global-log" -> {
                validateArgsNum(args, 1);
                repo.globalLog();
            }
            case "rm" -> {
                validateArgsNum(args, 2);
                repo.removeBranch(args[1]);
            }
            case "find" -> {
                validateArgsNum(args, 2);
                repo.find(args[1]);
            }
            case "status" -> {
                validateArgsNum(args, 1);
                repo.status();
            }
            case "checkout" -> {
                switch (args.length) {
                    case 3 -> {
                        if (!args[1].equals("--")) {
                            validateArgsNum(args, 100);
                        }
                        repo.checkoutHeadFile(args[2]);
                }
                    case 4 -> {
                        if (!args[2].equals("--")) {
                            validateArgsNum(args, 100);
                        }
                        repo.checkoutCommitFile(args[1], args[3]);
                }
                    case 2 -> repo.checkoutBranch(args[1]);
                    default -> validateArgsNum(args, 100);
                }
            }
            case "branch" -> {
                validateArgsNum(args, 2);
                repo.branch(args[1]);
            }
            case "rm-branch" -> {
                validateArgsNum(args, 2);
                repo.removeBranch(args[1]);
            }
            case "reset" -> {
                validateArgsNum(args, 2);
                repo.reset(args[1]);
            }
            case "merge" -> {
                validateArgsNum(args, 2);
                repo.merge(args[1]);
            }
            default -> message("No command with that name exists.");
        }
    }

    /**
     * Check if the numbers of args is correct.
     * @param args the args that has input in command line.
     * @param i the validate numbers of the args.
     */
    private static void validateArgsNum(String[] args, int i) {
        if (args.length != i) {
            message("Incorrect operands.");
            System.exit(0);
        }
    }
}
