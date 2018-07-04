import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DB {
	// DB SELECT INSERT ��밡�̵� (1~5�ܰ�) 
	public void UsingDB() {
		String API_KEY = "";
		String Secret_KEY= "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
		// 1. DB ��� ��ü useDB ����
		DB useDB = new DB();	
		
		// 2. ������ String ����
		String selectSql = String.format("SELECT API_KEY, "
				+ "Secret_KEY FROM APIKEY WHERE _ID = \"%s\" and exchangeName = \"%s\" ;", "dirtyrobot00", "bithumb");
		
		// 3. ResultSet rs�� useDB.Query(������, "select" or "insert")�� ����
		ResultSet rs = useDB.Query(selectSql, "select"); 
		
		// 4-1. SELECT���� ��� rs�� ����� ResultSet���� ���ϴ� ���� ����
		try {
			while(rs.next()) {  
				API_KEY = rs.getString("API_KEY");
				Secret_KEY = rs.getString("Secret_KEY");
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		}
		
		// 4-2. INSERT���� ���
		 
		String insertSql = String.format("INSERT INTO Trade VALUES(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", %s, \"%s\", \"%s\", \"%s\" ,\"%s\")",
				"dirtyrobot00", "test", "btc", "bithumb", "123123123", "321321321", "bollingerPatternNaked", dateFormat.format(new Date()), "", dateFormat.format(new Date()));
		useDB.Query(insertSql, "insert");
		
		// 5. DB ����� clean()�� �̿��Ͽ� ����
		useDB.clean();
		
		System.out.println(API_KEY + " && " + Secret_KEY);
	}
	
	// DB Query �Լ�
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null; //ResultSet ��ü ����

	public ResultSet Query(String Sql, String INSSEL) {
		
		// ����̹� �ε�
	    try {	
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        System.out.println("����̹� �ε� ����!");
	
	    } catch (ClassNotFoundException e) {
	       System.out.println(e.getMessage());
	    }
		
	    try {	
	    	// DB ����
	    	String url = "jdbc:mysql://localhost:3306/test?autoReconnect=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC";
	        con = DriverManager.getConnection(url,"root","1111");	
	        System.out.println("�����ͺ��̽� ���� ����!");
	
	        stmt = con.createStatement();
	        
	        // INSERT��
	        if (INSSEL == "insert") {
				stmt.executeUpdate(Sql);
	        }
	        // SELECT��
	        else if (INSSEL == "select"){
				rs = stmt.executeQuery(Sql);	        	
	        } else { System.out.println("�����ͺ��̽� ���� ����!"); }

	        	        
	    } catch (SQLException se) {
            se.printStackTrace();
        } 
        return rs;	
	}
	
	public void clean() {

		try {
			if(rs !=null) { rs.close(); }
			if(stmt != null) { stmt.close(); }
			if(con!= null) { con.close(); }
		} catch (SQLException se) {
	        se.printStackTrace();
	    } 		
	}
}