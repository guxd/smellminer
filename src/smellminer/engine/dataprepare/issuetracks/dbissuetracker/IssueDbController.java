package smellminer.engine.dataprepare.issuetracks.dbissuetracker;

public interface IssueDbController {
	public void initConnection();
	
	/***
	 * Given a bug Id, search from the data base to check whether it is a real bug or other types such as improvement
	 * @param bugId
	 * @return
	 */
	public boolean isBuggy(Long bugId);
}
