package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if(args.length == 0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];

        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs(args, 2);
                String fileToAddName = args[1];
                Repository.add(fileToAddName);
                // TODO: handle the `add [filename]` command
                break;
            case "commit":
                validateNumArgs(args, 2);
                String massage = args[1];
                Repository.commit(massage);
                break;
            case "checkout":
                validateNumArgs(args, 2, 3, 4);
                if(args.length == 2){
                    String branchName = args[1];
                    Repository.checkOutBranch(branchName);
                }
                else if(args.length == 3){
                    String fileToCheckName = args[1];
                    if(!args[2].equals("--")){
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkOutFile(fileToCheckName);
                }
                else if(args.length == 4) {
                    if(!args[2].equals("--")){
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    String commitId = args[1];
                    String fileToCheckName = args[3];
                    Repository.checkOutCommit(commitId, fileToCheckName);
                }
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "rm":
                validateNumArgs(args, 2);
                String fileToRemoveName = args[1];
                Repository.rm(fileToRemoveName);
                break;
            case "global-log" :
                validateNumArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                String message = args[1];
                Repository.find(message);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "branch":
                validateNumArgs(args, 2);
                String branchName = args[1];
                Repository.branch(branchName);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                String branchNameToRemove = args[1];
                Repository.rmBranch(branchNameToRemove);
                break;
            case "reset":
                validateNumArgs(args, 2);
                String commitID = args[1];
                Repository.reset(commitID);
                break;
            case "merge":
                validateNumArgs(args, 2);
                String branchNameToBeMerged = args[1];
                Repository.merge(branchNameToBeMerged);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void validateNumArgs(String[] args, int ... n){
        if(!args[0].equals("init") && !Repository.GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        for (int j : n) {
            if (args.length == j) {
                return;
            }
        }
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

}


