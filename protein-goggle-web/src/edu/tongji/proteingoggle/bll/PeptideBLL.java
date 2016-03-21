package edu.tongji.proteingoggle.bll;

import java.sql.SQLException;
import java.util.Map;

import edu.tongji.proteingoggle.dal.PeptideDAL;
import edu.tongji.proteingoggle.model.ProteinSession;

public class PeptideBLL {
	private ProteinSession session;
    private PeptideDAL peDAL;

    public PeptideBLL(ProteinSession pSession)
    {
        session = pSession;
        peDAL = new PeptideDAL(session.DataBase);
    }
    
    public PeptideBLL(String sDataBase)
    {
        peDAL = new PeptideDAL(sDataBase);
    }
    
    public void UpdateCreateTime(String startTime, String endTime) throws ClassNotFoundException, SQLException
    {
        peDAL.UpdateCreateTime(startTime, endTime);
    }
    
    public void Excute(Map<String, String> dict) throws ClassNotFoundException, SQLException
    {
        peDAL.InsertPeptide(dict);
    }

}
