package smellminer.engine.dataprepare.issuetracks;

import java.io.Serializable;
import smellminer.engine.dataprepare.issuetracks.definition.Issue;

public interface IIssueTrack extends Serializable
{
   /***
    * Link a commit log with an issue in the issue tracker.
    * @param commitRev
    * @return
    */
    public Issue linkIssue(String commitRev);   
}
