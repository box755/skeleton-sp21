package gitlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
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
        //如果所追蹤檔案有更改在stage中尚未提交，或是最新的commit中沒有追蹤這個檔案，報錯
        if(commitToCheckFrom == null){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        else if(!commitToCheckFrom.getFiles().containsKey(fileName)){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        checkOutCertainFIle(commitToCheckFrom, fileName);

    }

    private static void checkOutCertainFIle(Commit commitToCheckFrom, String fileName){
        //獲取當案
        Commit currCommit = Commit.getCommitByHash(getHEADBranchFromFile().getHead());
        String blobHash = commitToCheckFrom.getFiles().get(fileName);
        //如果檔案在這個commit中不存在，將目錄中的檔案刪除
        if(commitToCheckFrom.getFiles().containsKey(fileName)){
            Blob blob = readObject(join(Blob.BLOB_DIR, blobHash), Blob.class);
            String fileContent = blob.getContent();
            //寫入檔案
            writeContents(join(CWD, fileName), fileContent);        }
        //如果檔案在這個commit中且值不為null，則將檔案寫入目錄中
        else if(!commitToCheckFrom.getFiles().containsKey(fileName) && currCommit.getFiles().containsKey(fileName)){
            join(CWD, fileName).delete();
        }
    }

    public static void log(){
        Branch currBranch = getHEADBranchFromFile();
        Commit currCommit = Commit.getCommitByHash(currBranch.getHead());
        while(currCommit != null){
            System.out.println(currCommit);
            currCommit = Commit.getCommitByHash(currCommit.getParentHash());
        }
    }

    public static void globalLog(){
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

    public static void find(String massage){
        List<String> commitHashes = plainFilenamesIn(Commit.COMMIT_DIR);
        List<Commit> commits = new ArrayList<>();
        for(String commitHash : commitHashes){
            Commit commitObject  = Commit.getCommitByHash(commitHash);
            if(commitObject.getMessage().equals(massage)){
                commits.add(commitObject);
            }
        }
        //如果是空的，跳出錯誤訊息並退出
        if(commits.isEmpty()){
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        //印出所有commit
        for(Commit commit : commits){
            System.out.println(commit.getHash());
        }
    }

    public static void status(){
        //載入暫存區和branch和headCommit
        Stage currStage = Stage.getStage();
        List<String> branches = plainFilenamesIn(Branch.BRANCH_DIR);
        Branch HEADBranch = getHEADBranchFromFile();
        String HEAD = HEADBranch.getName();
        Commit headCommit = Commit.getCommitByHash(HEADBranch.getHead());

        System.out.println("=== Branches ===");
        if(branches != null){
            for(String branchName : branches){
                if(branchName.equals(HEAD)){
                    System.out.println("*" + HEAD);
                }
                else {
                    System.out.println(branchName);
                }
            }
        }
        System.out.println();

        //印出被加入的檔案
        System.out.println("=== Staged Files ===");
        for(String stagedFilesName : currStage.getFilesChanged().keySet()){
            System.out.println(stagedFilesName);
        }
        System.out.println();

        //印出被刪除的檔案
        System.out.println("=== Removed Files ===");
        for(String removedFilesName : currStage.getFilesRemoved()){
            System.out.println(removedFilesName);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for(String fileName : headCommit.getFiles().keySet()){
            boolean isFileStaged = currStage.getFilesChanged().containsKey(fileName);
            boolean isFileExist = join(CWD, fileName).exists();

            if(!isFileExist){
                System.out.println(fileName + " (deleted)");
                continue;
            }

            String fileContents = readContentsAsString(join(CWD, fileName));

            // 檢查暫存區中的檔案內容
            if (isFileStaged) {
                Blob stagedContentsBlob = readObject(join(Blob.BLOB_DIR, currStage.getFilesChanged().get(fileName)), Blob.class);
                String stagedContents = stagedContentsBlob.getContent();
                // 如果暫存區的內容與當前工作目錄不匹配，則認為檔案被修改
                if (!fileContents.equals(stagedContents)) {
                    System.out.println(fileName + " (modified)");
                }
            } else {
                // 如果檔案沒有被暫存，則與commit中的blob進行比較
                Blob fileBlob = readObject(join(Blob.BLOB_DIR, headCommit.getFiles().get(fileName)), Blob.class);
                String fileContentsInBlob = fileBlob.getContent();
                if (!fileContents.equals(fileContentsInBlob)) {
                    System.out.println(fileName + " (modified)");
                }
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for(String fileName : plainFilenamesIn(CWD)){
            if(!headCommit.getFiles().containsKey(fileName) && !currStage.getFilesChanged().containsKey(fileName)){
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    public static void branch(String branchName){
        List<String> branches = plainFilenamesIn(Branch.BRANCH_DIR);
        if(branches == null || branches.contains(branchName)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        //將當前HEADBranch的head Commit作為新branch的head
        Branch currBranch = getHEADBranchFromFile();
        Branch newBranch = new Branch(branchName);
        Commit currCommit = Commit.getCommitByHash(currBranch.getHead());
        newBranch.setHeadByCommitObj(currCommit);
        //儲存branch
        newBranch.saveBranch();
    }

    public static void rmBranch(String branchName){
        List<String> branches = plainFilenamesIn(Branch.BRANCH_DIR);
        Branch currBranch = getHEADBranchFromFile();
        if(branches == null || !branches.contains(branchName)){
            System.out.println("branch with that name does not exist.");
            System.exit(0);
        }
        else if(currBranch.getName().equals(branchName)){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        //從branch_dir中刪除branch
        File branchToBeRemoved = join(Branch.BRANCH_DIR, branchName);
        branchToBeRemoved.delete();
    }

    public static void reset(String commitID){
        Commit commitToCheckFrom = Commit.getCommitByHash(commitID);
        if(commitToCheckFrom == null){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        //更新HEAD branch的head commit
        Branch currBranch = getHEADBranchFromFile();
        Commit commitToBeChecked = Commit.getCommitByHash(commitID);
        currBranch.setHeadByCommitObj(commitToBeChecked);
        //更新檔案
        for(String fileName : Utils.plainFilenamesIn(CWD)){
            checkOutCertainFIle(commitToCheckFrom, fileName);
        }

        //儲存branch

        currBranch.saveBranch();

    }

    public static void merge(String branchName){
        //讀取stage
        Stage currStage = Stage.getStage();
        //讀取branch和head
        Branch HEADBranch = getHEADBranchFromFile();
        Branch otherBranch = Branch.loadBranchByName(branchName);
        Commit HEADBranchHead = Commit.getCommitByHash(HEADBranch.getHead());
        Commit otherBranchHead = Commit.getCommitByHash(otherBranch.getHead());

        Commit splitPointCommit = splitPointHash(HEADBranchHead, otherBranchHead);

        //檢查錯誤
        if(!currStage.getFilesChanged().isEmpty() || !currStage.getFilesRemoved().isEmpty()){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        else if(otherBranch == null){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        else if(HEADBranch.getName().equals(otherBranch.getName())){
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        else if(willUntrackedFilesBeOverwritten(HEADBranchHead, otherBranchHead)){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        //Case 1:指定的分支指向的是分裂點，則不需合併
        if(otherBranch.getHead().equals(splitPointCommit.getHash())){
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        else if(HEADBranch.getHead().equals(splitPointCommit.getHash())){
            System.out.println("Current branch fast-forwarded.");
            checkOutBranch(otherBranch.getName());
        }
        else{
            boolean hasConflict = false;

            // 合併文件
            for (String fileName : plainFilenamesIn(CWD)) {
                String headBlobHash = HEADBranchHead.getFiles().get(fileName);
                String otherBlobHash = otherBranchHead.getFiles().get(fileName);
                String splitBlobHash = splitPointCommit.getFiles().get(fileName);

                // Case: 文件只存在於給定分支，加入並stage
                if (splitBlobHash == null && otherBlobHash != null && headBlobHash == null) {
                    checkOutCertainFIle(otherBranchHead, fileName);
                    currStage.updateStage(fileName);
                }
                // Case: 文件在給定分支被刪除，從CWD刪除並加入removed files
                else if (splitBlobHash != null && otherBlobHash == null && headBlobHash != null) {
                    join(CWD, fileName).delete();
                    currStage.addRemovedFile(fileName);
                }
                // Case: 當前分支沒有修改，給定分支有修改，使用給定分支的版本
                else if (splitBlobHash != null && headBlobHash.equals(splitBlobHash) && !otherBlobHash.equals(splitBlobHash)) {
                    checkOutCertainFIle(otherBranchHead, fileName);
                    currStage.updateStage(fileName);
                }
                // Case: 文件在兩邊有不同的修改，處理衝突
                else if (headBlobHash != null && otherBlobHash != null && !headBlobHash.equals(otherBlobHash)) {
                    handleConflict(fileName, headBlobHash, otherBlobHash);
                    hasConflict = true;
                }
            }

            // 處理新文件和文件刪除情況
            for (String fileName : plainFilenamesIn(CWD)) {
                if (!HEADBranchHead.getFiles().containsKey(fileName) && otherBranchHead.getFiles().containsKey(fileName)) {
                    checkOutCertainFIle(otherBranchHead, fileName);
                    currStage.updateStage(fileName);
                } else if (HEADBranchHead.getFiles().containsKey(fileName) && !otherBranchHead.getFiles().containsKey(fileName)) {
                    join(CWD, fileName).delete();
                    currStage.addRemovedFile(fileName);
                }
            }
            // 提交合併
            String mergeMessage = "Merged " + branchName + " into " + HEADBranch.getName() + ".";
            mergeCommit(otherBranch.getHead(), mergeMessage);

            // 如果有衝突，顯示衝突訊息
            if (hasConflict) {
                System.out.println("Encountered a merge conflict.");
            }



        }



    }

    private static Commit splitPointHash(Commit HEADBranchHead, Commit otherBranchHead){
            Set<String> commitHistory = new HashSet<>();
            while(HEADBranchHead != null){
                commitHistory.add(HEADBranchHead.getHash());
                HEADBranchHead = Commit.getCommitByHash(HEADBranchHead.getParentHash());
            }
            while(otherBranchHead != null){
                if(commitHistory.contains(otherBranchHead.getHash())){
                    return otherBranchHead;
                }
                otherBranchHead = Commit.getCommitByHash(otherBranchHead.getParentHash());
            }
            return null;
    }

    private static boolean willUntrackedFilesBeOverwritten(Commit currentCommit, Commit targetCommit) {
        // 讀取暫存區和commit的檔案
        List<String> workingFiles = Utils.plainFilenamesIn(Repository.CWD);
        Map<String, String> currentTrackedFiles = currentCommit.getFiles();
        Map<String, String> targetTrackedFiles = targetCommit.getFiles();

        // 檢查工作目錄中的未追蹤檔案
        for (String fileName : workingFiles) {
            boolean isUntracked = !currentTrackedFiles.containsKey(fileName) && !Stage.getStage().getFilesChanged().containsKey(fileName);
            if (isUntracked && targetTrackedFiles.containsKey(fileName)) {
                return true;
            }
        }
        return false;
    }

    private static void mergeCommit(String otherBranchHeadHash, String massage){
        //讀取stage
        Stage currStage = Stage.getStage();

        //讀取branch, parent的commit
        Branch currBranch = getHEADBranchFromFile();
        String parentHash = currBranch.getHead();
        Commit oldCommit = Commit.getCommitByHash(parentHash);
        //初始化新commit並將其更新後存入檔案
        Map<String, String> oldFiles = oldCommit.getFiles();
        try{
            Commit currCommit = new Commit(parentHash, otherBranchHeadHash, massage, oldFiles);
            currCommit.updateCommit(parentHash,  currStage);
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

    private static void handleConflict(String fileName, String headBlobHash, String otherBlobHash) {
        Blob headBlob = readObject(join(Blob.BLOB_DIR, headBlobHash), Blob.class);
        Blob otherBlob = readObject(join(Blob.BLOB_DIR, otherBlobHash), Blob.class);

        String headContent = headBlob != null ? headBlob.getContent() : "";
        String otherContent = otherBlob != null ? otherBlob.getContent() : "";

        String conflictContent = "<<<<<<< HEAD\n" + headContent + "\n=======\n" + otherContent + "\n>>>>>>>\n";
        writeContents(join(CWD, fileName), conflictContent);

        Stage.getStage().updateStage(fileName);
    }

}