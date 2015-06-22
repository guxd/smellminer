package smellminer.engine.dataprepare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.Pair;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import datacollect.commits.scm.ISCMController;
import datacollect.commits.scm.definitions.LogEntry;
import datacollect.commits.scm.git.GitController;
import smellminer.definition.CommitTag;
import smellminer.definition.FileSnapshot;
import smellminer.definition.FileStat;
import smellminer.definition.Metrics;
import smellminer.engine.dataprepare.codemetrics.IMetricExtract;
import smellminer.engine.dataprepare.codemetrics.MetricExtractor;
import smellminer.engine.dataprepare.tagassign.ITagAssign;
import smellminer.engine.dataprepare.tagassign.TagAssigner;
import smellminer.engine.smellanalyze.Regression;
import smellminer.engine.smelldetect.ISmellDetect;
import smellminer.engine.smelldetect.decor.Decor;

@DefaultSerializer(JavaSerializer.class)
public class DataPrepare implements IDataPrepare,Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = 8363745874521428863L;
   /**
    * parameters
    */
   String localRepoPath="";
   String svnURI = "";
   String startRevision = "1682021";
   String lastRevision = "1680000";
   String projType="java";//csharp
   /**
    * Link to SCM tool such as SVN, Git
    */
  // private ISCMController scmController;
   
   /**
    * Link to metric extractor
    */
   private IMetricExtract metricExtractor;
   /**
    * Link to smell detector
    */
   private ISmellDetect smellDetector;
   /**
    * Link to a tag assigner, to assign tags to smell-introducing commits.
    */
   private ITagAssign tagAssigner;
   
   
   
   /***
    * Data
    */
   private Map<String, Integer> globalRevisionOrders = new HashMap<String, Integer>();
   Map<String, Map<Integer, FileSnapshot>> FCtable = new HashMap<String, Map<Integer, FileSnapshot>>();
   Map<String,FileStat> fileStats=new HashMap<String,FileStat>();
   
   
   
   public DataPrepare() {
	 super();
	// scmController = new SVNController(svnURI);
	// smellDetector = new Decor();
	 metricExtractor = new MetricExtractor();
	 //tagAssigner = new TagAssigner(uri);
   }

   public DataPrepare(String localRepoPath,String uri, String startRev, String endRev,String projType) {
	 this.localRepoPath=localRepoPath;
	 this.svnURI = uri;
	 this.startRevision = startRev;
	 this.lastRevision = endRev;
	 this.projType=projType;
	// scmController = new SVNController(svnURI);
	// smellDetector = new Decor();
	 metricExtractor = new MetricExtractor();
	 tagAssigner = new TagAssigner(uri);
   }

   /*
    * public DataPrepare(ISCMController scmController, IMetricExtract
    * metricExtractor, ISmellDetect smellDetector, ITagAssign tagAssigner) {
    * super(); this.scmController = scmController; this.metricExtractor =
    * metricExtractor; this.smellDetector = smellDetector; this.tagAssigner =
    * tagAssigner; }
    */
   public static void main(String[] args)
   {
	 // TODO Auto-generated method stub
   }

   @Override
   public Map<String, Map<Integer, FileSnapshot>> collectData(String uri)
   {
	 ISCMController scmController = GitController.getInstance(localRepoPath, svnURI);//SVNController.getInstance(svnURI);
	 List<LogEntry> logEntries = scmController.getRevisionHistory(startRevision,
		  lastRevision,true);
	 Set<String> filelist = new HashSet<String>();
	 int revOrder=0; boolean validCommit=false;
	 for (int r = 0; r < logEntries.size(); r++)
	 {
	    LogEntry logEntry = logEntries.get(r);
	    String revision = logEntry.getRevisionID() + "";
	    String datetime = logEntry.getDate().toString();
	   
	    Set<String> changedPathsSet = logEntry.getChangedPaths();
	    for (Iterator<String> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();)
	    {
		  
		  String path = changedPaths.next();
		  if (!path.endsWith(".java"))continue;// Exclude non-java files
//		  if (!path.contains("/trunk"))continue;// Exclude non trunk files
	      System.out.println("------------------------");
		  System.out.println(datetime+" r" + revision + " ");
		  System.out.println(path);
		  System.out.println();
		  String filecontent;
		  try
		  {
			filecontent = scmController.getFileContent(path, revision);
		  } catch (SVNException e)
		  {
			   System.err.println("Failed to get content of file:"+path);
			   continue;
		  }
		   //System.out.println(content);
		  
		
		  
			// create a temp path and write the file content to a
			// newbuilt java file
			String tmpfolder = "/tmp";
			File tmpfold = new File(tmpfolder);
			File tmpsrcfile=null;
			try
			{
			   if(!tmpfold.exists())tmpfold.mkdir();
			   tmpsrcfile=new File("/tmp/tmpsrc.java");		
			   BufferedWriter bw = new BufferedWriter(new FileWriter(tmpsrcfile));
			   bw.write(filecontent);
			   bw.close();
			} catch (IOException e)
			{
			   e.printStackTrace();
			} 
			
			Decor decor=null;
			try{decor=new Decor(projType,"/tmp");}
			catch (padl.kernel.exception.ModelDeclarationException e)
			 {
			    System.err.println("Failed to build PADL model for src path:" + path
					+"\n padl.kernel.exception.ModelDeclarationException");
			    continue;
			 }
			  filelist.add(path);
			  
			//Set<String> smells = smellDetector.detectSmellTypes("/tmp");
			Set<String> smells=decor.detectSmellTypes();
			for(String smell:smells)System.out.print(smell+" ");System.out.println();
			//Metrics metrics = metricExtractor.extractMetrics(tmpsrcfile);
			Metrics metrics=decor.extractMetrics(tmpsrcfile);
			System.out.println(metrics);
			
			validCommit=true;
			
		  if (FCtable.get(path) == null)
		  {
			FCtable.put(path, new HashMap<Integer, FileSnapshot>());
			FileSnapshot snap = new FileSnapshot(path, revision, 2, smells, metrics);
			FCtable.get(path).put(revOrder, snap);
		  }
		  else
		  {
			FileSnapshot snap = new FileSnapshot(path, revision, 1, smells, metrics);
			FCtable.get(path).put(revOrder, snap);
		  }
	    }
	    if(validCommit)globalRevisionOrders.put(revision, revOrder++);
	 }
	 return FCtable;
   }

   /*
    * @Override public List<FileStat> prepareData(Map<String, Map<String,
    * FileSnapshot>> rawdata) { List<FileStat> filestats = new
    * ArrayList<FileStat>(); Set<String> filelist = rawdata.keySet();
    * Iterator<String> it_file = filelist.iterator(); while (it_file.hasNext())
    * { String filepath = it_file.next(); List<String> revlist =
    * rawdata.get(filepath).keySet().stream() .collect(Collectors.toList());
    * String smellIntroCommit =
    * smellDetector.findSmellIntroRevision(rawdata.get(filepath), regr_slop);
    * FileStat stat = new FileStat(filepath, revlist, revlist.size() - 1,
    * smellIntroCommit); filestats.add(stat); } return filestats; }
    */
   
   /**
    * get Number of commits before a smell is detected for a file.
    * If a file is detected to be smell, return the number of commits,
    * if a file is not smell at all, return -1.
    */
   public int getNumCommitBeforeSmell(String filepath, String smelltype)
   {
	 Map<Integer, FileSnapshot> fileinfos = FCtable.get(filepath);
	 int numcommitBeforeSmell = -1;
	 int count = 0;
	 List<Integer> revOrders = fileinfos.keySet().stream().sorted().collect(Collectors.toList());
	 for (int revOrder : revOrders)
	 {
	    FileSnapshot fileinfo = fileinfos.get(revOrder);
	    if (fileinfo.smells.contains(smelltype))
	    {
		  numcommitBeforeSmell = count;
		  break;
	    }
	    count++;
	 }
	 return numcommitBeforeSmell;
   }



   /***
    * prepare data to analyze commit-metric regression line.
    * Find files (not) affected by a smell and return their metric-commit pairs.
    * @param filepath
    * @param metric
    * @param smelltype
    * @param include - return data for files affected by that smell or not affected by that smell 
    * @param revNum - get the first revNum revisions' data for a file.
    *                 (only useful for files that are not affected by the smell)
    * @return
    */
   // problem1: distinguish smell type, need to return data for files without a smell
	//problem2: x-axis data is global revOrder. should it be orders for a specific file?
	//  problem3: need consider regressions that has only one data point
   public List<Pair<Integer, Double>> getRegressionData(String filepath, String metric,
	    String smelltype, int revNum)
   {
	 List<Pair<Integer, Double>> regrData = new ArrayList<Pair<Integer, Double>>();
	 Map<Integer, FileSnapshot> fileinfos = FCtable.get(filepath);

	 List<Integer> globalRevOrdersForFile = fileinfos.keySet().stream().sorted().collect(Collectors.toList());
	 if(revNum>globalRevOrdersForFile.size())revNum=globalRevOrdersForFile.size();
	 for (int fileRevOrder=0;fileRevOrder<revNum;fileRevOrder++)
	 {
	    int globalRevOrder=globalRevOrdersForFile.get(fileRevOrder);
	    FileSnapshot fileinfo = fileinfos.get(globalRevOrder);
	    regrData.add(new Pair<Integer, Double>(fileRevOrder, fileinfo.metrics.getMetricValue(metric)));
	 }
	 return regrData;
   }

   /**
    * Find smell introducing commits based on RQ1 by comparing slops
    * 
    * @param filepath
    * @param smelltype
    * @param metric
    * @param metric_smell_related  According to RQ1 results, whether the metric is significantly related to the smell introducing. 
    * @return
    */
   public List<String> findSmellIntroCommit(String filepath, String smelltype, String metric,boolean metric_smell_related)
   {
	 List<String> smellIntroCommits = new ArrayList<String>();
	 Map<Integer, FileSnapshot> fileinfos = FCtable.get(filepath);
	 if (fileinfos.isEmpty()) return smellIntroCommits;
	 List<Integer> commits = fileinfos.keySet().stream().sorted().collect(Collectors.toList());
	 if (fileinfos.get(commits.get(0)).smells.contains(smelltype))
	 {
	    smellIntroCommits.add(fileinfos.get(commits.get(0)).revision);
	 }
	 else if (commits.size() < 2)
	 {}
	 else if(metric_smell_related)
	 {
	    int numCommitsBeforeSmell = this.getNumCommitBeforeSmell(filepath, smelltype);
	    if(numCommitsBeforeSmell<0)//the file is not affected by the smell at all, then return empty.
		  return smellIntroCommits;
	    List<Pair<Integer, Double>> regr_data = getRegressionData(filepath, metric, smelltype,numCommitsBeforeSmell);
	    double slop = new Regression().regression(regr_data);
	    for (int i = 1; i < commits.size(); i++)
	    {
		  int prevCommit = commits.get(i - 1);
		  int currCommit = commits.get(i);
		  double prevMetricValue = fileinfos.get(prevCommit).metrics.getMetricValue(metric);
		  double currMetricValue = fileinfos.get(currCommit).metrics.getMetricValue(metric);
		  double slop_i = (currMetricValue - prevMetricValue);// /(currCommit-prevCommit);
		  if (Math.abs(slop_i) > Math.abs(slop))
			smellIntroCommits.add(fileinfos.get(currCommit).revision);
	    }
	 }
	 return smellIntroCommits;
   }
   


   
   
   
   
   public List<CommitTag> tagCommits(Map<String,Set<String>> smellIntroCommits)
   {
	 // TODO Auto-generated method stub
	 List<CommitTag> tags = new ArrayList<CommitTag>();
	 smellIntroCommits.keySet().forEach(smellIntroCommit -> {
	    boolean bugfix = false;
	    boolean enhance = false;
	    boolean newfeature = false;
	    boolean refactory = false;
	    String workOnRelease = null;
	    String projStartup = tagAssigner.projStartup(smellIntroCommit);
	    System.err.println("tag startup");
	    String workload = tagAssigner.workload(smellIntroCommit);
	    System.err.println("tag workload");
	    // ###################################################################
	    // //find smell introducing files
	    // ???? if smellIntroFile is null then??
		  Set<String> smellIntroFiles=smellIntroCommits.get(smellIntroCommit);
		  boolean ownership = tagAssigner.ownership(smellIntroCommit, smellIntroFiles);
		  System.err.println("tag ownership");
		  boolean newcomer = tagAssigner.newCommer(smellIntroCommit);
		  System.err.println("tag newcomer");
		  CommitTag tag = new CommitTag(smellIntroCommit, bugfix, enhance, newfeature, refactory,
			   workOnRelease, projStartup, workload, ownership, newcomer);
		  tags.add(tag);
	    });
	 return tags;
   }
    
   public Map<String, Map<Integer, FileSnapshot>> getRawData()
   {
	 return FCtable;
   }
   

}
