package edu.tongji.proteingoggle.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DataBaseDAL {
	String url = "jdbc:mysql://127.0.0.1:3306/";

	private Connection connection;

	public DataBaseDAL() {
	}

	public void CreateDataBase(String sDataBase) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(url, "root", "root");
		Statement stmt = connection.createStatement();
		String sql=String.format("CREATE DATABASE %s;", sDataBase);
	
		stmt.executeUpdate(sql);

	}

	@org.junit.Test
	public void Test() {
		try {
			CreateDataBase("soap_cyx");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
