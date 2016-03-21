package edu.tongji.proteingoggle.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ElementPeaksDAL {
	String url = "jdbc:mysql://127.0.0.1:3306/";

	public Map<String, PeaksData> getElementPeaks() throws Exception {

		Map<String, PeaksData> elementPeaks = new HashMap<String, PeaksData>();

		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager
				.getConnection(url, "root", "root");
		Statement stmt = connection.createStatement();
		String sql = "select * from element_peaks.peak";
		ResultSet results = stmt.executeQuery(sql);
		while (results.next()) {
			String symbol = results.getString(1);
			String number = results.getString(2);
			String json = results.getString(3);
			JSONArray peaksArray = JSON.parseArray(json);
			double[] p = new double[peaksArray.size()];
			double[] mass = new double[peaksArray.size()];
			for (int i = 0; i != peaksArray.size(); i++) {
				JSONObject eachPeakObject = (JSONObject) peaksArray.get(i);
				String pStr = eachPeakObject.getString("p");
				String massStr = eachPeakObject.getString("mass");
				p[i] = Double.valueOf(pStr);
				mass[i] = Double.valueOf(massStr);
			}

			PeaksData data = new PeaksData(p, mass);
			elementPeaks.put(symbol + number, data);
			System.out.println();

		}
		System.out.println();
		return elementPeaks;
	}

	public class PeaksData {

		private double[] p;
		private double[] mass;

		public double[] getP() {
			return p;
		}

		public double[] getMass() {
			return mass;
		}

		public PeaksData(double[] p, double[] mass) {
			this.p = p;
			this.mass = mass;
		}
	}
}
