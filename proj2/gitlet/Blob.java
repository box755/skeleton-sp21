package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static gitlet.Utils.join;

public class Blob implements Serializable {

    public static final File BLOB_DIR = join(Repository.GITLET_DIR, "blobs");

    private String hash;
    private String content;
    public Blob(String content){
        if(content == null){
            throw new IllegalArgumentException("Content can not be null");
        }
        this.content = content;
        try{
            this.hash = SHA1();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getHash(){
        return this.hash;
    }

    public String getContent(){
        return this.content;
    }

    private String SHA1() throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
