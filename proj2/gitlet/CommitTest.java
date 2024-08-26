package gitlet;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommitTest {
    @Test
    public void testtt() throws Exception{
        Commit commitTest = new Commit(null, "TESTTT", null);
        String hash = commitTest.getHash();
        System.out.println(hash);


    }

}