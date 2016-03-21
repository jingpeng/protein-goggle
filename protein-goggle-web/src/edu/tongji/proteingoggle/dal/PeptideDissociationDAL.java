package edu.tongji.proteingoggle.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import edu.tongji.proteingoggle.model.DissociationModel;

public class PeptideDissociationDAL {

	private final String sTableName = "Fragment_ions";
	String url = "jdbc:mysql://127.0.0.1:3306/";
	static String dataBase = "";

	public PeptideDissociationDAL(String sDataBase) {
		dataBase = sDataBase;
		url+= dataBase;
	}

	public void Insert_PeptideDissociation(List<DissociationModel> listModel)
			throws ClassNotFoundException, SQLException {
		StringBuilder sb = new StringBuilder();
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(url, "root", "root");
		stmt = conn.createStatement();
		for (DissociationModel model : listModel) {
			sb.setLength(0);
			sb.append(String.format("INSERT INTO %s", sTableName));
			sb.append(" VALUES (");

			sb.append(String.format("'%s',", model.ID));
			sb.append(String.format("'%s',", model.SEQKBN));
			sb.append(String.format("'%s',", model.Mod_Res));
			sb.append(String.format("'%s',", model.SEQ));
			sb.append(String.format("'%s&%s',", model.HCD_M_Z_Start,
					model.HCD_M_Z_End));
			sb.append(String.format("'%s&%s',", model.HCD_M_Start,
					model.HCD_M_End));
			sb.append(String.format("'%s&%s',", model.ETD_M_Z_Start,
					model.ETD_M_Z_End));
			sb.append(String.format("'%s&%s',", model.ETD_M_Start,
					model.ETD_M_End));
			sb.append(String.format("'%s&%s',", model.HCD_MAX_MZ_Start,
					model.HCD_MAX_MZ_End));
			sb.append(String.format("'%s&%s');", model.ETD_MAX_MZ_Start,
					model.ETD_MAX_MZ_End));
			System.out.println(sb.toString());
			stmt.executeUpdate(sb.toString());
		}
	}

	public void CreatePeptideDissociatioTable() throws ClassNotFoundException,
			SQLException {
		StringBuilder sb = new StringBuilder();
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(url, "root", "root");
		stmt = conn.createStatement();
		sb.append(String.format("CREATE TABLE %s (", sTableName));
		sb.append("   ID VARCHAR(50) NOT NULL,");
		sb.append("   AA_VARIATION VARCHAR(200) NOT NULL,");
		sb.append("   MOD_RES VARCHAR(300) NOT NULL,");
		sb.append("   SEQ TEXT NOT NULL,");
		sb.append("   CID_MZ LONGTEXT NOT NULL,");
		sb.append("   CID_Relative_Abundance LONGTEXT NOT NULL,");
		sb.append("   ETD_MZ LONGTEXT NOT NULL,");
		sb.append("   ETD_Relative_Abundance LONGTEXT NOT NULL,");
		sb.append("   CID_MZ_MAX LONGTEXT NOT NULL,");
		sb.append("   ETD_MZ_MAX LONGTEXT NOT NULL,");
		sb.append(" PRIMARY KEY (ID,AA_VARIATION,MOD_RES)");
		sb.append(") ENGINE = MYISAM DEFAULT CHARSET=latin1;");

		stmt.executeUpdate(sb.toString());

		sb.setLength(0);
		sb.append(String.format(
				"ALTER TABLE %s ADD INDEX MOD_RES_IDX(MOD_RES)", sTableName));
		stmt.executeUpdate(sb.toString());

		// sb.Clear();
		// sb.AppendFormat("ALTER TABLE {0} ADD INDEX SEQ_IDX(SEQ)",
		// sTableName);
		// sqlHelper.ExecuteSql(sb.ToString());
	}

}
