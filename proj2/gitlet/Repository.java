package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Fu Sheng You
 */
public class Repository {
    /**
     * *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    //HEAD指標的檔案
    private static final File HEAD = join(GITLET_DIR, "HEAD");

    public static void init(){
        if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        try{
            GITLET_DIR.mkdir();
            Blob.BLOB_DIR.mkdir();
            Commit.COMMIT_DIR.mkdir();
            Branch.BRANCH_DIR.mkdir();
            Branch.setBranch();
            Stage.setStage();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void setHEADByBranchName(String branchName){
        writeContents(HEAD, branchName);
    }

    public static Branch getHEADBranchFromFile(){
        String branchName = readContentsAsString(HEAD);
        return readObject(join(Branch.BRANCH_DIR, branchName), Branch.class);
    }

    private static boolean isTrackedFile(String fileName, Commit currCommit) {
        return currCommit.getFiles().containsKey(fileName);
    }

    private static boolean willOverwriteUntrackedFile(Commit newCommit) {
        List<String> workingFiles = plainFilenamesIn(CWD);
        Commit currentCommit = Commit.getCommitByHash(getHEADBranchFromFile().getHead());

        for (String fileName : workingFiles) {
            boolean isTrackedInCurrentCommit = currentCommit.getFiles().containsKey(fileName);
            boolean isStaged = Stage.getStage().getFilesChanged().containsKey(fileName);
            boolean isTrackedInNewCommit = newCommit.getFiles().containsKey(fileName);

            if (!isTrackedInCurrentCommit && !isStaged && isTrackedInNewCommit) {
                return true;
            }
        }
        return false;
    }


    public static void add(String NameOfFileToStage){
        if(!join(CWD, NameOfFileToStage).exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Stage currenStage = Stage.getStage();
        currenStage.updateStage(NameOfFileToStage);          //更新stage的狀態。
        currenStage.saveStage();                                   //更新後的stage存到stage檔案中。
    }

    public static void rm(String fileToBeRemoveName){
        Branch currBranch = getHEADBranchFromFile();
        Commit currHeadCommit = Commit.getCommitByHash(currBranch.getHead());
        Stage currStage = Stage.getStage();
        if(!currHeadCommit.getFiles().containsKey(fileToBeRemoveName) && !currStage.getFilesChanged().containsKey(fileToBeRemoveName)){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if(currHeadCommit.getFiles().containsKey(fileToBeRemoveName)){
            File fileToRemove = join(CWD, fileToBeRemoveName);
            fileToRemove.delete();
        }
        currStage.addRemovedFile(fileToBeRemoveName);
        currStage.saveStage();
    }

    public static void commit(String massage){
        //讀取stage
        Stage currStage = Stage.getStage();
        if(currStage.getFilesChanged().isEmpty() && currStage.getFilesRemoved().isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        //讀取branch, parent的commit
        Branch currBranch = getHEADBranchFromFile();
        String parentHash = currBranch.getHead();
        Commit oldCommit = Commit.getCommitByHash(parentHash);
        //初始化新commit並將其更新後存入檔案
        Map<String, String> oldFiles = oldCommit.getFiles();
        try{
            Commit currCommit = new Commit(parentHash, massage, oldFiles);
            currCommit.updateCommit(parentHash, currStage);
            currBranch.setHeadByCommitObj(currCommit);
            currCommit.saveCommit();

        }catch (Exception e){
            e.printStackTrace();
        }
        //把stage清空
        currStage.clear();
        //儲存branch
        currBranch.saveBranch();
        //儲存stage
        currStage.saveStage();
    }

    public static void checkOutBranch(String branchName){
        Branch branch = Branch.loadBranchByName(branchName);
        Stage currStage = Stage.getStage();

        //如果branch不存在或已經是當前HEAD，或是CWD中有未追蹤檔案，報錯。
        if(branch == null){
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        Commit branchHeadCommit = Commit.getCommitByHash(branch.getHead());

        if(branch.getName().equals(getHEADBranchFromFile().getName())){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        else if(willOverwriteUntrackedFile(branchHeadCommit)){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        //更新branch檔案
        setHEADByBranchName(branch.getName());


        //將檔案寫入CWD
        for(Map.Entry<String, String> entry : branchHeadCommit.getFiles().entrySet()){
            String fileName = entry.getKey();
            String blobHash = entry.getValue();

            Blob blob = readObject(join(Blob.BLOB_DIR, blobHash), Blob.class);
            String fileContent = blob.getContent();

            writeContents(join(CWD, fileName), fileContent);
        }

        //刪除新branch headCommit 中沒有的檔案
        for(String fileName : plainFilenamesIn(CWD)){
            if(!branchHeadCommit.getFiles().containsKey(fileName)){
                join(CWD, fileName).delete();
            }
        }

        //清空暫存區
        currStage.clear();
        currStage.saveStage();
    }

    public static void checkOutFile(String fileName){
        String headCommit = getHEADBranchFromFile().getHead();
        checkOutCommit(headCommit, fileName);
    }

    public static void checkOutCommit(String commitHash, String fileName){
        Commit commitToCheckFrom = Commit.getCommitByHash(commitHash);
        Stage currStage = Stage.getStage();
        //如果所追蹤檔案有更改在stage中尚未提交，或是最新的commit中沒有追蹤這個檔案，報錯
        if(commitToCheckFrom == null){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        else if(!commitToCheckFrom.getFiles().containsKey(fileName)){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        //獲取當案
        String blobHash = commitToCheckFrom.getFiles().get(fileName);
        Blob blob = readObject(join(Blob.BLOB_DIR, blobHash), Blob.class);
        String fileContent = blob.getContent();
        //寫入檔案
        writeContents(join(CWD, fileName), fileContent);
    }

    public static void log(){
        List<String> commitHashes = plainFilenamesIn(Commit.COMMIT_DIR);
        List<Commit> commits = new ArrayList<>();
        for(String commitHash : commitHashes){
            Commit commitObject  = Commit.getCommitByHash(commitHash);
            commits.add(commitObject);
        }
        Collections.sort(commits);
        for(Commit commit : commits){
            System.out.println(commit);
        }
    }



}
