package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OLAP {

	private int threashold = 3000;
	Statement stmt = null;
	PreparedStatement prepStatement = null;

	Connection getConnectionToDb() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			return DriverManager
					.getConnection(
							"jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/CSE601_2159.buffalo.edu",
							"marora2", "cse601");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public ArrayList<String[]> diseaseGoidExpression(){
		ArrayList<String[]> arr = new ArrayList<>();
		String sql = "select * from (select t5.name , go_id, expression from "
				+ "M_RNA_EXPRESSION t1 join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID "
				+ "order by t5.name, go_id)"
				+ "WHERE ROWNUM <= "+threashold;
				;
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}

			return arr;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}
		return null;
	}

	public ArrayList<String[]> diseaseUUIDExpression(){
		ArrayList<String[]> arr = new ArrayList<>();
		String sql = "select * from (select t5.name , t7.UUID, expression from "
				+ "M_RNA_EXPRESSION t1 join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID "
				+ "order by t5.name,t7.UUID)"
				+ "WHERE ROWNUM <= "+threashold;
				;
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}
			return arr;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}

		return null;
	}
	public ArrayList<String[]> diseaseGoidExpressionRollup(){
		ArrayList<String[]> arr = new ArrayList<>();
		String sql = "select * from (select t5.name , go_id, avg(expression) from "
				+ "M_RNA_EXPRESSION t1 join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID"
				+ " group by t5.name,go_id "
				+ "order by t5.name, go_id)"
				+ "WHERE ROWNUM <= "+threashold;
				;
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}
			return arr;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}

		return null;
	}

	public ArrayList<String[]> diseaseUUIDExpressionRollup(){
		ArrayList<String[]> arr = new ArrayList<>();
		String sql = "select * from (select t5.name , t7.UUID, avg(expression) from "
				+ "M_RNA_EXPRESSION t1 join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t7.UUID = t8.UUID "
				+ " group by t5.name,t7.UUID "
				+ "order by t5.name, t7.UUID)"
				+ "WHERE ROWNUM <= "+threashold;
				;
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}
			return arr;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}

		return null;
	}
	
	public int numOfPatientsWithDiseasesByDescription(final String description){
//		List the number of patients who had “tumor” (disease description), “leukemia” (disease type) and 
//		“ALL” (disease name), separately.
		int ret = 0;
		String sql = "select count(*) from treatment t1 join disease t2 on t1.ds_id = t2.ds_id where description = ?";		
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, description);
			ResultSet rs = prepStatement.executeQuery();
			rs.next();
			ret = rs.getInt(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}
		
		return ret;
	}
	public int numOfPatientsWithDiseasesByType(final String type){
//		List the number of patients who had “tumor” (disease description), “leukemia” (disease type) and 
//		“ALL” (disease name), separately.
		int ret = 0;
		String sql = "select count(*) from treatment t1 join disease t2 on t1.ds_id = t2.ds_id where type = ?";		
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, type);
			ResultSet rs = prepStatement.executeQuery();
			rs.next();
			ret = rs.getInt(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}
		
		return ret;
	}
	public int numOfPatientsWithDiseasesByName(final String name){
//		List the number of patients who had “tumor” (disease description), “leukemia” (disease type) and 
//		“ALL” (disease name), separately.
		int ret = 0;
		String sql = "select count(*) from treatment t1 join disease t2 on t1.ds_id = t2.ds_id where name = ?";		
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, name);
			ResultSet rs = prepStatement.executeQuery();
			rs.next();
			ret = rs.getInt(1);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}
		return ret;
	}
	
	public ArrayList<String> listOfDrugsAppliedToPatientsWith(final String diseaseDescription){
//		List the types of drugs which have been applied to patients with “tumor”

		ArrayList<String> ret = new ArrayList<>();
		String sql = "select unique(t3.type) from treatment t1 join disease t2 on t1.ds_id = t2.ds_id join drug t3 on t1.dr_id = t3.dr_id where t2.description =?";		
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, diseaseDescription.trim().replace("\n"," "));
			ResultSet rs = prepStatement.executeQuery();
			while(rs.next()){
			ret.add(rs.getString(1));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}
		return ret;
	}
	
	public ArrayList<String> listMRNAExpressions(final String diseaseName,final String clusterId, final String muId){
//		For each sample of patients with “ALL”, list the mRNA values (expression) of probes in cluster
//		id “00002” for each experiment with measure unit id = “001”. (Note: measure unit id corresponds
//				to mu_id in microarray_fact.txt, cluster id corresponds to cl_id in gene_fact.txt, mRNA expression
//				value corresponds to exp in microarray_fact.txt, UID in probe.txt is a foreign key referring to
//				gene_fact.txt)

		ArrayList<String> ret = new ArrayList<>();
		String sql = "select t1.expression from m_rna_expression t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join treatment t3 on t3.p_id = t2.p_id "
				+ "join disease t4 on t3.ds_id = t4.ds_id "
				+ "join probe t5 on t1.pb_id = t5.pb_id "
				+ "join gene_sequence t6 on t5.UUID = t6.UUID "
				+ "join gene_cluster t7 on t6.UUID = t7.UUID "
				+ "join measurement_unit t8 on t1.mu_id = t8.mu_id where t4.name = ? and t7.cl_id = ? and t8.mu_id=?";
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, diseaseName);
			prepStatement.setString(2, clusterId);
			prepStatement.setString(3, muId);
			ResultSet rs = prepStatement.executeQuery();
			while(rs.next()){
				ret.add(rs.getString(1));

			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}
		
		return ret;
	}
	
	
	public ArrayList<String> drugsAppliedToPatientsWithDisease(String disease){
		//List the types of drugs which have been applied to patients with “tumor”
		ArrayList<String> ret = new ArrayList<String>();
		String sql = " select unique(t3.type) from treatment t1 "
				+ "join disease t2 on t1.ds_id = t2.ds_id "
				+ "join drug t3 on t1.dr_id = t3.dr_id where "
				+ "t2.description=?";		
		try {Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, disease);
			ResultSet rs = prepStatement.executeQuery();
			rs.next();
			ret.add(rs.getString(1));
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}

		return ret;
	}
	
	public ArrayList<Integer> expressionValuesOfPatients(String disease,int clusterId,int measureUnitId){
		//List the types of drugs which have been applied to patients with “tumor”
		ArrayList<Integer> ret = new ArrayList<Integer>();
		String sql = " select t1.expression "
				+ "from m_rna_expression t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join treatment t3 on t3.p_id = t2.p_id "
				+ "join disease t4 on t3.ds_id = t4.ds_id "
				+ "join probe t5 on t1.pb_id = t5.pb_id "
				+ "join gene_sequence t6 on t5.UUID = t6.UUID "
				+ "join gene_cluster t7 on t6.UUID = t7.UUID "
				+ "join measurement_unit t8 on t1.mu_id = t8.mu_id "
				+ "where t4.name = ? and t7.cl_id = ? and t8.mu_id=?";		
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setString(1, disease);
			prepStatement.setInt(2, clusterId);
			prepStatement.setInt(3, measureUnitId);
			ResultSet rs = prepStatement.executeQuery();
			while(rs.next()){
				ret.add(rs.getInt(1));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}

		return ret;
	}
	
	public ArrayList<String> testQuery() {
		ArrayList<String> arr = new ArrayList<>();
		try {
			Connection connection = getConnectionToDb();
			stmt = connection.createStatement();
			String sqlc;
			sqlc =  "SELECT * FROM Disease";
			ResultSet rs = stmt.executeQuery(sqlc);
			
			while (rs.next()) {
				arr.add(rs.getString(2));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		finally{
			try{connection.close();}catch(Exception ignored){}
		}

		return arr;
	}
	

}
