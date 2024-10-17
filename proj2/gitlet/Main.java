package gitlet;

import static gitlet.Utils.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        Repository repo = new Repository();
        // TODO: what if args is empty?
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateArgsNum(args, 1);
                repo.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateArgsNum(args, 2);
                repo.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length != 2 || args[1].isEmpty()) {
                    message("Please enter a commit message.");
                    System.exit(0);
                }
                repo.commit(args[1]);
                break;
            case "log":
                validateArgsNum(args, 1);
                repo.log();
                break;
            case "global-log":
                validateArgsNum(args, 1);
                repo.global_log();
                break;
            case "rm":
                validateArgsNum(args, 2);
                repo.rm(args[1]);
                break;
            case "find":
                validateArgsNum(args, 2);
                repo.find(args[1]);
                break;
            case "status":
                validateArgsNum(args, 1);
                repo.status();
                break;
            case "checkout":
                switch (args.length) {
                    case 3:
                        if (!args[1].equals("--")) {
                            validateArgsNum(args, 100);
                        }
                        repo.checkout(args[2]);
                        break;
                    case 4:
                        if (!args[2].equals("--")) {
                            validateArgsNum(args, 100);
                        }
                        repo.checkout(args[1], args[3]);
                        break;
                    case 2:
                        repo.checkoutBranch(args[1]);
                        break;
                    default:
                        validateArgsNum(args, 100);
                }
                break;
            case "branch":
                validateArgsNum(args, 2);
                repo.branch(args[1]);
                break;
            case "rm-branch":
                validateArgsNum(args, 2);
                repo.rm_branch(args[1]);
                break;
            case "reset":
                validateArgsNum(args, 2);
                repo.reset(args[1]);
                break;
            case "merge":
                validateArgsNum(args, 2);
                repo.merge(args[1]);
                break;
            default:
                message("No command with that name exists.");
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
