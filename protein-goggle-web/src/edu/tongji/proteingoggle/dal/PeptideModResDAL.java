package edu.tongji.proteingoggle.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.tongji.proteingoggle.model.ModResModel;

public class PeptideModResDAL {
	private final String sTableName = "Parent_ions";

	String url = "jdbc:mysql://127.0.0.1:3306/";

	public PeptideModResDAL(String sDataBase) {
		url += sDataBase;
	}

	public void CreatePeptideModResTable() throws ClassNotFoundException,
			SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		Statement stmt = null;
		conn = DriverManager.getConnection(url, "root", "root");

		StringBuilder sb = new StringBuilder();

		sb.append(String.format("CREATE TABLE %s (", sTableName));
		sb.append("   ID VARCHAR(50) NOT NULL,");
		sb.append("   AA_VARIATION VARCHAR(200) NOT NULL,");
		sb.append("   MOD_RES VARCHAR(300) NOT NULL,");
		sb.append("   Z INT(10) UNSIGNED NOT NULL,");
		sb.append("   SEQ TEXT NOT NULL,");
		sb.append("   MZ DOUBLE NOT NULL,");
		sb.append("   MZ_ALL VARCHAR(1000) NOT NULL,");
		sb.append("   Relative_Abundance_ALL VARCHAR(1000) NOT NULL,");
		sb.append("   FORUMLA VARCHAR(100) NOT NULL,");
		sb.append(" PRIMARY KEY (ID,AA_VARIATION,MOD_RES,Z)");
		sb.append(") ENGINE = MYISAM DEFAULT CHARSET=latin1;");
		stmt = conn.createStatement();
		stmt.executeUpdate(sb.toString());
		stmt.executeUpdate(String.format("ALTER TABLE %s ADD INDEX MZ_IDX(MZ)",
				sTableName));
	}

	public void Insert_PeptideModRes(List<ModResModel> listModel) throws SQLException, ClassNotFoundException
     {
		 Class.forName("com.mysql.jdbc.Driver");
		 Connection conn = null;
         Statement stmt = null;
		 conn = DriverManager.getConnection(url, "root", "root");
		 stmt = conn.createStatement();
         StringBuilder sbHead = new StringBuilder();
         StringBuilder sb = new StringBuilder();
         int iCnt = 1;

//         sbHead.append(String.format("INSERT INTO {0}", sTableName);
         sbHead.append(String.format("INSERT INTO %s", sTableName));
//         sbHead.append(String.format("({0},{1},{2},{3},{4},{5},{6},{7},{8})", "ID", "AA_VARIATION", "MOD_RES", "Z", "SEQ", "MZ", "MZ_ALL", "Relative_Abundance_ALL", "FORUMLA");
         sbHead.append(String.format("(%s,%s,%s,%s,%s,%s,%s,%s,%s)","ID", "AA_VARIATION", "MOD_RES", "Z", "SEQ", "MZ", "MZ_ALL", "Relative_Abundance_ALL", "FORUMLA"));
         
         sbHead.append(" VALUES ");

         for (ModResModel model : listModel)
         {

             if (iCnt == 500 || listModel.size() == iCnt)
             {
                // sb.append(String.format("('{0}',", model.ID);
                 sb.append(String.format("('%s',", model.ID));
                 sb.append(String.format("'%s',", model.SEQKBN));
                 sb.append(String.format("'%s',", model.Mod_Res));
                 sb.append(String.format("%s,", model.Z));
                 sb.append(String.format("'%s',", model.Sequence));
                 sb.append(String.format("%s,", model.M_Z));
                 sb.append(String.format("'%s',", StringUtils.join(model.M_Z_ALL.toArray(),',')));
                 sb.append(String.format("'%s',", StringUtils.join(model.M_ALL.toArray(),',')));
                 sb.append(String.format("'%s');", model.Forumla));
                 stmt.executeUpdate(sbHead.toString() + sb.toString());
                 sb.delete( 0, sb.length() );
             }
             else
             {
                 sb.append(String.format("('%s',", model.ID));
                 sb.append(String.format("'%s',", model.SEQKBN));
                 sb.append(String.format("'%s',", model.Mod_Res));
                 sb.append(String.format("%s,", model.Z));
                 sb.append(String.format("'%s',", model.Sequence));
                 sb.append(String.format("%s,", model.M_Z));
                 sb.append(String.format("'%s',", StringUtils.join(model.M_Z_ALL.toArray(),',')));
                 sb.append(String.format("'%s',", StringUtils.join(model.M_ALL.toArray(),',')));
                 sb.append(String.format("'%s'),", model.Forumla));
             }

             iCnt++;
         }
     }
}
