package smellminer.engine.dataprepare.bugtracks.dbbugtracker;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import smellminer.engine.dataprepare.bugtracks.IBugTrack;




public class BugTrackerMySQLImpl implements IBugTrack{
	Connection connection;
	private final static String PROPERTY_FILE = "/mysql.properties";
	private final static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	
	private String db;
	private String user;
	private String password;
	
	public BugTrackerMySQLImpl(String db,String user,String pw)
	{
	   this.db=db;
	   this.user=user;
	   this.password=pw;
	   this.initConnection();
	}
	
	private void loadSettingFromProperties(){
/*		Properties properties = new Properties();
		try {
			InputStream is = getClass().getResourceAsStream(PROPERTY_FILE);
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.db = properties.getProperty("mysql.db");
		this.user = properties.getProperty("mysql.user");
		this.password = properties.getProperty("mysql.password");
		this.initConnection();*/
	}

	public void initConnection() {
		loadSettingFromProperties();
		
		try {
			Class.forName(MYSQL_DRIVER);
			connection = DriverManager.getConnection(db, user, password);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isBuggy(Long bugId) {
		String sql = "SELECT bugSeverity FROM bugReport where bugId=?";
		String severity = "";
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setLong(1, bugId);
			
			ResultSet rs = statement.executeQuery();
			
			rs.next();
			severity = rs.getString("bugSeverity");
			severity = severity.toLowerCase();
		} catch (SQLException e) {
			return false;
		}
		
		return !(severity.equals("enhancement") || severity.equals("trivial"));
	}
}
