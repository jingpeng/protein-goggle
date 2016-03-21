package edu.tongji.proteingoggle.bll;


public class AnalysisParameter{
	public String Formula;
	public String Sequence;
	public int ValenceState ;

    public long FastCalc ;

	public String getFormula() {
		return Formula;
	}

	public void setFormula(String formula) {
		Formula = formula;
	}

	public String getSequence() {
		return Sequence;
	}

	public void setSequence(String sequence) {
		Sequence = sequence;
	}

	public int getValenceState() {
		return ValenceState;
	}

	public void setValenceState(int valenceState) {
		ValenceState = valenceState;
	}

	public long getFastCalc() {
		return FastCalc;
	}

	public void setFastCalc(long fastCalc) {
		FastCalc = fastCalc;
	}
    
}

