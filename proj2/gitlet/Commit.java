package gitlet;

// TODO: any imports you need here

import javax.sql.rowset.spi.SyncResolver;
import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable, Comparable<Commit> {

    public static final File COMMIT_DIR = join(Repository.GITLET_DIR, "commits");

    private static final long serialVersionUID = -4582844670626469235L;

    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private String timeStamp;
    /** The message of this Commit. */
    private String message;

    private String parentHash;

    private Map<String, String> files;

    private String hash;

    private SimpleDateFormat formater = new SimpleDateFormat("EEE MMM d HH:mm:ss Z");

    public Commit(String parentHash, String message, Map<String, String> files) throws Exception{
        this.message = message;
        this.timeStamp = formater.format(new Date());
        this.parentHash = parentHash;
        this.files = files;
        this.hash = SHA1();
    }

    public Commit(String parentHash, String message, Map<String, String> files, Date date) throws Exception{
        this(parentHash, message, files);
        this.timeStamp = formater.format(date);
    }

    public String getDate(){
        return this.timeStamp;
    }

    public String getMessage(){
        return this.message;
    }

    public String getParentHash(){
        return this.parentHash;
    }

    public String getHash(){
        return this.hash;
    }

    private String SHA1() throws Exception {
        StringBuffer contents = new StringBuffer();
        contents.append(timeStamp);
        contents.append(message);
        if(files != null){
            for(Map.Entry<String, String> entry : files.entrySet()){
                contents.append(entry.getKey());
                contents.append(entry.getValue());
            }
        }
        if(parentHash != null){
            contents.append(parentHash);
        }

        String contentsString = contents.toString();
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(contentsString.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void saveCommit(){
        File commitFile = Utils.join(Commit.COMMIT_DIR, this.hash);
        Utils.writeObject(commitFile, this);
    }

    public Map<String, String> getFiles(){
            return this.files;
    }

    public static Commit getCommitByHash(String commitHash){
        File commitFile = join(COMMIT_DIR, commitHash);
        if(!commitFile.exists()){
            return null;
        }
        return readObject(commitFile, Commit.class);
    }

    public void updateCommit(String parentHash, Stage stageUpdated){
        //更新parentHash
        this.parentHash = parentHash;
        //更新files
        Map<String, String> fileToCommit = stageUpdated.getFilesChanged();
        for(Map.Entry<String, String> entry : fileToCommit.entrySet()){
            String fileName = entry.getKey();
            String blobHash = entry.getValue();
            files.put(fileName, blobHash);
        }
        //更新hash
        try{
            this.hash = SHA1();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return "commit " + hash + "\n" + "date " + timeStamp + "\n" + message;
    }

    @Override
    public int compareTo(Commit commit){
        return commit.timeStamp.compareTo(timeStamp);
    }

    /* TODO: fill in the rest of this class. */
}
