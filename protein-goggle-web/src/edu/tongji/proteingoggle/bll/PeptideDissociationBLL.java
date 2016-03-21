package edu.tongji.proteingoggle.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;

import edu.tongji.proteingoggle.dal.ElementPeaksDAL.PeaksData;
import edu.tongji.proteingoggle.dal.PeptideDissociationDAL;
import edu.tongji.proteingoggle.external.IPC;
import edu.tongji.proteingoggle.external.Peak;
import edu.tongji.proteingoggle.gpu.GPUSolution;
import edu.tongji.proteingoggle.model.DissociationModel;
import edu.tongji.proteingoggle.model.ProteinSession;
import edu.tongji.proteingoggle.concurrent.ConcurrentCal;

public class PeptideDissociationBLL {
	private ProteinSession session;
	private PeptideDissociationDAL peptideDissociationDAL;
	private Analysis analysis = new Analysis();

	private List<DissociationModel> lstDissociation = new ArrayList<DissociationModel>();

	// Remember MZ M MaxMZ
	private Map<String, String> dictForumlaForMZ = new HashMap<String, String>();
	private Map<String, String> dictForumlaForM = new HashMap<String, String>();
	private Map<String, String> dictForumlaForMaxMZ = new HashMap<String, String>();
	private List<AnalysisParameter> listParameters;
	//
	private List<String> lstStartHCD_MZ = new ArrayList<String>();
	private List<String> lstStartHCD_M = new ArrayList<String>();
	private List<String> lstStartETD_MZ = new ArrayList<String>();
	private List<String> lstStartETD_M = new ArrayList<String>();
	private List<String> lstEndHCD_MZ = new ArrayList<String>();
	private List<String> lstEndHCD_M = new ArrayList<String>();
	private List<String> lstEndETD_MZ = new ArrayList<String>();
	private List<String> lstEndETD_M = new ArrayList<String>();
	private List<String> lstStartHCD_MAX_MZ = new ArrayList<String>();
	private List<String> lstEndHCD_MAX_MZ = new ArrayList<String>();
	private List<String> lstStartETD_MAX_MZ = new ArrayList<String>();
	private List<String> lstEndETD_MAX_MZ = new ArrayList<String>();
	private List<IPC.Results> listResults = new ArrayList<IPC.Results>();
	private int ProteinCount;

	public int getProteinCount() {
		return ProteinCount;
	}

	public void setProteinCount(int proteinCount) {
		ProteinCount = proteinCount;
	}

	public PeptideDissociationBLL(String sDataBaseName) {
		peptideDissociationDAL = new PeptideDissociationDAL(sDataBaseName);
	}

	public PeptideDissociationBLL(ProteinSession pSession) {
		session = pSession;
		peptideDissociationDAL = new PeptideDissociationDAL(session.DataBase);
	}

	public void Excute(Map<String, PeaksData> elementPeaks)
			throws ClassNotFoundException, SQLException, InterruptedException, ExecutionException {
		List<String> lstSeq = session.Sequence;
		List<String> lstSeqKbn = session.SeqKbn;
		Map<String, List<String>> dictAll = session.Mod_Res;

		int allCnt = 0;
		int avgCnt = 0;
		int currentCnt = 0;
		int sum = 0;
		for (int i = 0; i < lstSeq.size(); i++)
			sum += lstSeq.get(i).length();

		if (lstSeq.size() != 0 && dictAll.size() != 0) {
			// allCnt = lstSeq.Sum(ms => ms.Length) *
			// dictAll.ElementAt(0).Value.Count * this.ProteinCount;
			allCnt = sum * dictAll.get(dictAll.keySet().iterator().next()).size() * this.getProteinCount();
			// avgCnt = allCnt / 95 == 0 ? 1 : allCnt / 95;
			allCnt = allCnt / 95 == 0 ? 1 : allCnt / 95;
		}

		for (int iSeq = 0; iSeq < lstSeq.size(); iSeq++) {
			// var list = dictAll.Where(ms => ms.Key == lstSeq[iSeq]).Select(ms
			// => ms.Value).First();
			List<String> list = dictAll.get(lstSeq.get(iSeq));

			for (int i = 0; i < list.size(); i++) {
				AnalysisByDissociation(lstSeq.get(iSeq), lstSeqKbn.get(iSeq), list.get(i), avgCnt, currentCnt,
						elementPeaks);

				if (lstDissociation.size() > 0) {
					peptideDissociationDAL.Insert_PeptideDissociation(lstDissociation);
					lstDissociation.clear();
				}
			}
		}

	}

	private void ConCurrentCalc(Map<String, PeaksData> elementPeaks) throws InterruptedException, ExecutionException {

		listResults = ConcurrentCal.excuteForkJoinAnalysisCalc(analysis, listParameters);
		// GPUSolution solution = new GPUSolution();
		// listResults = solution.excuteGPUAnalysisCalc(analysis,
		// listParameters,
		// elementPeaks);

		System.out.println(listParameters.size());

		for (int i = 0; i < listResults.size(); i++) {
			IPC.Results results = listResults.get(i);
			String sFormula = listParameters.get(i).Formula;
			TreeSet<Peak> tree = results.getPeaks();
			double dM_Z = 0d;
			List<Double> lstM_Z = new ArrayList<Double>();
			List<Double> lstM = new ArrayList<Double>();
			for (Peak peak : tree) {
				if (peak.getRelInt() == 1d) {
					// dM_Z = Math.Round(peak.getMass(), 6,
					// MidpointRounding.AwayFromZero);
					dM_Z = (double) (Math.round(peak.getMass() * 1000000)) / 1000000;

				}

				// lstM_Z.Add(Math.Round(peak.getMass(), 6,
				// MidpointRounding.AwayFromZero));
				// lstM.Add(Math.Round(peak.getRelInt(), 3,
				// MidpointRounding.AwayFromZero));
				lstM_Z.add((double) (Math.round(peak.getMass() * 1000000)) / 1000000);
				lstM.add((double) (Math.round(peak.getRelInt() * 1000)) / 1000);
			}
			System.out.println(sFormula + "^^^^^^   " + StringUtils.join(lstM_Z, ','));
			dictForumlaForMZ.put(sFormula, StringUtils.join(lstM_Z, ','));
			dictForumlaForM.put(sFormula, StringUtils.join(lstM, ','));
			dictForumlaForMaxMZ.put(sFormula, String.valueOf(dM_Z));
		}

	}

	private void AnalysisByDissociation(String sSeq, String sSeqKbn, String sModRes, int avgCnt, int currentCnt,
			Map<String, PeaksData> elementPeaks) throws InterruptedException, ExecutionException {
		String sFormula = "";
		Map<String, String> dictFormula;
		listParameters = new ArrayList<AnalysisParameter>();
		this.lstStartETD_MZ.clear();
		this.lstStartETD_M.clear();
		this.lstEndETD_MZ.clear();
		this.lstEndETD_M.clear();
		this.lstStartHCD_MZ.clear();
		this.lstStartHCD_M.clear();
		this.lstEndHCD_MZ.clear();
		this.lstEndHCD_M.clear();
		this.lstStartHCD_MAX_MZ.clear();
		this.lstEndHCD_MAX_MZ.clear();
		this.lstStartETD_MAX_MZ.clear();
		this.lstEndETD_MAX_MZ.clear();
		List<String> FormulaRecord = new ArrayList<String>();
		int iLength = sSeq.length();
		for (int iSeq = 1; iSeq <= iLength - 1; iSeq++) {
			// 得到四个分子式(0:ETD_Start,1:ETD_End,2:HCD_Start,3:HCD_End)
			dictFormula = this.getFormulaByModRes(sSeq, sModRes, iSeq);

			// ETD_Start
			sFormula = dictFormula.get("StartETD");
			this.CalcFormula(sSeq, sFormula);
			FormulaRecord.add(sFormula);
			// this.lstStartETD_MZ.add(String.format("C%s;MZ=%s", iSeq,
			// dictForumlaForMZ.get(sFormula)));
			// this.lstStartETD_M.add(String.format("C%s;M=%s", iSeq,
			// dictForumlaForM.get(sFormula)));
			// this.lstStartETD_MAX_MZ.add(String.format("C%s;MAX_MZ=%s", iSeq,
			// dictForumlaForMaxMZ.get(sFormula)));

			// ETD_End
			sFormula = dictFormula.get("EndETD");
			this.CalcFormula(sSeq, sFormula);
			FormulaRecord.add(sFormula);
			// this.lstEndETD_MZ.add(String.format("Z%s;MZ=%s", iLength - iSeq,
			// dictForumlaForMZ.get(sFormula)));
			// this.lstEndETD_M.add(String.format("Z%s;M=%s", iLength - iSeq,
			// dictForumlaForM.get(sFormula)));
			// this.lstEndETD_MAX_MZ.add(String.format("Z%s;MAX_MZ=%s", iLength
			// - iSeq, dictForumlaForMaxMZ.get(sFormula)));
			// this.lstEndETD_MZ.Add(String.Format("Z{0};MZ={1}", iLength -
			// iSeq, dictForumlaForMZ[sFormula]));
			// this.lstEndETD_M.Add(String.Format("Z{0};M={1}", iLength - iSeq,
			// dictForumlaForM[sFormula]));
			// this.lstEndETD_MAX_MZ.Add(String.Format("Z{0};MAX_MZ={1}",
			// iLength - iSeq, dictForumlaForMaxMZ[sFormula]));

			// HCD_Start
			sFormula = dictFormula.get("StartHCD");
			this.CalcFormula(sSeq, sFormula);
			FormulaRecord.add(sFormula);
			// this.lstStartHCD_MZ.add(String.format("B%s;MZ=%s", iSeq,
			// dictForumlaForMZ.get(sFormula)));
			// this.lstStartHCD_M.add(String.format("B%s;M=%s", iSeq,
			// dictForumlaForM.get(sFormula)));
			// this.lstStartHCD_MAX_MZ.add(String.format("B%s;MAX_MZ=%s", iSeq,
			// dictForumlaForMaxMZ.get(sFormula)));

			// this.lstStartHCD_MZ.Add(String.Format("B{0};MZ={1}", iSeq,
			// dictForumlaForMZ[sFormula]));
			// this.lstStartHCD_M.Add(String.Format("B{0};M={1}", iSeq,
			// dictForumlaForM[sFormula]));
			// this.lstStartHCD_MAX_MZ.Add(String.Format("B{0};MAX_MZ={1}",
			// iSeq, dictForumlaForMaxMZ[sFormula]));

			// HCD_End
			sFormula = dictFormula.get("EndHCD");
			this.CalcFormula(sSeq, sFormula);
			FormulaRecord.add(sFormula);
			// this.lstEndHCD_MZ.add(String.format("Y%s;MZ=%s", iLength - iSeq,
			// dictForumlaForMZ.get(sFormula)));
			// this.lstEndHCD_M.add(String.format("Y%s;M=%s", iLength - iSeq,
			// dictForumlaForM.get(sFormula)));
			// this.lstEndHCD_MAX_MZ.add(String.format("Y%s;MAX_MZ=%s", iLength
			// - iSeq, dictForumlaForMaxMZ.get(sFormula)));

			// this.lstEndHCD_MZ.Add(String.Format("Y{0};MZ={1}", iLength -
			// iSeq, dictForumlaForMZ[sFormula]));
			// this.lstEndHCD_M.Add(String.Format("Y{0};M={1}", iLength - iSeq,
			// dictForumlaForM[sFormula]));
			// this.lstEndHCD_MAX_MZ.Add(String.Format("Y{0};MAX_MZ={1}",
			// iLength - iSeq, dictForumlaForMaxMZ[sFormula]));

			currentCnt++;
			// if (this.ProgressBra.Value < 100 && currentCnt % avgCnt == 0)
			// this.ProgressBra.Value += 1;
		}

		ConCurrentCalc(elementPeaks);
		int count = 0;
		for (int iSeq = 1; iSeq <= iLength - 1; iSeq++) {
			sFormula = FormulaRecord.get(count);
			count++;
			this.lstStartETD_MZ.add(String.format("C%s;MZ=%s", iSeq, dictForumlaForMZ.get(sFormula)));
			this.lstStartETD_M.add(String.format("C%s;M=%s", iSeq, dictForumlaForM.get(sFormula)));
			this.lstStartETD_MAX_MZ.add(String.format("C%s;MAX_MZ=%s", iSeq, dictForumlaForMaxMZ.get(sFormula)));
			sFormula = FormulaRecord.get(count);
			count++;
			this.lstEndETD_MZ.add(String.format("Z%s;MZ=%s", iLength - iSeq, dictForumlaForMZ.get(sFormula)));
			this.lstEndETD_M.add(String.format("Z%s;M=%s", iLength - iSeq, dictForumlaForM.get(sFormula)));
			this.lstEndETD_MAX_MZ
					.add(String.format("Z%s;MAX_MZ=%s", iLength - iSeq, dictForumlaForMaxMZ.get(sFormula)));
			sFormula = FormulaRecord.get(count);
			count++;
			System.out.println(sFormula + "=====" + dictForumlaForMZ.get(sFormula));
			this.lstStartHCD_MZ.add(String.format("B%s;MZ=%s", iSeq, dictForumlaForMZ.get(sFormula)));
			this.lstStartHCD_M.add(String.format("B%s;M=%s", iSeq, dictForumlaForM.get(sFormula)));
			this.lstStartHCD_MAX_MZ.add(String.format("B%s;MAX_MZ=%s", iSeq, dictForumlaForMaxMZ.get(sFormula)));
			sFormula = FormulaRecord.get(count);
			count++;
			this.lstEndHCD_MZ.add(String.format("Y%s;MZ=%s", iLength - iSeq, dictForumlaForMZ.get(sFormula)));
			this.lstEndHCD_M.add(String.format("Y%s;M=%s", iLength - iSeq, dictForumlaForM.get(sFormula)));
			this.lstEndHCD_MAX_MZ
					.add(String.format("Y%s;MAX_MZ=%s", iLength - iSeq, dictForumlaForMaxMZ.get(sFormula)));
		}
		DissociationModel model = new DissociationModel();
		model.ID = session.ID;
		model.SEQ = sSeq;
		model.SEQKBN = sSeqKbn;
		model.Mod_Res = sModRes;

		// ETD_Start(0:M/Z,1:M) ETD_End(2:M/Z,3:M) HCD_Start(4:M/Z,5:M)
		// HCD_End(6:M/Z,7:M)
		model.ETD_M_Z_Start = StringUtils.join(this.lstStartETD_MZ, '&');
		model.ETD_M_Start = StringUtils.join(this.lstStartETD_M, '&');
		model.ETD_M_Z_End = StringUtils.join(this.lstEndETD_MZ, '&');
		model.ETD_M_End = StringUtils.join(this.lstEndETD_M, '&');
		model.HCD_M_Z_Start = StringUtils.join(this.lstStartHCD_MZ, '&');
		model.HCD_M_Start = StringUtils.join(this.lstStartHCD_M, '&');
		model.HCD_M_Z_End = StringUtils.join(this.lstEndHCD_MZ, '&');
		model.HCD_M_End = StringUtils.join(this.lstEndHCD_M, '&');
		model.ETD_MAX_MZ_Start = StringUtils.join(this.lstStartETD_MAX_MZ, '&');
		model.ETD_MAX_MZ_End = StringUtils.join(this.lstEndETD_MAX_MZ, '&');
		model.HCD_MAX_MZ_Start = StringUtils.join(this.lstStartHCD_MAX_MZ, '&');
		model.HCD_MAX_MZ_End = StringUtils.join(this.lstEndHCD_MAX_MZ, '&');

		lstDissociation.add(model);
	}

	private void CalcFormula(String sSeq, String sFormula) {
		// if (dictForumlaForMZ.Where(ms => ms.Key == sFormula).Count() != 0)
		if (dictForumlaForMZ.keySet().contains(sFormula))
			if (dictForumlaForMZ.get(sFormula).length() != 0)
				return;

		// double dM_Z = 0d;
		// List<Double> lstM_Z = new ArrayList<Double>();
		// List<Double> lstM = new ArrayList<Double>();

		// 计算1价的M/Z,M
		AnalysisParameter aParameter = new AnalysisParameter();
		aParameter.FastCalc = session.FastCalc;
		aParameter.Sequence = sSeq;
		aParameter.Formula = sFormula;
		aParameter.ValenceState = 1;

		listParameters.add(aParameter);
		//
		// IPC.Results results = analysis.AnalysisCalc(aParameter);
		// TreeSet<Peak> tree = results.getPeaks();
		//
		// for (Peak peak : tree)
		// {
		// if (peak.getRelInt() == 1d)
		// {
		// // dM_Z = Math.Round(peak.getMass(), 6,
		// MidpointRounding.AwayFromZero);
		// dM_Z = (double) (Math.round(peak.getMass() * 1000000)) / 1000000;
		//
		// }
		//
		// // lstM_Z.Add(Math.Round(peak.getMass(), 6,
		// MidpointRounding.AwayFromZero));
		// // lstM.Add(Math.Round(peak.getRelInt(), 3,
		// MidpointRounding.AwayFromZero));
		// lstM_Z.add((double) (Math.round(peak.getMass() * 1000000)) /
		// 1000000);
		// lstM.add((double) (Math.round(peak.getRelInt() * 1000)) / 1000);
		// }
		//
		// dictForumlaForMZ.put(sFormula, StringUtils.join(lstM_Z,','));
		// dictForumlaForM.put(sFormula, StringUtils.join(lstM,','));
		// dictForumlaForMaxMZ.put(sFormula, String.valueOf(dM_Z));
	}

	private Map<String, String> getFormulaByModRes(String sSeq, String sModRes, int iLocation) {
		String sStartSeq = sSeq.substring(0, iLocation);
		String sEndSeq = sSeq.substring(iLocation);

		// 返回ETD_Start,ETD_End,HCD_Start,HCD_End的分子式
		Map<String, String> dictFormula = new HashMap<String, String>();

		// 每个位置的 增加的元素
		Map<Integer, String> dictLocation = Common.getModResForDictionay(sModRes);

		// 解离前面的增加元素集合
		// String[] sArrStart = dictLocation.Where(n => n.Key <=
		// iLocation).ToDictionary(n => n.Key, n => n.Value).Values.ToArray();
		List<String> ass = new ArrayList<String>();
		for (Integer it : dictLocation.keySet()) {
			if (it <= iLocation) {
				ass.add(dictLocation.get(it));
			}
		}
		String[] sArrStart = ass.toArray(new String[ass.size()]);

		// 解离前面的元素容器
		Map<String, Integer> compontStart = analysis.GetFormulaComponentsBySequence(sStartSeq);

		// 解离前面的增加元素容器
		// String[] sArrEnd = dictLocation.Where(n => n.Key >
		// iLocation).ToDictionary(n => n.Key, n => n.Value).Values.ToArray();
		ass = new ArrayList<String>();
		for (Integer it : dictLocation.keySet()) {
			if (it > iLocation) {
				ass.add(dictLocation.get(it));
			}
		}
		String[] sArrEnd = ass.toArray(new String[ass.size()]);
		// 解离后面的元素容器
		Map<String, Integer> compontEnd = analysis.GetFormulaComponentsBySequence(sEndSeq);

		// ETD-Start Cx
		Map<String, Integer> compontStartETD = analysis.GetFormulaComponentsByComponents(compontStart,
				Table.makeETDCXTable());
		dictFormula.put("StartETD", analysis.GetFormulaByComponents(compontStartETD, sArrStart));

		// ETD-End Zy
		Map<String, Integer> compontEndETD = analysis.GetFormulaComponentsByComponents(compontEnd,
				Table.makeETDYZTable());
		dictFormula.put("EndETD", analysis.GetFormulaByComponents(compontEndETD, sArrEnd));

		// HCD-Start Bx
		Map<String, Integer> compontStartHCD = analysis.GetFormulaComponentsByComponents(compontStart,
				Table.makeHCDBXTable());
		dictFormula.put("StartHCD", analysis.GetFormulaByComponents(compontStartHCD, sArrStart));

		// HCD-End Yy
		Map<String, Integer> compontEndHCD = analysis.GetFormulaComponentsByComponents(compontEnd, null);
		dictFormula.put("EndHCD", analysis.GetFormulaByComponents(compontEndHCD, sArrEnd));

		return dictFormula;
	}

}
