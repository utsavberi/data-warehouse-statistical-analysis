import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class StatisticalAnalysis {
	Connection connection = null;
	Statement stmt = null;
	PreparedStatement prepStatement = null;

	public StatisticalAnalysis(){
		initConnectionToDb();
	}
	void initConnectionToDb(){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/CSE601_2159.buffalo.edu", "marora2",
					"cse601");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void testQuery(){
		try{
			stmt = connection.createStatement();
			String sql;
			sql = "SELECT * FROM Disease";
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				println(rs.getString(2));
			}
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
	}

//	double calculatePearsonCorelation
	
	
	
	double runTtestOneVsAll(String diseaseName){
		String sql = "select t1.expression, t5.name "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID ";
				
		ArrayList<Double> expression1 = new ArrayList<Double>();
		ArrayList<Double> expression2 = new ArrayList<Double>();
		try{
			prepStatement = connection.prepareStatement(sql);
			
			ResultSet rs = prepStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()){
				if(rs.getString("NAME").equals(diseaseName)) expression1.add(rs.getDouble("EXPRESSION"));
				else expression2.add(rs.getDouble("EXPRESSION"));
			}
			return Statistics.tTest(expression1, expression2);
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		return 0.0;

	}
	double runTtestOneVsAll(String diseaseName, int goId){
		String sql = "select t1.expression, t5.name "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID "
				+ "where t8.go_id = ? ";
		ArrayList<Double> expression1 = new ArrayList<Double>();
		ArrayList<Double> expression2 = new ArrayList<Double>();
		try{
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,goId);
			ResultSet rs = prepStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()){
				if(rs.getString("NAME").equals(diseaseName))expression1.add(rs.getDouble("EXPRESSION"));
				else expression2.add(rs.getDouble("EXPRESSION"));
			}
			return Statistics.tTest(expression1, expression2);
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		return 0.0;

	}
	public double runFtest(ArrayList<String> groups){
		HashMap<String,ArrayList<Double>> groupMap = new HashMap<String,ArrayList<Double>>();
		for(String group: groups){
			groupMap.put(group, new ArrayList<Double>());
		}
		String sql = "select t1.expression, t5.name "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID ";
				
		
		try{
			prepStatement = connection.prepareStatement(sql);
			ResultSet rs = prepStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()){
				String diseaseName = rs.getString("NAME"); 
				if(groups.contains(diseaseName)) groupMap.get(diseaseName).add(rs.getDouble("EXPRESSION"));
		}
			return Statistics.fTest(groupMap);
//			return performTtest(expression1, expression2);
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		return 0.0;
	}
	public double runFtest(ArrayList<String> groups,int goId){
		HashMap<String,ArrayList<Double>> groupMap = new HashMap<String,ArrayList<Double>>();
		for(String group: groups){
			groupMap.put(group, new ArrayList<Double>());
		}
		String sql = "select t1.expression, t5.name "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID "
				+ "where t8.go_id = ? ";
		
		try{
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1,goId);
			ResultSet rs = prepStatement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while(rs.next()){
				String diseaseName = rs.getString("NAME"); 
				if(groups.contains(diseaseName)) groupMap.get(diseaseName).add(rs.getDouble("EXPRESSION"));
		}
			return Statistics.fTest(groupMap);
		}
		catch(SQLException ex){
			ex.printStackTrace();
		}
		return 0.0;
	}
		public static void println(String msg){
		System.out.println(msg);
	}
	public static void main(String[] args){
		StatisticalAnalysis an = new StatisticalAnalysis();
		System.out.println(an.runTtestOneVsAll("ALL",  12502));
		System.out.println(an.runFtest(new ArrayList<String>(){{add("ALL");
		add("AML");
		add("Colon tumor");
		add("Breast tumor");}}, 7154));
		
		//all patients with all expresion
		//LIST of patient[pid expression ALL]
		//LIST not ALL
		//combination of all two
		
		//
	}
}

