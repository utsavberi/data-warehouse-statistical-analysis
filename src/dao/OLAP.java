package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OLAP {

	Connection connection = null;
	Statement stmt = null;
	PreparedStatement prepStatement = null;

	public OLAP() {
		initConnectionToDb();
	}

	void initConnectionToDb() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager
					.getConnection(
							"jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/CSE601_2159.buffalo.edu",
							"marora2", "cse601");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
				+ "join go_annotation t8 on t7.UUID = t8.UUID )"
				+ "WHERE ROWNUM <= 50";		
		try {
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}
			return arr;
//			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
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
				+ "join go_annotation t8 on t7.UUID = t8.UUID )"
				+ "WHERE ROWNUM <= 50";		
		try {
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}
			return arr;
//			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
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
				+ " group by t5.name,go_id )"
				+ "WHERE ROWNUM <= 50";		
		try {
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}
			return arr;
//			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
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
				+ " group by t5.name,t7.UUID)"
				+ "WHERE ROWNUM <= 50";		
		try {
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String a [] = {rs.getString(1),rs.getString(2),
						Integer.toString(rs.getInt(3))};
				arr.add(a);
			}
			return arr;
//			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public ArrayList<String> testQuery() {
		ArrayList<String> arr = new ArrayList<>();
		try {
			stmt = connection.createStatement();
			String sqlc;
			sqlc =  "SELECT * FROM Disease";
			ResultSet rs = stmt.executeQuery(sqlc);
			
			while (rs.next()) {
			//	println(rs.getString(2));
				arr.add(rs.getString(2));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return arr;
	}

}
