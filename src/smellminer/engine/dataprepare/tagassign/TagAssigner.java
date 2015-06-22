package smellminer.engine.dataprepare.tagassign;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.util.Pair;
import org.tmatesoft.svn.core.SVNLogEntry;
import datacollect.commits.scm.ISCMController;
import datacollect.commits.scm.definitions.LogEntry;
import datacollect.commits.scm.git.GitController;
import smellminer.engine.dataprepare.issuetracks.IIssueTrack;
import smellminer.engine.dataprepare.issuetracks.definition.Issue;
import smellminer.engine.dataprepare.issuetracks.fileissuetracker.FileBasedIssueTracker;

public class TagAssigner implements ITagAssign,Serializable
{
   private String localRepoPath="";
   private String svnURI="";
   
   
  // private ISCMController scmController;
   private IIssueTrack issueTracker=new FileBasedIssueTracker();
   
   
   public TagAssigner(String svnURI)
   {
	 this.svnURI=svnURI;
//	 scmController=new SVNController(svnURI);
   }
   
   public TagAssigner(String localRepoPath,String remoteURI)
   {
	 this.localRepoPath=localRepoPath;
	 this.svnURI=remoteURI;
//	 scmController=new SVNController(svnURI);
   }
   
   
   @Override
   public boolean isBugfix(String smellIntroCommit)
   {
	 // TODO Auto-generated method stub
	 Issue issue=issueTracker.linkIssue(smellIntroCommit);
	 if(issue!=null)
	    if(issue.type.equalsIgnoreCase("fix"))return true;
	 return false;
   }

   @Override
   public boolean isEnhance(String smellIntroCommit)
   {
	 Issue issue=issueTracker.linkIssue(smellIntroCommit);
	 if(issue!=null)
	    if(issue.type.equalsIgnoreCase("enhance"))return true;
	 return false;
   }

   @Override
   public boolean isNewFeature(String smellIntroCommit)
   {
	 Issue issue=issueTracker.linkIssue(smellIntroCommit);
	 if(issue!=null)
	    if(issue.type.equalsIgnoreCase("newfeature"))return true;
	 return false;
   }

   @Override
   public boolean isRefactory(String smellIntroCommit)
   {
	 Issue issue=issueTracker.linkIssue(smellIntroCommit);
	 if(issue!=null)
	    if(issue.type.equalsIgnoreCase("refactory"))return true;
	 return false;
   }
   
   

   @Override
   public String workingOnRelease(String smellIntroCommit)
   {
	 // TODO Auto-generated method stub
	 return "";
   }

   
   @Override
   /***
    * How long since the project start up to the smell introducing commit?
    * One day? One week? One year?
    */
   public String projStartup(String smellIntroCommit)
   {
	 ISCMController scmController = GitController.getInstance(localRepoPath, svnURI);//SVNController.getInstance(svnURI);
	 List<LogEntry> logEntries=scmController.getRevisionHistory(scmController.getEarliestRevision(), smellIntroCommit,false);
	 LogEntry startCommit=logEntries.get(0);
	 Calendar startDate = Calendar.getInstance();
      startDate.setTime(startCommit.getDate());
	 LogEntry currCommit=logEntries.get(logEntries.size()-1);
	 Calendar currDate = Calendar.getInstance();
	 currDate.setTime(currCommit.getDate());
	 long lastPeriod=currDate.getTimeInMillis()-startDate.getTimeInMillis();
	 if(lastPeriod>365*24*60*60*1000) return "more than one year";
	 else if(lastPeriod>30*24*60*60*1000) return "one year";
	 else if(lastPeriod>7*24*60*60*1000) return "one month";
	 else return "one week";
   }

   /***
    * Measure How busy a developer is when committed the code.
    * By measuring # of commits of the author in the recent one month. 
    */
   @Override
   public String workload(String revision)
   {
	 ISCMController scmController = GitController.getInstance(localRepoPath, svnURI);//SVNController.getInstance(svnURI);
	 List<LogEntry> logEntries=scmController.getRevisionHistory( scmController.getEarliestRevision(), revision,false);
	 String author=logEntries.get(logEntries.size()-1).getAuthor();
	 Date commitDate= logEntries.get(logEntries.size()-1).getDate();
	 
	 Calendar rightNow = Calendar.getInstance();
      rightNow.setTime(commitDate);
      rightNow.add(Calendar.MONTH, -1);
	 Date windowStartDate=rightNow.getTime(); //30 days ago;
	 int workload= 0;
	// String winStartRev=scmController.getDatedRevision(windowStartDate); //this function has problem!
	// logEntries=scmController.getRevisionHistory("", winStartRev, revision,false);
	 
	 for(int i=logEntries.size()-1;i>=0;i--)
	 {
	    if(logEntries.get(i).getDate().before(windowStartDate))break;
	    if(logEntries.get(i).getAuthor().equalsIgnoreCase(author))workload++;
	 }
	 
	 /*stat all workloads of all authors*/
	 Map<String,Integer> workloads=new HashMap<String,Integer>();
	 for(int i=logEntries.size()-1;i>=0;i--)
	 {
	    String auth=logEntries.get(i).getAuthor();
	    if(workloads.get(auth)==null)workloads.put(auth, 1);
	    else workloads.put(auth, workloads.get(auth)+1);
	    if(logEntries.get(i).getDate().before(windowStartDate))break;
	 }
	 Integer[] workloadstat= workloads.values().stream().sorted().toArray(Integer[]::new);
	 int n = (int) Math.round(workloadstat.length * 25 / 100);
      double q1= workloadstat[n];
      n= (int) Math.round(workloadstat.length * 75 / 100);
	 double q3=workloadstat[n];
	 
	 if(workload<q1)return "low";
	 else if(workload>q3)return "high";
	 else return "medium";
   }
  
	

   @Override
   public boolean ownership(String smellIntroCommit,Set<String> smellIntroFiles)
   {
	 ISCMController scmController = GitController.getInstance(localRepoPath, svnURI);//SVNController.getInstance(svnURI);
	 List<LogEntry> logEntries=scmController.getRevisionHistory(scmController.getEarliestRevision(), smellIntroCommit,true);
	 String author=logEntries.get(logEntries.size()-1).getAuthor();
	 
       for(String smellIntroFile:smellIntroFiles)
       {
      	 if(ownership(author,smellIntroFile,logEntries))return true;
       }
       return false;
   }
   
   
   private boolean ownership(String author,String smellIntroFile,List<LogEntry> logEntries)
   {
	 Map<String,Integer> commitStat= new HashMap<String,Integer>();
	 int commitTotal=0;
	 for(int i=0;i<logEntries.size();i++)
	 {
	    if(logEntries.get(i).getChangedPaths().contains(smellIntroFile))
	    {
		  commitTotal++;
		  String auth=logEntries.get(i).getAuthor();
		  if(commitStat.get(auth)==null)commitStat.put(auth, 1);
		  else commitStat.put(auth, commitStat.get(auth)+1);
	    }
	 }
	 if(((double)commitStat.get(author))/commitTotal>0.75)return true;
	 return false;
   }

   /**
    * Check whether the commit is in the first 3 commits of the author.
    */
   @Override
   public boolean newCommer(String smellIntroCommit)
   {
	 ISCMController scmController = GitController.getInstance(localRepoPath, svnURI);//SVNController.getInstance(svnURI);
	 List<LogEntry> logEntries=scmController.getRevisionHistory( scmController.getEarliestRevision(), smellIntroCommit,false);
	 String author=logEntries.get(logEntries.size()-1).getAuthor();
	 Set<String> commitsForAuthor=new HashSet<String>();
	 for(int i=0;i<logEntries.size();i++)
	 {
	    if(commitsForAuthor.size()>2)break;
	    if(logEntries.get(i).getAuthor()==null)continue;
	    if(logEntries.get(i).getAuthor().equals(author))
	    {
		  commitsForAuthor.add(logEntries.get(i).getRevisionID()+"");
	    }
	 }
	 if(commitsForAuthor.contains(smellIntroCommit))return true;
	 return false;
   }}
