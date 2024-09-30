package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    //檔案在序列化的時候，會在檔案中存入serialVersionUID（有點像hashcode)，並且在反序列化時會檢查UID是否相同。
    //因此在stage當案被更改過後，再反序列化會出現InvalidClassException: local class incompatible。
    //因此我們要預先設定serialVersionUID，讓他固定，這樣不管檔案怎麼改，都可以反序列化。
    private static final long serialVersionUID = 1523184094071183839L;

    public static final File STAGEFILE = join(Repository.GITLET_DIR, "stage");

    private Map<String, String> filesChanged;
    private ArrayList<String> filesRemoved;

    public Stage() {
        this.filesChanged = new HashMap<>();
        this.filesRemoved = new ArrayList<>();
    }

    public static void setStage() {
        Stage initialStage = new Stage();
        Utils.writeObject(STAGEFILE, initialStage);
    }

    public void addRemovedFile(String fileName){
        if(filesChanged.containsKey(fileName)){
            filesChanged.remove(fileName);
        }
        filesRemoved.add(fileName);
    }
    public void updateStage(String fileName) {
        // 獲取當前branch，並獲取其head commit的檔案
        //Branch currentBranch = Utils.readObject(join(Branch.BRANCH_DIR, "Master"), Branch.class);
        Branch currBranch = Repository.getHEADBranchFromFile();
        Commit headCommit = Utils.readObject(join(Commit.COMMIT_DIR, currBranch.getHead()), Commit.class);
        Map<String, String> headFiles = headCommit.getFiles();

        // 獲取要track的檔案的內容，並轉成blob
        File fileToBeChecked = join(Repository.CWD, fileName);
        String fileContents = readContentsAsString(fileToBeChecked);
        Blob newBlob = new Blob(fileContents);
        File newBlobFile = join(Blob.BLOB_DIR, newBlob.getHash());

        // 如果 Blob 文件不存在，保存新的 Blob
        if (!newBlobFile.exists()) {
            writeObject(newBlobFile, newBlob);
        }


        // 如果文件在 HEAD 中不存在，或文件内容不同，则更新 filesChanged
        if (!headFiles.containsKey(fileName) || !headFiles.get(fileName).equals(newBlob.getHash())) {
            filesChanged.put(fileName, newBlob.getHash());
        }
    }

    public void saveStage(){
        writeObject(Stage.STAGEFILE, this);
    }

    public Map<String, String> getFilesChanged() {
        return filesChanged;
    }

    public ArrayList<String> getFilesRemoved(){
        return filesRemoved;
    }

    public static Stage getStage(){
        return readObject(STAGEFILE, Stage.class);
    }
    public void clear() {
        filesChanged.clear();
        filesRemoved.clear();
    }
}