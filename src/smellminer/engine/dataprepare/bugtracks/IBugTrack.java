package smellminer.engine.dataprepare.bugtracks;

public interface IBugTrack {
	public void initConnection();
	
	/***
	 * Given a bug Id, search from the data base to check whether it is a real bug or other types such as improvement
	 * @param bugId
	 * @return
	 */
	public boolean isBuggy(Long bugId);
}
