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
                Repository.add(args[1]);
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
                    String fileName = args[2];
                    Repository.checkOutFile(fileName);
                }
                else if(args.length == 4) {
                    String commitId = args[1];
                    String fileName = args[3];
                    Repository.checkOutCommit(commitId, fileName);
                }
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
                // TODO: FILL THE REST IN
        }
    }

    public static void validateNumArgs(String[] args, int ... n){
        for (int j : n) {
            if (args.length == j) {
                return;
            }
        }
        System.out.println("Incorrect operands.");
        System.exit(0);
    }
}


