package smellminer.definition;

import java.io.Serializable;
import java.util.List;

public class FileStat implements Serializable
{
    public String filename;
    public List<Integer> smellIntroCommits;
    public int numCommitsBeforeSmellAffected;
    public String smellDetectedRev;
   public FileStat(String filename, List<Integer> smellIntroCommits,
	    int numCommitsBeforeSmellAffected, String smellDetectedRev) {
	 super();
	 this.filename = filename;
	 this.smellIntroCommits = smellIntroCommits;
	 this.numCommitsBeforeSmellAffected = numCommitsBeforeSmellAffected;
	 this.smellDetectedRev = smellDetectedRev;
   }

    
    
}
