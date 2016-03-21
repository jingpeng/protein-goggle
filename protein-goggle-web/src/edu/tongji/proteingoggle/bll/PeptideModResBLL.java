package edu.tongji.proteingoggle.bll;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import edu.tongji.proteingoggle.analysis.TraditionalMainType;
import edu.tongji.proteingoggle.dal.PeptideModResDAL;
import edu.tongji.proteingoggle.external.IPC;
import edu.tongji.proteingoggle.external.Peak;
import edu.tongji.proteingoggle.model.ModResModel;
import edu.tongji.proteingoggle.model.ProteinSession;
import edu.tongji.proteingoggle.model.ParameterModel;

public class PeptideModResBLL {
	private ProteinSession session;

	private List<ModResModel> listModRes = new ArrayList<ModResModel>();
	private Analysis analysis = new Analysis();

	public PeptideModResBLL(ProteinSession pSession) {

		session = pSession;
		
	}

	public int Check_times(String formula) {
		int times = 0;
		for (ModResModel ms : listModRes) {
			if (ms.Z == 1 && ms.Forumla == formula)
				times++;
		}
		return times;
	}

	public void Excute() throws Exception {
		int iCnt = 0;
		String sFormula = "";
		String[] arrModRes;
		List<String> list = null;
		List<String> lstSeq = session.Sequence;
		List<String> lstSeqKbn = session.SeqKbn;
		Map<String, List<String>> dictAll = session.Mod_Res;
		Map<String, Integer> components;

		PeptideModResDAL pe = new PeptideModResDAL(session.DataBase);

		for (int iSeq = 0; iSeq < lstSeq.size(); iSeq++) {
			// list = dictAll.Where(ms => ms.Key == lstSeq[iSeq]).Select(ms =>
			// ms.Value).First();
			list = dictAll.get(lstSeq.get(iSeq));
			for (int i = 0; i < list.size(); i++) {
				// 得到分子式
				arrModRes = Common.getModResForArray(list.get(i));
				components = analysis.GetFormulaComponentsBySequence(lstSeq
						.get(iSeq));
				for (String cmp : components.keySet()) {
					System.out.println(cmp + " ---- " + components.get(cmp));
				}
				sFormula = analysis.GetFormulaByComponents(components,
						arrModRes);
				System.out.println(sFormula);
				iCnt = Check_times(sFormula);
				if (iCnt == 0) {
					AnalysisByModRes(lstSeq.get(iSeq), lstSeqKbn.get(iSeq),
							list.get(i), sFormula);

					if (listModRes.size() > 1000 || i == list.size() - 1) {
						 pe.Insert_PeptideModRes(listModRes); //插入数据库
						// listModRes.Clear();
					}

					continue;
				}

				// TraditionalMainType.Calc(sFormula);
				
				for(ModResModel mrm : listModRes)  // 未测试功能 by soap
				{
					if (mrm.Forumla==sFormula && mrm.IsGet==true){
						ModResModel temp=new ModResModel();
						temp = mrm;
						temp.IsGet = false;
						temp.Mod_Res = list.get(i);
						temp.Sequence = lstSeq.get(iSeq);
						temp.SEQKBN = lstSeqKbn.get(iSeq);
						listModRes.add(temp);
					}
				}
			
				 if (listModRes.size() > 1000 || i == list.size() - 1)
				 {
				 //pe.Insert_PeptideModRes(listModRes);
				// listModRes.Clear();
				 }
			}
		}
	}

	private void AnalysisByModRes(String sSeq, String sSeqKbn, String sModRes,
			String sFormula) {
		ModResModel model;
		model = this.Calc_ValenceStateForOne(sSeq, sSeqKbn, sModRes, sFormula);
		listModRes.add(model);
		this.Calc_ValenceStateForN(model);
	}

	private ModResModel Calc_ValenceStateForOne(String sSeq, String sSeqKbn,
			String sModRes, String sFormula) {
		Double dM_Z = 0d;
		Double dM = 0d;
		List<Double> lstM_Z = new ArrayList<Double>();
		List<Double> lstM = new ArrayList<Double>();

		AnalysisParameter aParameter = new AnalysisParameter();
		aParameter.FastCalc = session.FastCalc;// 检查 session.FastCalc
												// 值配置！2014/10/25 soap 留 recheck
												// note！
		aParameter.Sequence = sSeq;
		aParameter.Formula = sFormula;
		aParameter.ValenceState = 1;
		// Console.WriteLine(sSeq);
		IPC.Results results = analysis.AnalysisCalc(aParameter);
		TreeSet<Peak> tree = results.getPeaks();

		for (Peak peak : tree) {
			if (peak.getRelInt() == 1d) {
				dM_Z = (double) (Math.round(peak.getMass() * 1000000)) / 1000000;
				dM = (double) (Math.round(peak.getRelInt() * 1000)) / 1000;
			}

			// lstM_Z.Add(Math.Round(peak.getMass(),6,MidpointRounding.AwayFromZero));
			lstM_Z.add((double) (Math.round(peak.getMass() * 1000000)) / 1000000);

			lstM.add((double) (Math.round(peak.getRelInt() * 1000)) / 1000);
		}

		ModResModel model = new ModResModel();
		model.M_Z = dM_Z;
		model.M_Z_ALL = lstM_Z;
		model.M = dM;
		model.M_ALL = lstM;
		model.Forumla = sFormula;
		model.ID = session.ID;
		model.SEQKBN = sSeqKbn;
		model.Sequence = sSeq;
		model.Mod_Res = sModRes;
		model.Z = 1;
		model.IsGet = true;
		return model;
	}

	private double CalcExpression(double dMZ, int iValenceState) {
		// return (dMZ + (iValenceState - 1) * Table.H) / iValenceState;
		double d = (dMZ + (iValenceState - 1) * Table.H) / iValenceState;
//		return Math.Round(d, 6, MidpointRounding.AwayFromZero);
		return(double) (Math.round(d * 1000000)) / 1000000;
	}

	private List<Double> M_Z_ALLSelect(List<Double> modelOne_MZALL,int i)
	{
		List<Double> temp =new ArrayList<Double>();
		for(Double mz : modelOne_MZALL)
			temp.add(CalcExpression(mz, i));
		return temp;
	}
	private void Calc_ValenceStateForN(ModResModel modelOne)
     {
         ModResModel modelN;
         double dCalc = 0d;

         for (int i = 2; ; i++)
         {
             //Console.WriteLine(modelOne.M_Z);
             dCalc = CalcExpression(modelOne.M_Z, i);
             //Console.WriteLine(dCalc);
             if (dCalc < session.DBParameter.StartRange)
                 break;

             if (dCalc > session.DBParameter.EndRange)
                 continue;

             if (dCalc >= session.DBParameter.StartRange && dCalc <= session.DBParameter.EndRange)
             {
                 modelN = new ModResModel();
                 modelN.M_Z = dCalc;
                 modelN.M_Z_ALL = M_Z_ALLSelect(modelOne.M_Z_ALL,i);
                 modelN.M = modelOne.M;
                 modelN.M_ALL = modelOne.M_ALL;
                 modelN.Forumla = modelOne.Forumla;
                 modelN.ID = modelOne.ID;
                 modelN.SEQKBN = modelOne.SEQKBN;
                 modelN.Sequence = modelOne.Sequence;
                 modelN.Mod_Res = modelOne.Mod_Res;
                 modelN.Z = i;
                 modelN.IsGet = true;
                 listModRes.add(modelN);
             }
         }
     }
	
	public ParameterModel ParameterModel;
	
}
