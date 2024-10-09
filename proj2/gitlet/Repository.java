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
        currenStage.getFilesRemoved().remove(NameOfFileToStage);
        currenStage.updateStage(NameOfFileToStage);          //更新stage的狀態。
        currenStage.saveStage();                                   //更新後的stage存到stage檔案中。
    }

    public static void rm(String fileToBeRemoveName) {
        Branch currBranch = getHEADBranchFromFile();
        Commit currHeadCommit = Commit.getCommitByHash(currBranch.getHead());
        Stage currStage = Stage.getStage();
        File fileToRemove = join(CWD, fileToBeRemoveName);

        // 1. 檢查檔案是否存在於當前提交或暫存區中
        boolean isTrackedInCommit = currHeadCommit.getFiles().containsKey(fileToBeRemoveName);
        boolean isStaged = currStage.getFilesChanged().containsKey(fileToBeRemoveName);

        if (!isTrackedInCommit && !isStaged) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        // 2. 如果檔案已經在提交中被追蹤，刪除檔案並標記為 "Removed"
        if (isTrackedInCommit) {
            if (fileToRemove.exists()) {
                fileToRemove.delete();  // 刪除工作目錄中的檔案
            }
            currStage.addRemovedFile(fileToBeRemoveName);  // 標記為待刪除
        }

        // 3. 如果檔案只是暫存區中的變更，從暫存區移除它
        if (isStaged) {
            currStage.getFilesChanged().remove(fileToBeRemoveName);  // 從暫存區中移除變更
        }

        // 4. 最後保存當前stage狀態
        currStage.saveStage();
    }

    public static void commit(String massage){
        //讀取stage
        Stage currStage = Stage.getStage();
        if(massage.isEmpty()){
            System.out.println("Please enter a commit massage.");
            System.exit(0);
        }
        else if(currStage.getFilesChanged().isEmpty() && currStage.getFilesRemoved().isEmpty()){
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
        Commit commitToCheckFrom = Commit.getCommitByHash(findFullCommitHash(commitHash));

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

    private static String findFullCommitHash(String abbreviatedHash) {
        List<String> commitHashes = plainFilenamesIn(Commit.COMMIT_DIR); // 取得所有 commit ID

        // 遍歷所有 commit ID 並找到匹配的完整 ID
        String fullCommitHash = null;
        for (String hash : commitHashes) {
            if (hash.startsWith(abbreviatedHash)) {
                if (fullCommitHash != null) {
                    // 發現多個 ID
                    System.out.println("Ambiguous commit ID.");
                    return fullCommitHash;
                }
                fullCommitHash = hash;
            }
        }

        return fullCommitHash;
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
            boolean isFileRemoved = currStage.getFilesRemoved().contains(fileName);
            boolean isFileStaged = currStage.getFilesChanged().containsKey(fileName);
            boolean isFileExist = join(CWD, fileName).exists();
            //檔案不存在時，可能是被手動刪除，也可能是被rm
            if(!isFileExist){
                if(!isFileRemoved){
                    System.out.println(fileName + " (deleted)");
                }
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
        Commit commitToCheckFrom = Commit.getCommitByHash(findFullCommitHash(commitID));
        if(commitToCheckFrom == null){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        // 檢查是否有未追蹤的檔案會被覆蓋
        if (willUntrackedFilesBeOverwritten(Commit.getCommitByHash(getHEADBranchFromFile().getHead()), commitToCheckFrom)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        //更新HEAD branch的head commit
        Stage currStage = Stage.getStage();
        Branch currBranch = getHEADBranchFromFile();
        Commit commitToBeChecked = Commit.getCommitByHash(commitID);
        currBranch.setHeadByCommitObj(commitToBeChecked);
        //更新檔案，把工作目錄以及要check的的檔案都checkout一遍
        for(String fileName : commitToCheckFrom.getFiles().keySet()){
            checkOutCertainFIle(commitToCheckFrom, fileName);
        }
        for(String fileName : plainFilenamesIn(CWD)){
            checkOutCertainFIle(commitToCheckFrom, fileName);
        }
        //清空暫存區
        currStage.clear();


        //儲存branch stage
        currStage.saveStage();
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


            // 開始合併文件
            Set<String> allFiles = new HashSet<>();
            allFiles.addAll(HEADBranchHead.getFiles().keySet());
            allFiles.addAll(otherBranchHead.getFiles().keySet());
            allFiles.addAll(splitPointCommit.getFiles().keySet());

            for (String fileName : allFiles) {
                String splitBlobHash = splitPointCommit.getFiles().get(fileName);
                String currentBlobHash = HEADBranchHead.getFiles().get(fileName);
                String givenBlobHash = otherBranchHead.getFiles().get(fileName);

                // Case 1: 文件在給定分支修改了，當前分支未修改
                if (splitBlobHash != null && givenBlobHash != null && currentBlobHash != null
                        && splitBlobHash.equals(currentBlobHash) && !splitBlobHash.equals(givenBlobHash)) {
                    checkOutCertainFIle(otherBranchHead, fileName);
                    currStage.updateStage(fileName);
                }
                // Case 2: 文件只存在於給定分支
                else if (splitBlobHash == null && givenBlobHash != null && currentBlobHash == null) {
                    checkOutCertainFIle(otherBranchHead, fileName);
                    currStage.updateStage(fileName);
                }
                // Case 3: 文件只存在於當前分支，保持不變
                else if (splitBlobHash == null && currentBlobHash != null && givenBlobHash == null) {
                    // 保持不變
                }
                // Case 4: 文件在當前分支被刪除，給定分支沒有修改，從CWD刪除
                else if (splitBlobHash != null && givenBlobHash == null && splitBlobHash.equals(currentBlobHash)) {
                    join(CWD, fileName).delete();
                    currStage.addRemovedFile(fileName);
                }
                // Case 5: 文件在兩個分支中以不同方式修改，衝突
                else if (currentBlobHash != null && givenBlobHash != null && !currentBlobHash.equals(givenBlobHash)) {
                    handleConflict(fileName, currentBlobHash, givenBlobHash);
                    hasConflict = true;
                }
            }

            // 如果有衝突，則通知使用者
            if (hasConflict) {
                System.out.println("Encountered a merge conflict.");
            } else {
                // 如果沒有衝突，則提交合併
                String mergeMessage = "Merged " + branchName + " into " + HEADBranch.getName() + ".";
                mergeCommit(HEADBranch.getHead(), mergeMessage);
            }

            currStage.saveStage();
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