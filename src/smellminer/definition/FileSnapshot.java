package smellminer.definition;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class FileSnapshot implements Serializable
{
   public String filename;
   public String revision;
    public int action;//2=add, 1=modify, 0=delete
    public Set<String> smells;
    public Metrics metrics;
    public FileSnapshot(String filename,String revision,int action, Set<String> smells, Metrics metrics) {
	 super();
	 this.filename=filename;
	 this.revision=revision;
	 this.action = action;
	 this.smells = smells;
	 this.metrics = metrics;
   }
   
    
}
