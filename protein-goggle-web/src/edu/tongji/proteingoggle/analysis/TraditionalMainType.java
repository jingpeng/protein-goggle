package edu.tongji.proteingoggle.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

import edu.tongji.proteingoggle.external.*;
import edu.tongji.proteingoggle.external.IPC.Results;
import edu.tongji.proteingoggle.external.IPC.Options;

public class TraditionalMainType {

	public static void Calc(String formula) throws Exception {
		IPC isotopePatternCalc = new IPC();

		Options option = new Options();
		option.parseChemFormulaAndAdd(formula);
		option.setFastCalc(100);
		option.setPrintOutput(true);
		option.setBinPeaks(IPC.binningType.FIXED_BINNING);
		option.setCharge(1);
 
		Results result = isotopePatternCalc.execute(option);
		
		FileWriter writer1 = new FileWriter(new File("E:/1_1.txt"));
		FileWriter writer2 = new FileWriter(new File("E:/2_1.txt"));
		BufferedWriter bw1 = new BufferedWriter(writer1);
		BufferedWriter bw2 = new BufferedWriter(writer2);

		double totalP = 0;
		for (Peak peak : result.getPeaks()) {
			bw1.write(String.valueOf(peak.getMass()));
			bw1.newLine();
			bw2.write(String.valueOf(peak.getP()));
			bw2.newLine();
			totalP += peak.getP();
		}

		bw1.close();
		bw2.close();
		System.out.println(totalP);
	}
}
