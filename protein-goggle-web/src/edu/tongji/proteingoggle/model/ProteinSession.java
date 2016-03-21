package edu.tongji.proteingoggle.model;

import java.util.List;
import java.util.Map;

public class ProteinSession {
	 public String ID ;
     public List<String> Sequence ;
     public List<String> SeqKbn ;
     public int ValenceState ;
     public int FastCalc ;
     public Map<String, List<String>> Mod_Res ;
     public int Mod_Res_Count ;
     public DataBaseParameter DBParameter ;
     public String DataBase ;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public List<String> getSequence() {
		return Sequence;
	}
	public void setSequence(List<String> sequence) {
		Sequence = sequence;
	}
	public List<String> getSeqKbn() {
		return SeqKbn;
	}
	public void setSeqKbn(List<String> seqKbn) {
		SeqKbn = seqKbn;
	}
	public int getValenceState() {
		return ValenceState;
	}
	public void setValenceState(int valenceState) {
		ValenceState = valenceState;
	}
	public int getFastCalc() {
		return FastCalc;
	}
	public void setFastCalc(int fastCalc) {
		FastCalc = fastCalc;
	}
	public Map<String, List<String>> getMod_Res() {
		return Mod_Res;
	}
	public void setMod_Res(Map<String, List<String>> mod_Res) {
		Mod_Res = mod_Res;
	}
	public int getMod_Res_Count() {
		return Mod_Res_Count;
	}
	public void setMod_Res_Count(int mod_Res_Count) {
		Mod_Res_Count = mod_Res_Count;
	}
	public DataBaseParameter getDBParameter() {
		return DBParameter;
	}
	public void setDBParameter(DataBaseParameter dBParameter) {
		DBParameter = dBParameter;
	}
	public String getDataBase() {
		return DataBase;
	}
	public void setDataBase(String dataBase) {
		DataBase = dataBase;
	}
     
}
