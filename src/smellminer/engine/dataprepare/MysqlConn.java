package smellminer.engine.dataprepare;
import java.sql.*;

public class MysqlConn {
	private String user = "";
	private String password = "";
	private String host = "";
	private String database = "";
	private String url ="";
	private Connection conn = null;
	Statement stmt;
	
	public static void main(String arg[]) throws SQLException{
		MysqlConn testConn = new MysqlConn("localhost","test","root", "pan446891");
		ResultSet rs = testConn.executeQuery("select * from mytest;");
		while(rs.next()) { 
			System.out.println("id=" + rs.getInt("id")); 
			System.out.println("name=" + rs.getString("name")); 
			System.out.println("---------------"); 
        }
		testConn.addBatch("insert into mytest values ('caidx',3)");
		testConn.addBatch("insert into mytest values ('caidx1',2)");
		//testConn.addBatch("commit");
		testConn.executeBatch();
		rs = testConn.executeQuery("select * from mytest;");
		while(rs.next()) { 
			System.out.println("id=" + rs.getInt("id")); 
			System.out.println("name=" + rs.getString("name")); 
			System.out.println("---------------"); 
        }
		rs.close();
		
		testConn.close();
	}
	
	public MysqlConn(String url,String user,String password)
	{
	   this.user = user;
	   this.password = password;
	   this.url = url;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}catch (ClassNotFoundException e) {
			System.err.println("class not found:" + e.getMessage());
			}
		try {
			conn = DriverManager.getConnection(this.url, this.user, this.password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		}catch (SQLException a) {
			System.err.println("sql exception:" + a.getMessage());
		}
	}
	
	public MysqlConn(String host, String database, String user, String password) {
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		this.url = "jdbc:mysql://" + host + "/" + database + "?useUnicode=true&characterEncoding=UTF-8";
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}catch (ClassNotFoundException e) {
			System.err.println("class not found:" + e.getMessage());
			}
		try {
			conn = DriverManager.getConnection(this.url, this.user, this.password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		}catch (SQLException a) {
			System.err.println("sql exception:" + a.getMessage());
			}
	}
	
	public MysqlConn(MysqlConn tempconn) {
		this.host = tempconn.host;
		this.database = tempconn.database;
		this.user = tempconn.user;
		this.password = tempconn.password;
		this.url = "jdbc:mysql://" + host + "/" + database + "?useUnicode=true&characterEncoding=UTF-8";
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
		}catch (ClassNotFoundException e) {
			System.err.println("class not found:" + e.getMessage());
			}
		try {
			conn = DriverManager.getConnection(this.url, this.user, this.password);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		}catch (SQLException a) {
			System.err.println("sql exception:" + a.getMessage());
			}
	}
	public Connection getConn() {
		return conn;
		}

	public ResultSet executeQuery(String sql) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		//	MainGene.log.appendLog(e.getMessage());
			for(StackTraceElement STE:e.getStackTrace()){
		//		MainGene.log.appendLog(STE.toString());
			}
			}
		return rs;
	}
	@SuppressWarnings("finally")
	public boolean executeUpdate(String sql) {
		boolean v = false;
		try {
			v = stmt.executeUpdate(sql) > 0 ? true : false;
		}catch (SQLException e) {
			e.printStackTrace();
		//	MainGene.log.appendLog(e.getMessage());
			for(StackTraceElement STE:e.getStackTrace()){
		//		MainGene.log.appendLog(STE.toString());
			}
			}
		finally {
			return v;
		}
	}
	
	@SuppressWarnings("finally")
	public int[] executeBatch() {
		int[] v = null;
		try {
			v = stmt.executeBatch();
		}catch (SQLException e) {
			e.printStackTrace();
		//	MainGene.log.appendLog(e.getMessage());
			for(StackTraceElement STE:e.getStackTrace()){
		//		MainGene.log.appendLog(STE.toString());
			}
			}
		finally {
			return v;
		}
	}
	
	public void addBatch(String sql) {
		try {
			stmt.addBatch(sql);
		}catch (SQLException e) {
			e.printStackTrace();
		//	MainGene.log.appendLog(e.getMessage());
			for(StackTraceElement STE:e.getStackTrace()){
		//		MainGene.log.appendLog(STE.toString());
			}
			}
	}
	
	public void close(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		//	MainGene.log.appendLog(e.getMessage());
			for(StackTraceElement STE:e.getStackTrace()){
		//		MainGene.log.appendLog(STE.toString());
			}
		}
	}
}
