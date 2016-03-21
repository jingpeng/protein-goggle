package edu.tongji.proteingoggle.bll;

import org.junit.Test;

import edu.tongji.proteingoggle.dal.DataBaseDAL;
import edu.tongji.proteingoggle.dal.PeptideDAL;
import edu.tongji.proteingoggle.dal.PeptideDissociationDAL;
import edu.tongji.proteingoggle.dal.PeptideModResDAL;
import edu.tongji.proteingoggle.model.ProteinSession;

public class DataBaseBLL {
	 DataBaseDAL dataBaseDAL = new DataBaseDAL();

     private ProteinSession session = new ProteinSession();
     
     public DataBaseBLL()
     {
         session = new ProteinSession();
     }
     public DataBaseBLL(ProteinSession pSession)
     {
         session = pSession;
     }
     public DataBaseBLL(String sDataBase)
     {
         session = new ProteinSession();
         session.DataBase = sDataBase;
     }
     
     public void CreateDataBaseWithTable() throws Exception
     {
    	
         PeptideDAL peptideDAL = new PeptideDAL(session.DataBase);
         PeptideDissociationDAL peptideDissociationDAL = new PeptideDissociationDAL(session.DataBase);
         PeptideModResDAL peptideModResDAL = new PeptideModResDAL(session.DataBase);

         dataBaseDAL.CreateDataBase(session.DataBase);
         peptideDAL.CreatePeptideTable();
         peptideModResDAL.CreatePeptideModResTable();
         peptideDissociationDAL.CreatePeptideDissociatioTable();
     }
     

}
