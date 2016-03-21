package edu.tongji.proteingoggle.bll;

import java.util.HashMap;
import java.util.Map;

import edu.tongji.proteingoggle.external.IPC;

public class Analysis {

	private static Map<String, Map<String, Integer>> aminoAcidTable;
	private Map<String, Map<String, Integer>> modResAminoAcidTable;
	private Map<String, String> modResSymbolTable;
	private Table table = new Table();

	public Analysis() {
		aminoAcidTable = Table.makeAminoAcidsTable();
		modResAminoAcidTable = Table.ModResAminoAcidsTable;
		// modResSymbolTable = Table.ModResSymbolTable;
	}

	public Map<String, Integer> GetFormulaComponentsBySequence(String sSeq) {
		Map<String, Integer> components = new HashMap<String, Integer>();
		Map<String, Integer> keys;
		char[] arrSeq = sSeq.toCharArray();

		for (char cSeq : arrSeq) {
			keys = aminoAcidTable.get(String.valueOf(cSeq));

			for (String key : keys.keySet()) {
				if (components.containsKey(key) == true)
					components
							.put(key,
									components.get(key)
											+ aminoAcidTable.get(
													String.valueOf(cSeq)).get(
													key));
				else
					components.put(key, aminoAcidTable
							.get(String.valueOf(cSeq)).get(key));
			}
		}
		components.put("H", components.get("H") - (sSeq.length() - 1) * 2);
		components.put("O", components.get("O") - (sSeq.length() - 1));
		components.put("H", components.get("H") + 1);
		// components["H"] = (int)components["H"] - (sSeq.Count() - 1) * 2;
		// components["O"] = (int)components["O"] - (sSeq.Count() - 1);
		// components["H"] = (int)components["H"] + 1;

		return components;
	}

	public String GetFormulaByComponents(Map<String, Integer> components,
			String[] strModResValue) {
		StringBuilder sb = new StringBuilder();
		Map<String, Integer> dict = new HashMap<String, Integer>(components);
		// Map<String, Integer>.KeyCollection keys;
		String[] keys;
		  for (String sModResValue : strModResValue)
          {
              keys = (String[]) modResAminoAcidTable.get(sModResValue).keySet().toArray();

//              foreach (string key in keys)
              for (String key :keys)
              {
                  if (dict.keySet().contains(key) == true)
//                      dict[key] = (int)dict[key] + (int)modResAminoAcidTable[sModResValue][key];
                	  dict.put(key, (int)dict.get(key) + (int)modResAminoAcidTable.get(sModResValue).get(key));
                  else
//                      dict[key] = (int)modResAminoAcidTable[sModResValue][key];
                	  dict.put(key,(int)modResAminoAcidTable.get(sModResValue).get(key));
              }
          } //function need completing , leave by soap

		for (String key : dict.keySet())
			sb.append(key + dict.get(key));

		return sb.toString();
	}

	public Map<String, Integer> GetFormulaComponentsByComponents(
			Map<String, Integer> components, Map<String, Integer> dictNew) {

		if (dictNew == null)
			return components;

		Map<String, Integer> dict = new HashMap<String, Integer>(components);
		// Dictionary<string, int>.KeyCollection keys = dictNew.Keys;

		for (String key : dictNew.keySet()) {
			if (dict.keySet().contains(key) == true)
//				dict[key] = (int) dict[key] + (int) dictNew[key];
				dict.put(key, dict.get(key)+dictNew.get(key));
			else
//				dict[key] = (int) dictNew[key];
				dict.put(key, dictNew.get(key));
		}

		return dict;
	}

	public IPC.Results AnalysisCalc(AnalysisParameter oParameter) {
		// IPC isotopePatternCalc = new IPC(ele);
		IPC isotopePatternCalc = new IPC();
		IPC.Options option = new IPC.Options();

		option.parseChemFormulaAndAdd(oParameter.Formula);
		option.setFastCalc(oParameter.FastCalc);
		option.setPrintOutput(true);
		option.setBinPeaks(IPC.binningType.FIXED_BINNING);
		option.setCharge(oParameter.ValenceState);

		IPC.Results result = isotopePatternCalc.execute(option);
		return result;
	}

}
