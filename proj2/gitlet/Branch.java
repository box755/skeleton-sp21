package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

public class Branch implements Serializable {

    private static final long serialVersionUID = -4259134256506733164L;

    public static final File BRANCH_DIR = join(Repository.GITLET_DIR, "branches");

    private final String name;
    private String head;
    public Branch(String name){
        this.name = name;
    }

    public static void setBranch() throws Exception{
        Commit initialCommit = new Commit(null, "initial commit", new HashMap<>(), new Date(0));
        Branch masterBranch = new Branch("Master");
        masterBranch.setHeadByCommitObj(initialCommit);
        Repository.setHEADByBranchName(masterBranch.getName());
        initialCommit.saveCommit();
        masterBranch.saveBranch();
    }

    public void setHeadByCommitObj(Commit commit){
        this.head = commit.getHash();
    }

    public String getHead(){
        return head;
    }

    public String getName(){
        return name;
    }

    public static Branch loadBranchByName(String branchName){
        File branchFile = join(BRANCH_DIR, branchName);
        if(!branchFile.exists()){
            return null;
        }
        Branch branchLoaded = readObject(branchFile, Branch.class);
        return branchLoaded;
    }


    public void saveBranch(){
        writeObject(join(Branch.BRANCH_DIR, this.name), this);
    }
}
