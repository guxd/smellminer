package smellminer.engine.dataprepare;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.util.Pair;
import smellminer.definition.CommitTag;
import smellminer.definition.FileSnapshot;
import smellminer.definition.FileStat;

public interface IDataPrepare
{
   /***
    * Step1: Prepare raw commit snapshots from repository 
    * @param uri
    * @return a 2-D table of file snapshots, rows for files and columns for revisions.
    */
    public Map<String,Map<Integer,FileSnapshot>> collectData(String uri); 
    
    
    
   // public List<FileStat> prepareData(Map<String,Map<String,FileSnapshot>> rawdata);
    public int getNumCommitBeforeSmell(String filepath, String smelltype);
    public List<Pair<Integer, Double>> getRegressionData(String filepath, String metric,
		    String smelltype,int revNum);
  //  public List<CommitTag> tagCommits(List<FileStat> filestats);
    
    public Map<String, Map<Integer, FileSnapshot>> getRawData();



   public List<String> findSmellIntroCommit(String filepath, String smelltype, String metric, boolean metric_smell_related);

  // public Map<String, Set<String>> findSmellIntroCommits(boolean[][] smellRelatedMetrics);



   public List<CommitTag> tagCommits(Map<String, Set<String>> smellIntroCommits);

}
