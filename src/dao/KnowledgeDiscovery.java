package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TTestImpl;

public class KnowledgeDiscovery {
	ArrayList<String> informativeGeneList = new ArrayList<String>();
	Statement stmt = null;
	PreparedStatement prepStatement = null;
	public static double thresholdPValue = 0.01;

	public KnowledgeDiscovery() {
		org.apache.commons.math.stat.inference.TTestImpl t = new TTestImpl();

	}

	Connection getConnectionToDb() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			return DriverManager.getConnection(
					"jdbc:oracle:thin:@//dbod-scan.acsu.buffalo.edu:1521/CSE601_2159.buffalo.edu", "marora2", "cse601");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	void testQuery() {
		try {
			Connection connection = getConnectionToDb();
			stmt = connection.createStatement();
			String sql;
			sql = "SELECT * FROM Disease";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				println(rs.getString(2));
			}
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<String> getInformativeGenes(String diseaseName) {
		ArrayList<String> informativeGenes = new ArrayList<String>();
		String sql = "select t1.expression, t5.name, t7.UUID " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				+ "join gene_sequence t7 on t6.UUID = t7.UUID ORDER BY  UUID ASC";
		System.out.println("SQL -> "+sql);
		try {
			Connection connection = getConnectionToDb();
			prepStatement = connection.prepareStatement(sql);
			HashMap<String, ArrayList<Double>> expression1 = new HashMap<String, ArrayList<Double>>();
			HashMap<String, ArrayList<Double>> expression2 = new HashMap<String, ArrayList<Double>>();
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME").equals(diseaseName)) {
					if (expression1.containsKey(rs.getString("UUID"))) {
						expression1 = addExpressionToExistingUUID(expression1, rs.getString("UUID"),
								Double.parseDouble(rs.getString("EXPRESSION")));
					} else {
						expression1 = createNewUUID(expression1, rs.getString("UUID"),
								Double.parseDouble(rs.getString("EXPRESSION")));
					}
				} else {
					if (expression2.containsKey(rs.getString("UUID"))) {
						expression2 = addExpressionToExistingUUID(expression2, rs.getString("UUID"),
								Double.parseDouble(rs.getString("EXPRESSION")));
					} else {
						expression2 = createNewUUID(expression2, rs.getString("UUID"),
								Double.parseDouble(rs.getString("EXPRESSION")));
					}
				}

			}
			String uuid;
			ArrayList<Double> expressionValues;
			for (Map.Entry<String, ArrayList<Double>> entry : expression1.entrySet()) {
				uuid = entry.getKey();
				expressionValues = entry.getValue();
				if (expression2.containsKey(uuid)) {
					if (isGeneInformative(expressionValues, expression2.get(uuid)))
						informativeGenes.add(uuid);
				}
			}
			System.out.println("Informative genes " + informativeGenes.toString());
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally{
			
		}
		
		return informativeGenes;

	}

	private boolean isGeneInformative(ArrayList<Double> expression1, ArrayList<Double> expression2) {
		// TODO Auto-generated method stub
		double[] expressionValues2 = getDoubleArrayFromArrayList(expression2);
		double[] expressionValues1 = getDoubleArrayFromArrayList(expression1);
		TTestImpl ttestImpl = new TTestImpl();
		try {
			double pValue = ttestImpl.homoscedasticTTest(expressionValues1, expressionValues2);
			if (pValue < thresholdPValue) {
				return true;
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private boolean classifyNewPatientForDisease(String diseaseName, HashMap<String, Double> sampleData)
			throws SQLException 
	{
		KnowledgeDiscovery an = new KnowledgeDiscovery();
	   if(informativeGeneList==null || informativeGeneList.size()==0)
		informativeGeneList = an.getInformativeGenes(diseaseName);
	   //System.out.println("Informative gene list ---> "+informativeGeneList);
		ArrayList<String> patientListWithDisease = an.getPatientsWithDisease(diseaseName, true);
		//System.out.println("PatientListWithDisease "+patientListWithDisease);
		ArrayList<String> patientListWithoutDisease = an.getPatientsWithDisease(diseaseName, false);
		//System.out.println("patientListWithoutDisease "+patientListWithoutDisease);
		HashMap<String, ArrayList<Double>> informativeGeneForNewPatient = getInformativeGenesForNewPatient(sampleData,
				informativeGeneList);
		//System.out.println("Size of gene of NEW patient "+informativeGeneForNewPatient.size());
		HashMap<String, HashMap<String, ArrayList<Double>>> patientGeneExpressionValueHashMapGroupA = prepareForCorrelation(
				patientListWithDisease, informativeGeneList, diseaseName, true);
		//System.out.println("patientGeneExpressionValueHashMapGroupA "+patientGeneExpressionValueHashMapGroupA.size());
		HashMap<String, HashMap<String, ArrayList<Double>>> patientGeneExpressionValueHashMapGroupB = prepareForCorrelation(
				patientListWithoutDisease, informativeGeneList, diseaseName, false);
		//System.out.println("patientGeneExpressionValueHashMapGroupB "+patientGeneExpressionValueHashMapGroupB.size());
		ArrayList<Double> groupACorrelationValues = new ArrayList<Double>();
		ArrayList<Double> groupBCorrelationValues = new ArrayList<Double>();
		for (Map.Entry<String, HashMap<String, ArrayList<Double>>> entry : patientGeneExpressionValueHashMapGroupA.entrySet()) 
		{
			
				String patient = entry.getKey();
				TreeMap<String, ArrayList<Double>> informativeGenesDataForPatientN = new TreeMap<String, ArrayList<Double>>(entry.getValue());
				TreeMap<String, ArrayList<Double>> informativeGeneForNewPatientTreeMap = new TreeMap<String, ArrayList<Double>>(informativeGeneForNewPatient);
			    ArrayList<Double> exp1 = new ArrayList<Double>();
			    ArrayList<Double> exp2 = new ArrayList<Double>();
				
			    for(ArrayList<Double> val: informativeGeneForNewPatientTreeMap.values()) {
					exp1.add(val.get(0));
					}
			    TreeMap<String, ArrayList<Double>> inforGene = new TreeMap<String, ArrayList<Double>>(informativeGenesDataForPatientN);
//				//for (Map.Entry<String, ArrayList<Double>> informativeGeneData : informativeGenesDataForPatientN.entrySet()) 
			    for (Map.Entry<String, ArrayList<Double>> informativeGeneData : inforGene.entrySet())
					{
					
					for(ArrayList<Double> val: inforGene.values()) {
						exp2.add(val.get(0));
						}
					//  System.out.println("Calculating correlation between GROUP A "+exp1.toString() + "!!!!!!!!!!!!!!!!!!!!!!"+exp2.toString());
				      groupACorrelationValues.add(Statistics.pearsonCorrelation(exp1, exp2));
					}
					//System.out.println("groupACorrelationValues "+groupACorrelationValues.toString());
		}
		for (Map.Entry<String, HashMap<String, ArrayList<Double>>> entry : patientGeneExpressionValueHashMapGroupB.entrySet()) 
		{
			
			String patient = entry.getKey();
			TreeMap<String, ArrayList<Double>> informativeGenesDataForPatientN = new TreeMap<String, ArrayList<Double>>(entry.getValue());
			TreeMap<String, ArrayList<Double>> informativeGeneForNewPatientTreeMap = new TreeMap<String, ArrayList<Double>>(informativeGeneForNewPatient);
		    ArrayList<Double> exp1 = new ArrayList<Double>();
		    ArrayList<Double> exp2 = new ArrayList<Double>();
		    for(ArrayList<Double> val: informativeGeneForNewPatientTreeMap.values()) {
				exp1.add(val.get(0));
				}
		    TreeMap<String, ArrayList<Double>> inforGene = new TreeMap<String, ArrayList<Double>>(informativeGenesDataForPatientN);
//			//for (Map.Entry<String, ArrayList<Double>> informativeGeneData : informativeGenesDataForPatientN.entrySet()) 
		    for (Map.Entry<String, ArrayList<Double>> informativeGeneData : inforGene.entrySet())
				{
				
				for(ArrayList<Double> val: inforGene.values()) {
					exp2.add(val.get(0));
					}
				 // System.out.println("Calculating correlation between for GROUP B "+exp1.toString() + "!!!!!!!!!!!!!!!!!!!!!!"+exp2.toString());
			      groupBCorrelationValues.add(Statistics.pearsonCorrelation(exp1, exp2));
				}
			//	System.out.println("groupBCorrelationValues "+groupBCorrelationValues.toString());
	}
		double[] expressionValues1 = getDoubleArrayFromArrayList(groupACorrelationValues);
		//System.out.println("expressionValues1 "+Arrays.asList(expressionValues1));
		double[] expressionValues2 = getDoubleArrayFromArrayList(groupBCorrelationValues);
		//System.out.println("expressionValues2 "+Arrays.asList(expressionValues2));
		TTestImpl ttestImpl = new TTestImpl();
		try {
			double pValue = ttestImpl.homoscedasticTTest(expressionValues1, expressionValues2);
			//System.out.println("pValue-->"+pValue);
			if (pValue < thresholdPValue) {
				return true;
			}
		}
		catch(Exception e)
		{e.printStackTrace();}
		return false;
	}

	private HashMap<String, ArrayList<Double>> getInformativeGenesForNewPatient(HashMap<String, Double> sampleData,
			ArrayList<String> informativeGeneList) {
		// TODO Auto-generated method stub
		HashMap<String, ArrayList<Double>> geneExpressionValueMap = new HashMap<String, ArrayList<Double>>();
		ArrayList<Double> arrayList;
		//System.out.println("Get Informative genes for new patient "+sampleData.toString());
		for (Map.Entry<String, Double> entry : sampleData.entrySet()) {
			String key = entry.getKey();
			double value = entry.getValue();
			if (informativeGeneList.contains(key)) 
			{
				System.out.println("This key is in infomative gene");
				arrayList =  new ArrayList<Double>();
				arrayList.add(value);
				geneExpressionValueMap.put(key, arrayList);
			}
		}
		//System.out.println("geneExpressionValueMap "+geneExpressionValueMap.toString());
		return geneExpressionValueMap;
	}

	private HashMap<String, HashMap<String, ArrayList<Double>>> prepareForCorrelation(ArrayList<String> patientList,
			ArrayList<String> informativeGeneList, String diseaseName, boolean withDisease) throws SQLException {
		HashMap<String, HashMap<String, ArrayList<Double>>> groupSampleData = new HashMap<String, HashMap<String,ArrayList<Double>>>();
		HashMap<String, ArrayList<Double>> informativeGeneListForPatient;
		for (String patient : patientList) {
			informativeGeneListForPatient = getInformativeGenesForPatient(patient, diseaseName, informativeGeneList,withDisease);
			if(!informativeGeneListForPatient.isEmpty())
			groupSampleData.put(patient, informativeGeneListForPatient);
		}
		return groupSampleData;
		// TODO Auto-generated method stub

	}

	private HashMap<String, ArrayList<Double>> getInformativeGenesForPatient(String patient, String diseaseName,
			ArrayList<String> informativeGeneList, boolean withDisease) throws SQLException {
		// TODO Auto-generated method stub
		String sql = null;
		HashMap<String, ArrayList<Double>> geneAndExpressionHashMap = new HashMap<String, ArrayList<Double>>();
		if(withDisease)
		{
		sql = "select t1.expression, t3.P_ID , t7.UUID " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id  "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id " + "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "where t5.name = ? and t2.p_id = ? ";
		}
		if(withDisease == false)
		{
			sql = "select t1.expression, t3.P_ID , t7.UUID " + "from M_RNA_EXPRESSION t1 "
					+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id  "
					+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
					+ "join probe t6 on t1.pb_id = t6.pb_id " + "join gene_sequence t7 on t6.UUID = t7.UUID "
					+ "where t5.name <> ? and t2.p_id = ? ";

		}
		System.out.println("SQL "+sql);
		Connection connection = getConnectionToDb();

		prepStatement = connection.prepareStatement(sql);
		prepStatement.setString(1, diseaseName);
		prepStatement.setString(2, patient);
	   
		ResultSet rs = prepStatement.executeQuery();
		//System.out.println("withDisease "+withDisease +" patient "+ patient );
		int count =0;
		while (rs.next()) {
			count++;
			if (informativeGeneList.contains(rs.getString("UUID"))) {
				if (geneAndExpressionHashMap.containsKey(rs.getString("UUID"))) {
					geneAndExpressionHashMap = addExpressionToExistingUUID(geneAndExpressionHashMap,
							rs.getString("UUID"), Double.parseDouble(rs.getString("EXPRESSION")));

				} else {
					geneAndExpressionHashMap = createNewUUID(geneAndExpressionHashMap, rs.getString("UUID"),
							Double.parseDouble(rs.getString("EXPRESSION")));
				}
			}
		}
		System.out.println(" rs.size "+count);
		System.out.println("geneAndExpressionHashMap "+geneAndExpressionHashMap.toString());
		connection.close();
		return geneAndExpressionHashMap;
	}

	private ArrayList<String> getPatientsWithDisease(String diseaseName, boolean withDisease)  {
		// TODO Auto-generated method stub
		ArrayList<String> patientList = new ArrayList<String>();
		
		//System.out.println("in get patient with disease"+diseaseName+">>");
		String sql = "";
		if(withDisease)
		sql = "select t1.P_ID from TREATMENT t1" + " join disease t2 on t1.ds_id = t2.DS_ID  where t2.NAME = ?";
		if(withDisease == false)
			sql = "select t1.P_ID from TREATMENT t1 " + " join disease t2 on t1.ds_id = t2.DS_ID  where t2.NAME <> ?";
	
		//System.out.println("SQL" +sql);
		try {
			Connection connection = getConnectionToDb();

			prepStatement = connection.prepareStatement(sql);
		
		prepStatement.setString(1, diseaseName);
		
		ResultSet rs = prepStatement.executeQuery();
		System.out.println("Resultset "+	rs);
		while (rs.next()) {
			patientList.add(rs.getString("P_ID"));
		}
		connection.close();
		//System.out.println("Patient List with disease "+ withDisease + " --> "+patientList.toString());
		return patientList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return patientList;	
	}

	private double[] getDoubleArrayFromArrayList(ArrayList<Double> expression) {
		// TODO Auto-generated method stub
		double[] expressionValues = new double[expression.size()];
		for (int i = 0; i < expression.size(); i++) {
			expressionValues[i] = expression.get(i).doubleValue(); // java 1.4
																	// style
			// or:
			expressionValues[i] = expression.get(i); // java 1.5+ style
														// (outboxing)
		}

		return expressionValues;
	}

	private HashMap<String, ArrayList<Double>> createNewUUID(HashMap<String, ArrayList<Double>> expression,
			String uuid, double expressionValue) {
		// TODO Auto-generated method stub
		ArrayList<Double> expressionValues = new ArrayList<Double>();
		expressionValues.add(expressionValue);
		expression.put(uuid, expressionValues);
		return expression;
	}

	private HashMap<String, ArrayList<Double>> addExpressionToExistingUUID(
			HashMap<String, ArrayList<Double>> expression, String uuid, double expressionValue) {
		// TODO Auto-generated method stub
		ArrayList<Double> expressionValues = expression.get(uuid);
		expressionValues.add(expressionValue);
		expression.put(uuid, expressionValues);
		return expression;

	}

	double runTtestOneVsAll(String diseaseName) {
		String sql = "select t1.expression, t5.name " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				// + "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID ";

		ArrayList<Double> expression1 = new ArrayList<Double>();
		ArrayList<Double> expression2 = new ArrayList<Double>();
		try {
			Connection connection = getConnectionToDb();

			prepStatement = connection.prepareStatement(sql);

			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME").equals(diseaseName))
					expression1.add(rs.getDouble("EXPRESSION"));
				else
					expression2.add(rs.getDouble("EXPRESSION"));
			}
			connection.close();
			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	double runTtestOneVsAll(String diseaseName, int goId) {
		String sql = "select t1.expression, t5.name " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				// + "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID " + "where t8.go_id = ? ";
		ArrayList<Double> expression1 = new ArrayList<Double>();
		ArrayList<Double> expression2 = new ArrayList<Double>();
		try {
			Connection connection = getConnectionToDb();

			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, goId);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				if (rs.getString("NAME").equals(diseaseName))
					expression1.add(rs.getDouble("EXPRESSION"));
				else
					expression2.add(rs.getDouble("EXPRESSION"));
			}
			connection.close();
			return Statistics.tTest(expression1, expression2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	double calculateAveragePearsonCorrelation(String diseaseName, int goId) {
		String sql = "select t1.expression, t5.name , t3.p_id " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				// + "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID " + "where t8.go_id = ? and t5.name=?";
		HashMap<Integer, ArrayList<Double>> patientExpression = new HashMap<Integer, ArrayList<Double>>();
		try {
			Connection connection = getConnectionToDb();

			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, goId);
			prepStatement.setString(2, diseaseName);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				int pId = rs.getInt("p_id");
				if (patientExpression.get(pId) == null) {
					patientExpression.put(pId, new ArrayList<Double>() {
						{
							add(rs.getDouble("EXPRESSION"));
						}
					});
				} else {
					patientExpression.get(pId).add(rs.getDouble("EXPRESSION"));
				}

			}
			double sum = 0;
			ArrayList<ArrayList<Double>> tmp = new ArrayList<ArrayList<Double>>(patientExpression.values());
			for (int i = 0; i < patientExpression.size(); i++) {
				for (int j = i + 1; j < patientExpression.size(); j++) {
					sum += Statistics.pearsonCorrelation(tmp.get(i), tmp.get(j));
				}
			}
			connection.close();
			return sum / ((tmp.size() * (tmp.size() - 1)) / 2);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return 0.0;
	}

	double calculateAveragePearsonCorrelation(String diseaseName1, String diseaseName2, int goId) {
		String sql = "select t1.expression, t5.name , t3.p_id " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				// + "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID " + "where t8.go_id = ? and (t5.name=? OR t5.name=?)";
		HashMap<Integer, ArrayList<Double>> patientExpressionD1 = new HashMap<Integer, ArrayList<Double>>();
		HashMap<Integer, ArrayList<Double>> patientExpressionD2 = new HashMap<Integer, ArrayList<Double>>();
		try {
			Connection connection = getConnectionToDb();

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
						patientExpressionD1.put(pId, new ArrayList<Double>() {
							{
								add(rs.getDouble("EXPRESSION"));
							}
						});
					} else {
						patientExpressionD1.get(pId).add(rs.getDouble("EXPRESSION"));
					}
				} else {
					if (patientExpressionD2.get(pId) == null) {
						patientExpressionD2.put(pId, new ArrayList<Double>() {
							{
								add(rs.getDouble("EXPRESSION"));
							}
						});
					} else {
						patientExpressionD2.get(pId).add(rs.getDouble("EXPRESSION"));
					}
				}
			}
			double sum = 0;
			ArrayList<ArrayList<Double>> tmp = new ArrayList<ArrayList<Double>>(patientExpressionD1.values());
			ArrayList<ArrayList<Double>> tmp2 = new ArrayList<ArrayList<Double>>(patientExpressionD2.values());
			for (ArrayList<Double> arr1 : tmp) {
				for (ArrayList<Double> arr2 : tmp2) {
					sum += Statistics.pearsonCorrelation(arr1, arr2);
				}
			}
			connection.close();
			return sum / patientExpressionD1.size() * patientExpressionD2.size();
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
		String sql = "select t1.expression, t5.name " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				// + "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID ";
		try {
			Connection connection = getConnectionToDb();

			prepStatement = connection.prepareStatement(sql);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String diseaseName = rs.getString("NAME");
				if (groups.contains(diseaseName))
					groupMap.get(diseaseName).add(rs.getDouble("EXPRESSION"));
			}
			connection.close();
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
		String sql = "select t1.expression, t5.name " + "from M_RNA_EXPRESSION t1 "
				+ "join sample t2 on t1.s_id = t2.s_id " + "join patient t3 on t2.p_id = t3.p_id "
				+ "join treatment t4 on t4.p_id = t3.p_id " + "join DISEASE t5 on t4.DS_ID = t5.ds_id "
				+ "join probe t6 on t1.pb_id = t6.pb_id "
				// + "join gene_sequence t7 on t6.UUID = t7.UUID "
				+ "join go_annotation t8 on t6.UUID = t8.UUID " + "where t8.go_id = ? ";

		try {
			Connection connection = getConnectionToDb();

			prepStatement = connection.prepareStatement(sql);
			prepStatement.setInt(1, goId);
			ResultSet rs = prepStatement.executeQuery();
			while (rs.next()) {
				String diseaseName = rs.getString("NAME");
				if (groups.contains(diseaseName))
					groupMap.get(diseaseName).add(rs.getDouble("EXPRESSION"));
			}
			connection.close();
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
		KnowledgeDiscovery an = new KnowledgeDiscovery();
		/*
		 * System.out.println(an.runTtestOneVsAll("ALL", 12502));
		 * System.out.println(an.runFtest(new ArrayList<String>() { {
		 * add("ALL"); add("AML"); add("Colon tumor"); add("Breast tumor"); } },
		 * 7154));
		 * System.out.println(an.calculateAveragePearsonCorrelation("ALL",
		 * 7154));
		 * System.out.println(an.calculateAveragePearsonCorrelation("ALL",
		 * "AML", 7154));
		 */
		an.getInformativeGenes("ALL");
		an.runTestsForSampleData("ALL");
	}

	public ArrayList<String> runTestsForSampleData(String diseaseName) {
		ArrayList<String> ret = new ArrayList<>();
		for (int testSampleCount = 3; testSampleCount <= 5; testSampleCount++) {
			String sql = "select UUID, test" + testSampleCount + " from test_sample";
			HashMap<String, Double> sampleData = new HashMap<String, Double>();
			KnowledgeDiscovery an = new KnowledgeDiscovery();
			
			try {
				Connection connection = getConnectionToDb();

				prepStatement = connection.prepareStatement(sql);
				ResultSet rs = prepStatement.executeQuery();
				System.out.println("Classify called "+rs);
				
				boolean hasDisease = false;
				while (rs.next()) {
					
					sampleData.put(rs.getString("UUID"),
							Double.parseDouble(rs.getString("TEST" + testSampleCount)));
					
				}
				System.out.println("Classify called "+testSampleCount);
				System.out.println("sampleData "+sampleData.toString());
				
				hasDisease = an.classifyNewPatientForDisease(diseaseName, sampleData);
				System.out.println("test" + testSampleCount + " has +" + diseaseName + " -->  " + hasDisease);
				ret.add("test" + testSampleCount + " has +" + diseaseName + " -->  " + hasDisease);
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}}
