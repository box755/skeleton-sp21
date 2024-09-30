package gitlet;
import org.junit.Test;

import java.io.File;

import static gitlet.Utils.*;

public class CommitTest {
    @Test
    public void testInit() throws Exception{
        Repository.init();
    }

    @Test
    public void testAdd() throws Exception{
        Repository.add("test.txt");
    }

    @Test
    public void testRm() throws Exception{
        Repository.rm("test.txt");
    }


    @Test
    public void testCommit() throws Exception{
        Repository.commit("Third Commit.");
    }

    @Test
    public void printBlob() throws Exception{
        File blob = join(Blob.BLOB_DIR, "94bcb68e447067c5e0d8283383b89cac60410b19");
        Blob blobRead = readObject(blob, Blob.class);
        System.out.println(blobRead.getContent());
    }

    @Test
    public void printStage() throws Exception{
        Stage stage = readObject(Stage.STAGEFILE, Stage.class);
        System.out.println(stage.getFilesChanged());
    }

    @Test
    public void printHEAD() throws Exception{
        Branch HEAD = Repository.getHEADBranchFromFile();
        System.out.println(HEAD.getName());

    }

    @Test
    public void printHeadCommitFiles() throws Exception{
        Branch HEAD = Repository.getHEADBranchFromFile();
        Commit headCommit = Commit.getCommitByHash(HEAD.getHead());
        System.out.println(headCommit.getFiles());

    }



    @Test
    public void checkoutCommitTest() throws Exception{
        Repository.checkOutCommit("65ecbd2795d9d6e05d3a952ee1ad239760c9196a", "test.txt");

    }

    @Test
    public void checkoutTest() throws Exception{
        Repository.checkOutFile("test.txt");
    }

    @Test
    public void printTestTxt() throws Exception{
        File testFile = join(Repository.CWD, "test.txt");
        String content = readContentsAsString(testFile);
        System.out.println(content);
    }


    @Test
    public void printFiees() throws Exception{
        for(String fileName : Utils.plainFilenamesIn(Repository.CWD)){
            System.out.println(fileName);
        }

    }

    @Test
    public void logTest() throws Exception{
        Repository.log();

    }

}