package edu.tongji.proteingoggle.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;

import edu.tongji.proteingoggle.external.*;
import edu.tongji.proteingoggle.external.IPC.Results;
import edu.tongji.proteingoggle.external.IPC.Options;

public class MatrixMainType {
	public static void main(String[] args) throws IOException {

		TreeSet<Peak> C_Peaks;
		TreeSet<Peak> S_Peaks;
		TreeSet<Peak> H_Peaks;
		TreeSet<Peak> N_Peaks;
		TreeSet<Peak> O_Peaks;
		{
			Results results;
			IPC isotopePatternCalc = new IPC();
			Options option = new Options();

			option.parseChemFormulaAndAdd("C1228");
			option.setFastCalc(10000);
			option.setPrintOutput(true);
			option.setBinPeaks(IPC.binningType.FIXED_BINNING);
			option.setCharge(1);

			results = isotopePatternCalc.execute(option);
			C_Peaks = results.getPeaks();
		}
		{
			Results results;
			IPC isotopePatternCalc = new IPC();
			Options option = new Options();

			option.parseChemFormulaAndAdd("S13");
			option.setFastCalc(10000);
			option.setPrintOutput(true);
			option.setBinPeaks(IPC.binningType.FIXED_BINNING);
			option.setCharge(1);

			results = isotopePatternCalc.execute(option);
			S_Peaks = results.getPeaks();
		}
		{
			Results results;
			IPC isotopePatternCalc = new IPC();
			Options option = new Options();

			option.parseChemFormulaAndAdd("H1964");
			option.setFastCalc(10000);
			option.setPrintOutput(true);
			option.setBinPeaks(IPC.binningType.FIXED_BINNING);
			option.setCharge(1);

			results = isotopePatternCalc.execute(option);
			H_Peaks = results.getPeaks();
		}
		{
			Results results;
			IPC isotopePatternCalc = new IPC();
			Options option = new Options();

			option.parseChemFormulaAndAdd("N340");
			option.setFastCalc(10000);
			option.setPrintOutput(true);
			option.setBinPeaks(IPC.binningType.FIXED_BINNING);
			option.setCharge(1);

			results = isotopePatternCalc.execute(option);
			N_Peaks = results.getPeaks();
		}
		{
			Results results;
			IPC isotopePatternCalc = new IPC();
			Options option = new Options();

			option.parseChemFormulaAndAdd("O353");
			option.setFastCalc(10000);
			option.setPrintOutput(true);
			option.setBinPeaks(IPC.binningType.FIXED_BINNING);
			option.setCharge(1);

			results = isotopePatternCalc.execute(option);
			O_Peaks = results.getPeaks();
		}

		Options option = new Options();

		option.parseChemFormulaAndAdd("C1228H1964N340O353S13");
		option.setFastCalc(10000);
		option.setPrintOutput(true);
		option.setBinPeaks(IPC.binningType.FIXED_BINNING);
		option.setCharge(1);
		Results results = new Results(option);

		System.out.println(System.currentTimeMillis());
		TreeSet<Peak> combine = new TreeSet<Peak>();
		for (Peak C : C_Peaks) {
			for (Peak S : S_Peaks) {
				for (Peak H : H_Peaks) {
					for (Peak N : N_Peaks) {
						for (Peak O : O_Peaks) {
							Peak peak = new Peak(C.getMass() + S.getMass()
									+ H.getMass() + N.getMass() + O.getMass()
									+ 5 * IPC.ELECTRON_MASS, C.getP()
									* S.getP() * H.getP() * N.getP() * O.getP());
							combine.add(peak);
						}
					}
				}
			}
		}
		IPC.BinController roundingBinningController = new IPC.MassDiffBinController(
				IPC.ROUNDING_MAX_DIFF);
		IPC.summarizePeaks(combine, roundingBinningController, true);
		results.setPeaks(combine);
		IPC.fillResults(results);
		System.out.println(results.toString());
		System.out.println(System.currentTimeMillis());
		FileWriter writer1 = new FileWriter(new File("E:/1.txt"));
		FileWriter writer2 = new FileWriter(new File("E:/2.txt"));
		BufferedWriter bw1 = new BufferedWriter(writer1);
		BufferedWriter bw2 = new BufferedWriter(writer2);

		double totalP = 0;
		for (Peak peak : results.getPeaks()) {
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
