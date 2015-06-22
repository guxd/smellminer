package smellminer.engine.smelldetect;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import smellminer.definition.FileSnapshot;
import smellminer.definition.FileStat;

public interface ISmellDetect
{
   public void defineSmellRules();
   
   /***
    * Detect smell using Decor
    * @param sourcefile
    * @return smell type. e.g., Bob
    */
   public Set<String> detectSmellTypes();
    
   /***
    * Given statistics (revision history) of a file, 
    * decide which revision is the smell-introducing revision.
    * @param filestat
    * @return
    */
   public String findSmellIntroRevision(Map<String,FileSnapshot> filestat,double regr_slop);
   
}
