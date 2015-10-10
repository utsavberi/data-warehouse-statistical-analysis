package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class StatisticalAnalysis {
	Connection connection = null;
	Statement stmt = null;
	PreparedStatement prepStatement = null;

	public StatisticalAnalysis() {
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

	void testQuery() {
		try {
			stmt = connection.createStatement();
			String sql;
			sql = "SELECT * FROM Disease";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				println(rs.getString(2));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	double runTtestOneVsAll(String diseaseName) {
		String sql = "select t1.expression, t5.name "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID ";

		ArrayList<Double> expression1 = new ArrayList<Double>();
		ArrayList<Double> expression2 = new ArrayList<Double>();
		try {
			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME").equals(diseaseName))
					expression1.add(rs.getDouble("EXPRESSION"));
				else
					expression2.add(rs.getDouble("EXPRESSION"));
			}
			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	public double runTtestOneVsAll(String diseaseName, int goId) {
		String sql = "select t1.expression, t5.name "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID "
				+ "where t8.go_id = ? ";
		ArrayList<Double> expression1 = new ArrayList<Double>();
		ArrayList<Double> expression2 = new ArrayList<Double>();
		try {
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, goId);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME").equals(diseaseName))
					expression1.add(rs.getDouble("EXPRESSION"));
				else
					expression2.add(rs.getDouble("EXPRESSION"));
			}
			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	public double calculateAveragePearsonCorrelation(String diseaseName, int goId) {
		String sql = "select t1.expression, t5.name , t3.p_id "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID "
				+ "where t8.go_id = ? and t5.name=?";
		HashMap<Integer, ArrayList<Double>> patientExpression = new HashMap<Integer, ArrayList<Double>>();
		try {
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, goId);
			prepStatement.setString(2, diseaseName);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				int pId = rs.getInt("p_id");
				if (patientExpression.get(pId) == null) {
					patientExpression.put(pId, new ArrayList<Double>(){{add(rs.getDouble("EXPRESSION"));}});
				} else {
					patientExpression.get(pId).add(rs.getDouble("EXPRESSION"));
				}

			}
			double sum = 0;
			int num = 0;
			ArrayList<ArrayList<Double>> tmp = new ArrayList<ArrayList<Double>>(
					patientExpression.values());
			for (int i = 0; i < patientExpression.size(); i++) {
				for (int j = i + 1; j < patientExpression.size(); j++) {
					sum += Statistics.pearsonCorrelation(tmp.get(i), tmp.get(j));
					num++;
				}
			}
			
			return sum /num;// ((tmp.size() * (tmp.size() - 1)) / 2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	public double calculateAveragePearsonCorrelation(String diseaseName1,
			String diseaseName2, int goId) {
		String sql = "select t1.expression, t5.name , t3.p_id "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
//				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID "
				+ "where t8.go_id = ? and (t5.name=? OR t5.name=?)";
		HashMap<Integer, ArrayList<Double>> patientExpressionD1 = new HashMap<Integer, ArrayList<Double>>();
		HashMap<Integer, ArrayList<Double>> patientExpressionD2 = new HashMap<Integer, ArrayList<Double>>();
		try {
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, goId);
			prepStatement.setString(2, diseaseName1);
			prepStatement.setString(3, diseaseName2);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				int pId = rs.getInt("p_id");
				String disease = rs.getString("NAME");
				if (disease.equals(diseaseName1)) {
					if (patientExpressionD1.get(pId) == null) {
						patientExpressionD1.put(pId, new ArrayList<Double>() {{add(rs.getDouble("EXPRESSION"));}});
					} else {
						patientExpressionD1.get(pId).add(
								rs.getDouble("EXPRESSION"));
					}
				} else {
					if (patientExpressionD2.get(pId) == null) {
						patientExpressionD2.put(pId, new ArrayList<Double>() {{add(rs.getDouble("EXPRESSION"));}});
					} else {
						patientExpressionD2.get(pId).add(rs.getDouble("EXPRESSION"));
					}
				}
			}
			int num=0;
			double sum = 0;
			ArrayList<ArrayList<Double>> tmp = new ArrayList<ArrayList<Double>>(patientExpressionD1.values());
			ArrayList<ArrayList<Double>> tmp2 = new ArrayList<ArrayList<Double>>(patientExpressionD2.values());
			for (ArrayList<Double> arr1 : tmp) {
				for (ArrayList<Double> arr2 : tmp2) {
					sum += Statistics.pearsonCorrelation(arr1, arr2);
					num++;
				}
			}
			//((tmp.size() * (tmp.size() - 1)) / 2)
			return sum/num;
//			return sum / patientExpressionD1.size()
//					* patientExpressionD2.size();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;

	}

	public double runFtest(ArrayList<String> groups) {
		HashMap<String, ArrayList<Double>> groupMap = new HashMap<String, ArrayList<Double>>();
		for (String group : groups) {
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
				+ "join go_annotation t8 on t6.UUID = t8.UUID ";
		try {
			prepStatement = connection.prepareStatement(sql);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String diseaseName = rs.getString("NAME");
				if (groups.contains(diseaseName))
					groupMap.get(diseaseName).add(rs.getDouble("EXPRESSION"));
			}
			return Statistics.fTest(groupMap);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	public double runFtest(ArrayList<String> groups, int goId) {
		HashMap<String, ArrayList<Double>> groupMap = new HashMap<String, ArrayList<Double>>();
		for (String group : groups) {
			groupMap.put(group, new ArrayList<Double>());
		}
		String sql = "select t1.expression, t5.name "
				+ "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id "
				+ "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id "
				+ "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
//				+ "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID "
				+ "where t8.go_id = ? ";

		try {
			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, goId);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String diseaseName = rs.getString("NAME");
				if (groups.contains(diseaseName))
					groupMap.get(diseaseName).add(rs.getDouble("EXPRESSION"));
			}
			return Statistics.fTest(groupMap);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	public static void println(String msg) {
		System.out.println(msg);
	}

	public static void main(String[] args) {
		StatisticalAnalysis an = new StatisticalAnalysis();
		System.out.println(an.runTtestOneVsAll("ALL", 12502));
		System.out.println(an.runFtest(new ArrayList<String>() {
			{
				add("ALL");
				add("AML");
				add("Colon tumor");
				add("Breast tumor");
			}
		}, 7154));
		System.out.println(an.calculateAveragePearsonCorrelation("ALL", 7154));
		System.out.println(an.calculateAveragePearsonCorrelation("ALL", "AML",
				7154));
	}
}
