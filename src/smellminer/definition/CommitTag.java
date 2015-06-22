package smellminer.definition;

import java.io.Serializable;

public class CommitTag implements Serializable
{
    public String revision;
    
    public boolean bugfix;
    public boolean enhance;
    public boolean newfeature;
    public boolean refactory;
    
    public String workOnRelease;
    public String projStartup;
    
    public String workload;
    public boolean ownership;
    public boolean newcomer;
    
    
   public CommitTag(String revision, boolean bugfix, boolean enhance, boolean newfeature,
	    boolean refactory, String workOnRelease, String projStartup, String workload,
	    boolean ownership, boolean newcomer) {
	 super();
	 this.revision = revision;
	 this.bugfix = bugfix;
	 this.enhance = enhance;
	 this.newfeature = newfeature;
	 this.refactory = refactory;
	 this.workOnRelease = workOnRelease;
	 this.projStartup = projStartup;
	 this.workload = workload;
	 this.ownership = ownership;
	 this.newcomer = newcomer;
   }
   
   public String toString()
   {
	 return "bugfix:"+bugfix+" enhance:"+enhance+" newfeature:"+newfeature+"refactory"+refactory
		  +" workOnRelease:"+workOnRelease+" projStartup:"+projStartup+" workload:"+workload
		  +" ownership:"+ownership+" newcomer:"+newcomer;
   }

}
