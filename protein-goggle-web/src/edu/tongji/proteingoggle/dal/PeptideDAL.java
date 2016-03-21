package edu.tongji.proteingoggle.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PeptideDAL {
	private final String sTableName = "Flat_txt";
	String url = "jdbc:mysql://127.0.0.1:3306/";
	static String dataBase = "";
	private List<String> lstColName = new ArrayList<String>();

	public PeptideDAL(String sDataBase) {

		dataBase = sDataBase;
		lstColName.add("ID");
		lstColName.add("AC");
		lstColName.add("DT");
		lstColName.add("DE");
		lstColName.add("GN");
		lstColName.add("OS");
		lstColName.add("OC");
		lstColName.add("OX");
		lstColName.add("RN");
		lstColName.add("RP");
		lstColName.add("RX");
		lstColName.add("RA");
		lstColName.add("RT");
		lstColName.add("RL");
		lstColName.add("RC");
		lstColName.add("RG");
		lstColName.add("CC");
		lstColName.add("DR");
		lstColName.add("PE");
		lstColName.add("KW");
		lstColName.add("FT");
		lstColName.add("SQ");
		lstColName.add("ModResSingle");
	}

	public void CreatePeptideTable() throws ClassNotFoundException,
			SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(url + dataBase, "root", "root");
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("CREATE TABLE %s (", sTableName));
		sb.append("   ID VARCHAR(500) NOT NULL,");
		sb.append("   AC TEXT,");
		sb.append("   DT TEXT,");
		sb.append("   DE TEXT,");
		sb.append("   GN TEXT,");
		sb.append("   OS TEXT,");
		sb.append("   OC TEXT,");
		sb.append("   OX TEXT,");
		sb.append("   RN TEXT,");
		sb.append("   RP TEXT,");
		sb.append("   RX TEXT,");
		sb.append("   RA TEXT,");
		sb.append("   RT TEXT,");
		sb.append("   RL TEXT,");
		sb.append("   RC TEXT,");
		sb.append("   RG TEXT,");
		sb.append("   CC TEXT,");
		sb.append("   DR TEXT,");
		sb.append("   PE TEXT,");
		sb.append("   KW TEXT,");
		sb.append("   FT TEXT,");
		sb.append("   SQ TEXT,");
		sb.append("   ModResSingle TEXT,");
		sb.append("   StartTime VARCHAR(20),");
		sb.append("   EndTime VARCHAR(20),");
		sb.append(" PRIMARY KEY (ID)");
		sb.append(") ENGINE = MYISAM DEFAULT CHARSET=latin1;");
		stmt = conn.createStatement();

		stmt.executeUpdate(sb.toString());
	}

    public void UpdateCreateTime(String startTime,String endTime) throws ClassNotFoundException, SQLException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("UPDATE %s ", sTableName));
        sb.append(String.format("SET StartTime = '%s',", startTime));
        sb.append(String.format("    EndTime = '%s'", endTime));
        Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(url + dataBase, "root", "root");
		stmt = conn.createStatement();
		stmt.executeUpdate(sb.toString());
    }
    
	public void InsertPeptide(Map<String, String> dict)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(url + dataBase, "root", "root");
		StringBuilder sb = new StringBuilder();
		StringBuilder sbHead = new StringBuilder();
		StringBuilder sbValue = new StringBuilder();

		List<String> listKey = new ArrayList<String>();
		List<String> listValue = new ArrayList<String>();
		Iterator<String> it = dict.keySet().iterator();
		
		while (it.hasNext()) {
			String key = it.next().toString();
			listKey.add(key);
			listValue.add(dict.get(key));
		}
		
		for (int i = 0; i < listKey.size(); i++) {
			if (this.lstColName.contains(listKey.get(i))) {
				sbHead.append(String.format(i == 0 ? "%s" : ",%s", listKey.get(i)));
				sbValue.append(String.format(i == 0 ? "'%s'" : ",'%s'", listValue
						.get(i).replaceAll("'", "''")));
			}
			
		}

		sb.append(String.format("INSERT INTO %s (", sTableName));
		sb.append(String.format("%s", sbHead.toString()));
		sb.append(") VALUES (");
		sb.append(String.format("%s", sbValue.toString()));
		sb.append(");");
		stmt = conn.createStatement();
		stmt.executeUpdate(sb.toString());
	}
}
