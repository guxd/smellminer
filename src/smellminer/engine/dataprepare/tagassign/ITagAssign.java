package smellminer.engine.dataprepare.tagassign;

import java.util.Set;

public interface ITagAssign
{
   public boolean isBugfix(String smellIntroCommit);

   public boolean isEnhance(String smellIntroCommit);

   public boolean isNewFeature(String smellIntroCommit);

   public boolean isRefactory(String smellIntroCommit);

   public String workingOnRelease(String smellIntroCommit);

   public String projStartup(String smellIntroCommit);

   public String workload(String revision);

   /**whether the author of the smell-introducing commit is author of one of the smell-introducing files*/
   public boolean ownership(String smellIntroCommit,Set<String> smellIntroFiles);

   public boolean newCommer(String smellIntroCommit);
}
