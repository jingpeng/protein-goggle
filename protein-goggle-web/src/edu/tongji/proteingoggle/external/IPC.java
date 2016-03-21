/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tongji.proteingoggle.external;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import edu.tongji.proteingoggle.jfree.data.*;

import static java.lang.Math.*;

/**
 *
 * @author D3Y241
 */
public class IPC {

    public static enum binningType {

        NO_BINNING, FIXED_BINNING, RESOLUTION_BINNING
    };

    public static enum plotType {

        NO_PLOT, GAUSSIAN_PLOT, BAR_PLOT
    };
    final public static double TWO_SQRT_TWO_LN_TWO = 2.354820045030949382023138652919399275494771378771641077045051513000053177093969853616836276737541622134943157164024;
    final public static double PROTON_MASS = 1.00727646677;
    final public static double ELECTRON_MASS = 0.00054858;
    final public static double ROUNDING_MAX_DIFF = 0.00000001;
    final public static double DEFAULT_BINNING_MAX_DIFF = .05;
    final public static double DEFAULT_MIN_INT = 0.0001;
    final public static int DEFAULT_MAX_DIGITS = 10;
    final public static int ORBITRAP_RESOLUTION = 100000;
    final public static String ELEMENTFILE = "elemente.txt";
    //final private static Pattern isotopePattern = Pattern.compile("([\\d\\s]{3})\\s([\\w\\s]{3})\\s([\\d\\s]{3})\\s\\s([\\d.]+)(?:\\(\\d+\\))#?\\s+([\\d.]+)?(?:\\(\\d+\\))?\\s*([\\d.]+)?.*");
    private Map<String, TreeSet<Peak>> elements;
    private static Map<Character, Map<String, Integer>> aminoAcids = null;

    public Map<String, TreeSet<Peak>> getElements() {
        return elements;
    }

    public static Map<Character, Map<String, Integer>> getAminoAcids() {
        if (aminoAcids == null) {
            aminoAcids = makeAminoAcidsTable();
        }
        return aminoAcids;
    }

    public IPC() {
        //compounds = new LinkedList<Compound>();
        //peaks = new TreeSet<Isotope>();        
        elements = new HashMap<String, TreeSet<Peak>>();
        if (!initElements()) {
            System.err.printf("Error in init_elements\n");
            System.exit(0);
        }

        getAminoAcids();
    }

    public Results execute(Options options) {
        if (options.isBreakProcess()) {
            return null;
        }
        long startTime = System.currentTimeMillis();
        Results results = new Results(options);
        if (options.isBreakProcess()) {
            return null;
        }
        if (options.isCalcPeaks()) {
            calcPeaks(results);
        }
        if (options.isBreakProcess()) {
            return null;
        }
        fillResults(results);
        if (options.isBreakProcess()) {
            return null;
        }
        results.timeTook = (System.currentTimeMillis() - startTime) / 1000;
        if (options.isPrintOutput()) {
            System.out.println(results.toString());
            //System.out.println(options.getComponents());
        }
        if (options.isBreakProcess()) {
            return null;
        }
        if (options.getPlot() != plotType.NO_PLOT) {
            @SuppressWarnings("unused")
			String title = results.getOptions().getFormula();
            if (options.isBreakProcess()) {
                return null;
            }
            @SuppressWarnings("unused")
			boolean bars = (options.getPlot() == plotType.BAR_PLOT);
        }
        if (options.isBreakProcess()) {
            return null;
        }
        return results;
    }

    public static void usage() {
        System.out.printf("\nThis is IPC v %s\n\n", "MPC");
        System.out.printf("\nSynopsis:\n ipc -c <chemical formula> [OPTIONS]\n");
        System.out.printf(" ipc -p <file> [-c <chemical formula>] [OPTIONS]\n");
        System.out.printf(" ipc -a <amino acids> [-c <chemical formula>] [OPTIONS]\n");
        System.out.printf(" ipc [-h]\n\n");
        System.out.printf(" -c  <chemical formula> calculates the isotopic pattern of the \n");
        System.out.printf("     given chemical formula. Additive with -p\n");
        System.out.printf(" -p  <File> reads peptide sequence (one letter notation) from the \n");
        System.out.printf("     given file and calculates the isotopic pattern. Additive with -c\n");
        System.out.printf(" -a  <amino acids> calculate isotopic pattern from given amino acids\n");
        System.out.printf("     in one letter notation. H3O is added.\n");
        System.out.printf("\nOPTIONS: [-t] [-x] [-d <int>] [-z <int>] [-f <int>] [-g[s]] [-r <int>] [-b <double>] [-nb] [-na] [-fc]\n");
        System.out.printf(" -t  output is tabular and unformatted\n");
        System.out.printf(" -x  no calculation of the isotopic pattern.\n");
        System.out.printf(" -d  <int> digits significant\n");
        System.out.printf(" -z  <int> charges on ions \n");
        System.out.printf(" -f  fast calc, calculates only first <int> peaks\n");
        System.out.printf(" -g  show a graph of the calculated peaks using gaussian functions\n");
        System.out.printf(" -gs show a graph of the calculated peaks using sticks\n");
        System.out.printf(" -r  use <int> as the resolution (FWHM) for calculating gaussian peaks and determining binning. Default (Orbitrap) Resolution: %d\n", ORBITRAP_RESOLUTION);
        System.out.printf(" -b  bin peaks with mass difference less than <double>. If this option is not specified peaks are binned based on resolution. If no mass difference is given %f is used\n", DEFAULT_BINNING_MAX_DIFF);
        System.out.printf(" -nb don't perform any binning\n");
        System.out.printf(" -na don't add heights of peaks when performing binning. Only meaningful when not used with -nb\n");
        System.out.printf(" -fc fix cysteines. Adds Carbamidomethyl (H3C2NO) to each cysteine\n");
        System.out.printf(" -h  show this text\n\n");
    }

    public static class Options
            implements Serializable {

        private static final long serialVersionUID = -1558678287373925470l;
        private long fastCalc = 0;
        private double fixedBinningMaxDiff = DEFAULT_BINNING_MAX_DIFF;
        private long resolution = ORBITRAP_RESOLUTION;
        private boolean calcPeaks = true;
        private plotType plot = plotType.NO_PLOT;
        private boolean fixCysteines = false;
        private boolean tabOutput = false;
        private binningType binPeaks = binningType.RESOLUTION_BINNING;
        private boolean addPWhileBinning = true;
        private int useDigits = DEFAULT_MAX_DIGITS;
        private int charge = 1;
        private boolean printOutput = false;
        private double minIntensityToKeep = DEFAULT_MIN_INT;
        private Map<String, Integer> components;
        private boolean breakProcess = false;
        private Map<String, TreeSet<Peak>> overriddenElements = null;
        private Map<Character, Map<String, Integer>> overriddenAminoAcids = null;

        public Options() {
            components = new HashMap<String, Integer>();
        }

        private boolean addAminoAcid(Character aminoAcid) {
            if (overriddenAminoAcids != null && overriddenAminoAcids.containsKey(aminoAcid)) {
                addComponents(overriddenAminoAcids.get(aminoAcid));
                return true;
            } else if (getAminoAcids().containsKey(aminoAcid)) {
                addComponents(getAminoAcids().get(aminoAcid));
                return true;
            } else {
                throw new RuntimeException("Unknown symbol: " + aminoAcid);
                //return false;
            }
        }

        public boolean addPeptide(String aaFormula) {
            if (aaFormula.equals("")) {
                return true;
            }
            for (char AA : aaFormula.toCharArray()) {
                if (!addAminoAcid(AA)) {
                    return false;
                }
            }
            addComponent("O", 1);
            addComponent("H", 3);
            return true;
        }

        public boolean addPeptideFile(String fileName) {
            BufferedReader peptidesIn;
            String line;
            //int index = 0;
            try {
                peptidesIn = new BufferedReader(new FileReader(fileName));
                StringBuilder sb = new StringBuilder();
                while ((line = peptidesIn.readLine()) != null) {
                    if (!line.startsWith(">")) {
                        sb.append(line);
                    }
                }
                peptidesIn.close();
                return addPeptide(sb.toString());
            } catch (IOException ioe) {
                throw new RuntimeException("Can't open file: " + fileName);
            }
        }

        public static Map<String, Integer> parseChemFormula(String formula) throws IllegalArgumentException {
            Map<String, Integer> components = new HashMap<String, Integer>();
            String[] split = formula.replaceAll("([A-Z])", "\t$1").trim().split("\t");
            //System.out.println(Arrays.toString(split));
            for (String componentString : split) {
                int firstDigitIndex = -1;
                for (int i = 0; i < componentString.length(); i++) {
                    if (Character.isDigit(componentString.charAt(i))) {
                        firstDigitIndex = i;
                        break;
                    }
                }
                String symbol;
                int number = 1;
                if (firstDigitIndex == -1) {
                    symbol = componentString;
                } else {
                    symbol = componentString.substring(0, firstDigitIndex);
                    number = new Integer(componentString.substring(firstDigitIndex));
                }
                addComponent(symbol, number, components);
            }
            return components;
        }

        public void parseChemFormulaAndAdd(String formula) {
            addComponents(parseChemFormula(formula));
        }

        private static void addComponent(String symbol, int number, Map<String, Integer> components) {
            symbol = symbol.trim();
            if (symbol.equals("")) {
                return;
            }
            if (components.containsKey(symbol)) {
                components.put(symbol, components.get(symbol) + number);
            } else {
                components.put(symbol, number);
            }
        }

        public void addComponent(String symbol, int number) {
            addComponent(symbol, number, components);
        }

        public static void addComponents(Map<String, Integer> newComponents, Map<String, Integer> components) {
            for (String symbol : newComponents.keySet()) {
                addComponent(symbol, newComponents.get(symbol), components);
            }
        }

        public void addComponents(Map<String, Integer> newComponents) {
            if (newComponents == null) {
                return;
            }
            for (String symbol : newComponents.keySet()) {
                addComponent(symbol, newComponents.get(symbol));
            }
        }

        public static Options parseArgs(String[] args) {
            Options options = new Options();
            for (int d = 0; d < args.length; d++) {
                if (args[d].equals("-f")) {
                    d++;
                    if (d >= args.length) {
                        System.err.printf("Missing argument for -f\n");
                        System.exit(1);
                    }
                    options.setFastCalc((long) new Long(args[d]));
                } else if (args[d].equals("-z")) {
                    d++;
                    if (d >= args.length) {
                        System.err.printf("Missing argument for -z\n");
                        System.exit(1);
                    }
                    options.setCharge((int) new Integer(args[d]));
                } else if (args[d].equals("-d")) {
                    d++;
                    if (d >= args.length) {
                        System.err.printf("Missing argument for -d\n");
                        System.exit(1);
                    }
                    options.setUseDigits((int) new Integer(args[d]));
                } else if (args[d].equals("-c")) {
                    d++;
                    if (d >= args.length) {
                        System.err.printf("Missing argument for -c\n");
                        System.exit(1);
                    }
                    options.parseChemFormulaAndAdd(args[d]);
                } else if (args[d].equals("-g")) {
                    options.setPlot(plotType.GAUSSIAN_PLOT);
                } else if (args[d].equals("-gs")) {
                    options.setPlot(plotType.BAR_PLOT);
                } else if (args[d].equals("-p")) {
                    d++;
                    if (d >= args.length) {
                        System.err.printf("Missing argument for -p\n");
                        System.exit(1);
                    }
                    options.addPeptideFile(args[d]);
                } else if (args[d].equals("-a")) {
                    d++;
                    if (d >= args.length) {
                        System.err.printf("Missing argument for -a\n");
                        System.exit(1);
                    }
                    options.addPeptide(args[d]);
                } else if (args[d].equals("-r")) {
                    d++;
                    if (d >= args.length) {
                        System.err.printf("Missing argument for -r\n");
                        System.exit(1);
                    }
                    options.setResolution((long) new Long(args[d]));
                } else if (args[d].equals("-b")) {
                    options.setBinPeaks(binningType.FIXED_BINNING);
                    d++;
                    if (d < args.length) {
                        try {
                            options.setFixedBinningMaxDiff((double) new Double(args[d]));
                        } catch (NumberFormatException ne) {
                            d--;
                        }
                    }
                } else if (args[d].equals("-t")) {
                    options.setTabOutput(true);
                } else if (args[d].equals("-h")) {
                    usage();
                    System.exit(1);
                } else if (args[d].equals("-x")) {
                    options.setCalcPeaks(false);
                } else if (args[d].equals("-nb")) {
                    options.setBinPeaks(binningType.NO_BINNING);
                } else if (args[d].equals("-na")) {
                    options.setAddPWhileBinning(false);
                } else if (args[d].equals("-fc")) {
                    options.setFixCysteines(true);
                    //System.err.println("Fixing Cs, " + fixCysteines);
                } else {
                    System.err.printf("Unknown flag: %s\n", args[d]);
                    usage();
                    System.exit(1);
                }
            }
            return options;
        }

        public String getFormula() {
            return calcSum(getComponents());
        }

        public String getFormula(boolean spaces) {
            return calcSum(getComponents(), spaces);
        }

        public long getFastCalc() {
            return fastCalc;
        }

        public void setFastCalc(long fastCalc) {
            this.fastCalc = fastCalc;
        }

        public double getFixedBinningMaxDiff() {
            return fixedBinningMaxDiff;
        }

        public void setFixedBinningMaxDiff(double fixedBinningMaxDiff) {
            this.fixedBinningMaxDiff = fixedBinningMaxDiff;
        }

        public long getResolution() {
            return resolution;
        }

        public void setResolution(long resolution) {
            this.resolution = resolution;
        }

        public boolean isCalcPeaks() {
            return calcPeaks;
        }

        public void setCalcPeaks(boolean calcPeaks) {
            this.calcPeaks = calcPeaks;
        }

        public plotType getPlot() {
            return plot;
        }

        public void setPlot(plotType plot) {
            this.plot = plot;
        }

        public boolean isFixCysteines() {
            return fixCysteines;
        }

        public void setFixCysteines(boolean fixCysteines) {
            this.fixCysteines = fixCysteines;
        }

        public boolean isTabOutput() {
            return tabOutput;
        }

        public void setTabOutput(boolean tabOutput) {
            this.tabOutput = tabOutput;
        }

        public binningType getBinPeaks() {
            return binPeaks;
        }

        public void setBinPeaks(binningType binPeaks) {
            this.binPeaks = binPeaks;
        }

        public boolean isAddPWhileBinning() {
            return addPWhileBinning;
        }

        public void setAddPWhileBinning(boolean addPWhileBinning) {
            this.addPWhileBinning = addPWhileBinning;
        }

        public int getUseDigits() {
            return useDigits;
        }

        public void setUseDigits(int useDigits) {
            this.useDigits = useDigits;
        }

        public int getCharge() {
            return charge;
        }

        public void setCharge(int charge) {
            this.charge = charge;
        }

        public void setOverriddenElements(Map<String, TreeSet<Peak>> overriddenElements) {
            this.overriddenElements = overriddenElements;
        }

        public Map<String, TreeSet<Peak>> getOverriddenElements() {
            return overriddenElements;
        }

        public Map<Character, Map<String, Integer>> getOverriddenAminoAcids() {
            return overriddenAminoAcids;
        }

        public void setOverriddenAminoAcids(Map<Character, Map<String, Integer>> overriddenAminoAcids) {
            this.overriddenAminoAcids = overriddenAminoAcids;
        }



        public boolean isPrintOutput() {
            return printOutput;
        }

        public void setPrintOutput(boolean printOutput) {
            this.printOutput = printOutput;
        }

        public int getNumAtoms() {
            int atoms = 0;
            for (String symbol : getComponents().keySet()) {
                atoms += getComponents().get(symbol);
            }
            return atoms;
        }

        public Map<String, Integer> getComponents() {
            return components;
        }

        public double getMinIntensityToKeep() {
            return minIntensityToKeep;
        }

        public void setMinIntensityToKeep(double minIntensityToKeep) {
            this.minIntensityToKeep = minIntensityToKeep;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Options other = (Options) obj;
            if (this.fixedBinningMaxDiff != other.fixedBinningMaxDiff) {
                return false;
            }
            if (this.resolution != other.resolution) {
                return false;
            }
            if (this.calcPeaks != other.calcPeaks) {
                return false;
            }
            if (this.fixCysteines != other.fixCysteines) {
                return false;
            }
            if (this.addPWhileBinning != other.addPWhileBinning) {
                return false;
            }
            if (this.useDigits != other.useDigits) {
                return false;
            }
            if (this.charge != other.charge) {
                return false;
            }
            if (this.minIntensityToKeep != other.minIntensityToKeep) {
                return false;
            }
            if (this.components != other.components && (this.components == null || !this.components.equals(other.components))) {
                return false;
            }
            if (this.overriddenElements != other.overriddenElements && (this.overriddenElements == null || !this.overriddenElements.equals(other.overriddenElements))) {
                return false;
            }
            if (this.overriddenAminoAcids != other.overriddenAminoAcids && (this.overriddenAminoAcids == null || !this.overriddenAminoAcids.equals(other.overriddenAminoAcids))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 23 * hash + (this.overriddenAminoAcids != null ? this.overriddenAminoAcids.hashCode() : 0);
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.fixedBinningMaxDiff) ^ (Double.doubleToLongBits(this.fixedBinningMaxDiff) >>> 32));
            hash = 23 * hash + (int) (this.resolution ^ (this.resolution >>> 32));
            hash = 23 * hash + (this.calcPeaks ? 1 : 0);
            hash = 23 * hash + (this.fixCysteines ? 1 : 0);
            hash = 23 * hash + (this.addPWhileBinning ? 1 : 0);
            hash = 23 * hash + this.useDigits;
            hash = 23 * hash + this.charge;
            hash = 23 * hash + (int) (Double.doubleToLongBits(this.minIntensityToKeep) ^ (Double.doubleToLongBits(this.minIntensityToKeep) >>> 32));
            hash = 23 * hash + (this.components != null ? this.components.hashCode() : 0);
            hash = 23 * hash + (this.overriddenElements != null ? this.overriddenElements.hashCode() : 0);
            
            return hash;
        }

        public boolean isBreakProcess() {
            return breakProcess;
        }

        public void setBreakProcess(boolean breakProcess) {
            this.breakProcess = breakProcess;
        }
    }

    public static class Results
            implements Serializable {

        private static final long serialVersionUID = -463195606298108743L;
        private Options options;
        private double maxP;
        private double sumP;
        private double[] minDiff;
        private int calculatedPeaks;
        private int timesBinned;
        private int changeInPeaks;
        private int numPeaks;
        private TreeSet<Peak> peaks;
        private long timeTook;

        public Results(Options options) {
            this.options = options;
            peaks = new TreeSet<Peak>();
        }
        //resets the options field IF AND ONLY IF they are equal(), meaning they will produce the same reuslts

        public void resetOptions(Options options) throws IllegalArgumentException {
            if (this.options.equals(options)) {
                this.options = options;
            } else {
                throw new IllegalArgumentException();
            }
        }

        public double getMaxP() {
            return maxP;
        }

        public void setMaxP(double maxP) {
            this.maxP = maxP;
        }

        public double getSumP() {
            return sumP;
        }

        public void setSumP(double sumP) {
            this.sumP = sumP;
        }

        public double[] getMinDiff() {
            return minDiff;
        }

        public void setMinDiff(double[] minDiff) {
            this.minDiff = minDiff;
        }

        public int getCalculatedPeaks() {
            return calculatedPeaks;
        }

        public void setCalculatedPeaks(int calculatedPeaks) {
            this.calculatedPeaks = calculatedPeaks;
        }

        public int getTimesBinned() {
            return timesBinned;
        }

        public void setTimesBinned(int timesBinned) {
            this.timesBinned = timesBinned;
        }

        public int getChangeInPeaks() {
            return changeInPeaks;
        }

        public void setChangeInPeaks(int changeInPeaks) {
            this.changeInPeaks = changeInPeaks;
        }

        public int getNumPeaks() {
            return numPeaks;
        }

        public void setNumPeaks(int numPeaks) {
            this.numPeaks = numPeaks;
        }

        public TreeSet<Peak> getPeaks() {
            return peaks;
        }

        public void setPeaks(TreeSet<Peak> peaks) {
            this.peaks = peaks;
        }

        public Options getOptions() {
            return options;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Chemical formula: ");
            sb.append(options.getFormula());
            sb.append("\n\n");
            if (getOptions().isTabOutput()) {
                sb.append("M/Z\tPortion of Total\tRelative Intensity\n");
            }
            for (Peak peak : getPeaks()) {
                if (getOptions().isTabOutput()) {
                    sb.append(String.format("%f\t%f\t%f\n", peak.getMass(), peak.getP(), peak.getRelInt()));
                } else {
                    sb.append(String.format("M/z= %f, P= %f%%, Rel. Int.= %f%%\n", peak.getMass(), peak.getP() * 100, peak.getRelInt() * 100));
                }
            }
            sb.append(String.format("%nNumber of peaks calculated:              %d%n", getCalculatedPeaks()));
            sb.append(String.format("Number of peaks after binning:           %d%n", getNumPeaks()));
            sb.append(String.format("Number of peaks above minimum intensity: %d%n", getPeaks().size()));
            sb.append(String.format("%nCovered Intensity: %2.3f%%%n", roundToDigits(getSumP() * 100, 2)));
            if (getTimesBinned() > 1) {
                sb.append(String.format("%nBinned Peaks %d times.%n", getTimesBinned()));
            }
            sb.append(String.format("%nMinimum Mr difference: %f between %f and %f", getMinDiff()[0], getMinDiff()[1], getMinDiff()[2]));
            sb.append(String.format("%nComputing time: %d seconds.%n", timeTook));
            return sb.toString();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + (this.peaks != null ? this.peaks.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Results other = (Results) obj;
            if (this.peaks != other.peaks && (this.peaks == null || !this.peaks.equals(other.peaks))) {
                return false;
            }
            return true;
        }
    }

    public static void fillResults(Results results) {
        results.setMaxP(0.0);
        results.setSumP(sumP(results.getPeaks()));
        results.setMinDiff(new double[]{Double.POSITIVE_INFINITY, 0.0, 0.0});
        results.setCalculatedPeaks(results.getPeaks().size());
        results.setTimesBinned(0);
        if (results.getOptions().getBinPeaks() != binningType.NO_BINNING) {
            BinController binningController;
            if (results.getOptions().getBinPeaks() == binningType.RESOLUTION_BINNING) {
                binningController = new ResolvingPowerBinController(results.getOptions().getResolution());
            } else {
                binningController = new MassDiffBinController(results.getOptions().getFixedBinningMaxDiff());
            }
            do {
                int numberOfPeaks = results.getPeaks().size();
                summarizePeaks(
                        results.getPeaks(), binningController, results.getOptions().isAddPWhileBinning());
                results.setTimesBinned(results.getTimesBinned() + 1);
                results.setChangeInPeaks(numberOfPeaks - results.getPeaks().size());
            } while (results.getChangeInPeaks() != 0);
            results.setTimesBinned(results.getTimesBinned() - 1);
        } //turn masses to M/Z
        for (Peak currentPeak : results.getPeaks()) {
            currentPeak.setMass(toMZ(currentPeak.getMass(), results.getOptions().getCharge()));
        } //find min peak mass difference and maxP
        for (Peak currentPeak : results.getPeaks()) {
            Peak higher;
            if ((higher = results.getPeaks().higher(currentPeak)) != null) {
                double diff = higher.getMass() - currentPeak.getMass();
                if (diff < results.getMinDiff()[0]) {
                    results.getMinDiff()[0] = diff;
                    results.getMinDiff()[1] = currentPeak.getMass();
                    results.getMinDiff()[2] = higher.getMass();
                }
            }
            if (currentPeak.getP() > results.getMaxP()) {
                results.setMaxP(currentPeak.getP());
            }
        }
        results.setNumPeaks(results.getPeaks().size());
        //set relitive intensities and limit digits
        for (Iterator<Peak> it = results.getPeaks().iterator(); it.hasNext();) {
            Peak currentPeak = it.next();
            double relInt = currentPeak.getP() / results.getMaxP();
            if (relInt > results.getOptions().getMinIntensityToKeep()) {
                //System.out.println(currentIsotope.getP());
                currentPeak.setMass(roundToDigits(currentPeak.getMass(), results.getOptions().getUseDigits()));
                currentPeak.setP(roundToDigits(currentPeak.getP(), results.getOptions().getUseDigits()));
                currentPeak.setRelInt(relInt);
            } else {
                it.remove();
            }
        }
    }

    private void cutPeaks(TreeSet<Peak> peaks, long newSize) {
        //TreeSet<Peak> pOrderedPeaks = new TreeSet<Peak>();
        TreeSet<Peak> pOrderedPeaks = new TreeSet<Peak>(new PeakPComparator());
        pOrderedPeaks.addAll(peaks);
        long index = 1;
        //while(peaks.size()>newSize){
        //    peaks.remove(peaks.last());
        //}
        for (Peak isotope : pOrderedPeaks) {
            if (index > newSize) {
                //System.err.println("Removing peak");
                //it.remove();
                peaks.remove(isotope);
            }
            index++;
        }
        peaks.retainAll(pOrderedPeaks);
    }

    static Peak strongerIsotope(Peak peak1, Peak peak2) {
        Peak stronger = peak1;
        if (peak2.getP() > stronger.getP()) {
            stronger = peak2;
        }
        return stronger;
    }

    public interface BinController {

        boolean bin(Peak a, Peak b);
    }

    public static class MassDiffBinController
            implements BinController {

        double maxDiff;

        public MassDiffBinController(double maxDiff) {
            this.maxDiff = maxDiff;
        }

        public boolean bin(Peak peak1, Peak peak2) {
            return bin(peak1, peak2, maxDiff);
        }

        static public boolean bin(Peak peak1, Peak peak2, double maxDiff) {
            return peak1 != null && peak2 != null && abs(peak2.getMass() - peak1.getMass()) < maxDiff;
        }
    }

    private static class ResolvingPowerBinController
            implements BinController {

        long resolvingPower;

        public ResolvingPowerBinController(long resolvingPower) {
            this.resolvingPower = resolvingPower;
        }

        public boolean bin(Peak lower, Peak higher) {
            if (higher == null) {
                return false;
            }
            //double leftWidthOfHigher = higher.getMass() - halfWidth(higher.getMass());
            //double rightWidthOfLower = lower.getMass() + halfWidth(lower.getMass());
            //return ((leftWidthOfHigher - rightWidthOfLower) < ROUNDING_MAX_DIFF);
            halfWidth(PI);
            double maxDiff = halfWidth(lower.getMass()) + halfWidth(higher.getMass());
            return MassDiffBinController.bin(lower, higher, maxDiff);
        }

        double halfWidth(double mass) {
            return mass / resolvingPower / 2;
        }
    }

    public static void summarizePeaks(TreeSet<Peak> peaks, BinController binController, boolean combineP) {
        for (Iterator<Peak> it = peaks.iterator(); it.hasNext();) {
            Peak lighterPeak = it.next();
            Peak heavierPeak = peaks.higher(lighterPeak);
            if (binController.bin(lighterPeak, heavierPeak)) {
                //System.out.printf("combining %s and %s%n", lighterPeak, heavierPeak);
                it.remove();
                Peak newPeak = new Peak();
                double massDiff = heavierPeak.getMass() - lighterPeak.getMass();
                double sumP = lighterPeak.getP() + heavierPeak.getP();

                double newMass = lighterPeak.getMass() + massDiff * (heavierPeak.getP() / sumP);
                //System.out.println(lighterPeak.getMass() + " + " + massDiff + "*("+heavierPeak.getP()+" / "+sumP+")");
                newPeak.setMass(newMass);
                if (combineP) {
                    newPeak.setP(sumP);
                } else {
                	Peak stronger = strongerIsotope(lighterPeak, heavierPeak);
                    newPeak.setP(stronger.getP());
                    //System.out.printf("peaks merged: (%s)+(%s)->(%s)%n", peak1, peak2, newPeak);
                } //System.out.println(roundToDigits(lighterPeak.getMass(), 2) + " + " + heavierPeak.getMass() + " ==> " + newMass);
                if (newPeak.getP() > (1.0 + ROUNDING_MAX_DIFF)) {
                    throw new RuntimeException("In summarizePeaks newP is > 1.0");
                }
                heavierPeak.set(newPeak);
            }
        }
    }

    public TreeSet<Peak> findElement(Options ipcOptions, String elementSymbol) throws UnknownKeyException {
        if (ipcOptions != null && ipcOptions.getOverriddenElements() != null && ipcOptions.getOverriddenElements().containsKey(elementSymbol)) {
            return ipcOptions.getOverriddenElements().get(elementSymbol);
        }
        if (elements.containsKey(elementSymbol)) {
            TreeSet<Peak> element = elements.get(elementSymbol);
            //printElement(elementSymbol, element);
            return element;
        }
        throw new UnknownKeyException("Element '" + elementSymbol + "' not recognized.");
    }

    public static void printElement(String symbol, TreeSet<Peak> element) {
        StringBuilder sb = new StringBuilder();
        sb.append(symbol);
        sb.append(": {");
        boolean first = true;
        for (Peak p : element) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(p.getMass());
            sb.append(": ");
            sb.append(p.getP());
        }
        sb.append("}");
        System.out.println(sb.toString());
    }

    public void calcPeaks(Results results) {
        BinController roundingBinningController = new MassDiffBinController(ROUNDING_MAX_DIFF);
        Map<String, Integer> elementalComponents = results.getOptions().getComponents();
        results.getPeaks().add(new Peak(0, 1));
        //System.out.println("peaks size: " + results.getPeaks().size());
        if (results.options.isBreakProcess()) {
            return;
        }
        for (String elementSymbol : elementalComponents.keySet()) {
            //System.out.println("\t" + symbol + components.get(symbol) + "\tpeaks size: " + results.getPeaks().size());
            TreeSet<Peak> element = findElement(results.getOptions(), elementSymbol);
            //printElement(element);
            for (int number = 0; number
                    < elementalComponents.get(elementSymbol); number++) {
                //System.out.println("\t\t#" + number + "\tpeaks size: " + results.getPeaks().size());
                TreeSet<Peak> newPeaks = new TreeSet<Peak>();
                for (Peak elementIsotope : element) {
                    for (Peak peak : results.getPeaks()) {
                        if (results.options.isBreakProcess()) {
                            return;
                        }
                        double mass = peak.getMass() + elementIsotope.getMass();
                        double p = peak.getP() * elementIsotope.getP();
                        //if (p > 1) {
                        //System.out.println("\t\t\t" + "\tnewPeak mass: " + mass);
                        //System.out.println("\t\t\t" + "\tnewPeak p: " + p + " = " + peak.getP() + " * " + compoundIsotope.getP());
                        //}
                        addPeak(
                                newPeaks, new Peak(mass, p));
                        //System.out.println("\t\t\tsump: " + sumP(newPeaks));
                    }
                }
                //System.out.println("\t\t\t" + "\tnewPeaks size: " + newPeaks.size());
                results.setPeaks(newPeaks);
                //System.out.println("\t\t\t" + "\tpeaks size: " + results.getPeaks().size());
                summarizePeaks(
                        results.getPeaks(), roundingBinningController, true);
                if (results.getOptions().getFastCalc() > 0 && results.getOptions().getFastCalc() < results.getPeaks().size()) {
                    cutPeaks(results.getPeaks(), results.getOptions().getFastCalc());
                } //System.out.println("\t\t\t" + "\tpeaks size: " + results.getPeaks().size());
            }
        }
    }

    static void addPeak(TreeSet<Peak> peakSet, Peak peakToAdd) {
        if (peakToAdd.getP() <= 0) {
            return;
        }
        if (peakSet.contains(peakToAdd)) {
            Peak samePeak = peakSet.ceiling(peakToAdd);
            if (peakSet.floor(peakToAdd) != samePeak) {
                throw new RuntimeException("Can't find same peak!");
            }
            samePeak.addP(peakToAdd.getP());
        } else {
            //if(peakToAdd.)
            peakSet.add(peakToAdd);
        }
    }

    BufferedReader openFile(String filename) {
    	URL url  = this.getClass().getResource("elemente.txt");
    	System.out.println(url.toString());
        File file = new File(url.getFile());
        
        
        boolean exists = (file).exists();
        if (!exists)
        {
        	System.out.println(exists);
            System.out.println("file doesn't Exist!");
            System.exit(0);
        }
            
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(url.getFile()));
        } catch (IOException ioe) {
            throw new RuntimeException("Can't read elemente file.");
        }
        return in;
    }

    private static String[] splitElementeLine(String line) {
        line = String.format("%1$-74s", line);
        return new String[]{
                    line.substring(0, 4).trim(),
                    line.substring(4, 8).trim(),
                    line.substring(8, 13).trim(),
                    line.substring(13, 32).split("\\(")[0].trim(),
                    line.substring(32, 46).split("\\(")[0].trim()
                };
    }

    public static Map<Character, Map<String, Integer>> makeAminoAcidsTable() {
        Map<Character, Map<String, Integer>> newAminoAcidTable = new HashMap<Character, Map<String, Integer>>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        newAminoAcidTable.put('A', map);
        map.put("C", 3);
        map.put("H", 5);
        map.put("N", 1);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('R', map);
        map.put("C", 6);
        map.put("H", 12);
        map.put("N", 4);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('N', map);
        map.put("C", 4);
        map.put("H", 6);
        map.put("N", 2);
        map.put("O", 2);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('D', map);
        map.put("C", 4);
        map.put("H", 5);
        map.put("N", 1);
        map.put("O", 3);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('c', map);
        map.put("C", 5);
        map.put("H", 8);
        map.put("N", 2);
        map.put("O", 2);
        map.put("S", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('C', map);
        map.put("C", 3);
        map.put("H", 5);
        map.put("N", 1);
        map.put("O", 1);
        map.put("S", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('E', map);
        map.put("C", 5);
        map.put("H", 7);
        map.put("N", 1);
        map.put("O", 3);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('Q', map);
        map.put("C", 5);
        map.put("H", 8);
        map.put("N", 2);
        map.put("O", 2);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('G', map);
        map.put("C", 2);
        map.put("H", 3);
        map.put("N", 1);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('H', map);
        map.put("C", 6);
        map.put("H", 7);
        map.put("N", 3);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('I', map);
        newAminoAcidTable.put('L', map);
        map.put("C", 6);
        map.put("H", 11);
        map.put("N", 1);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('K', map);
        map.put("C", 6);
        map.put("H", 12);
        map.put("N", 2);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('M', map);
        map.put("C", 5);
        map.put("H", 9);
        map.put("N", 1);
        map.put("O", 1);
        map.put("S", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('F', map);
        map.put("C", 9);
        map.put("H", 9);
        map.put("N", 1);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('P', map);
        map.put("C", 5);
        map.put("H", 7);
        map.put("N", 1);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('S', map);
        map.put("C", 3);
        map.put("H", 5);
        map.put("N", 1);
        map.put("O", 2);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('T', map);
        map.put("C", 4);
        map.put("H", 7);
        map.put("N", 1);
        map.put("O", 2);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('W', map);
        map.put("C", 11);
        map.put("H", 10);
        map.put("N", 2);
        map.put("O", 1);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('Y', map);
        map.put("C", 9);
        map.put("H", 9);
        map.put("N", 1);
        map.put("O", 2);
        map = new HashMap<String, Integer>();
        newAminoAcidTable.put('V', map);
        map.put("C", 5);
        map.put("H", 9);
        map.put("N", 1);
        map.put("O", 1);
        return newAminoAcidTable;
    }

    boolean initElements() {
        BufferedReader data;
        //int count;
        //Peak isotope;
        if ((data = openFile(ELEMENTFILE)) == null) {
            return false;
        }
        try {
            data.readLine();  // read table headings
            data.readLine();
            data.readLine();  // get first barred line
            String line;
            while ((line = data.readLine()) != null) {
                String[] split = splitElementeLine(line);
                String symbol = split[1];
                TreeSet<Peak> isotopes = new TreeSet<Peak>();
                if (!symbol.equals("")) {
                    elements.put(symbol, isotopes);
                }
                Peak isotope = parseIsotope(split);
                // don't add isotopes with 0 probability since they don't affect results
                // but increase computing time.
                if (isotope != null && isotope.getP() > 0.0) {
                    isotopes.add(isotope);
                }
                while ((line = data.readLine()) != null && !line.startsWith("_")) {  //if the first char isn't a bar then this is an isotope line
                    isotope = parseIsotope(splitElementeLine(line));
                    if (isotope != null && isotope.getP() > 0.0) {
                        isotopes.add(isotope);
                    }
                }
                //data.readLine();  // otherwise read the rest of the bar line
            }
            data.close();
        } catch (IOException ioe) {
            return false;
        }              
        return true;
    }

    private static Peak parseIsotope(String[] line) {
        //System.out.println(Arrays.toString(Arrays.copyOfRange(line, 3, 5)));
        if (line[4].trim().equals("")) {
            return null;
        }
        return new Peak(new Double(line[3]), new Double(line[4]) / 100.0);
    }

    static void print(char[] chars) {
        System.err.print("[");
        for (int i = 0; i
                < chars.length - 1; i++) {
            System.err.print(chars[i] + " ");
        }
        System.err.println(chars[chars.length - 1] + "]");
    }

    static String convert(char[] chars) {
        //print(chars);
        StringBuilder sb = new StringBuilder();
        for (int c : chars) {
            if (c == '\0') {
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }

    public static double toMZ(double mass, int charge) {
        if (charge == 1) {
            return mass - ELECTRON_MASS;
        }
        return ((mass + ((charge - 1) * PROTON_MASS) - ELECTRON_MASS) / charge);
    }
    public static double toMr(double mz, int charge) {
        if (charge == 1) {
            return mz + ELECTRON_MASS;
        }
        return (mz*charge) + ELECTRON_MASS - ((charge - 1) * PROTON_MASS);
    }

    public static double roundToDigits(double d, int digits) {
        if (digits > 10) {
            return d;
        }
        double mult = pow(10, digits);
        return rint(d * mult) / mult;
    }

    public static double sumP(Iterable<Peak> isotopes) {
        double sum = 0;
        for (Peak i : isotopes) {
            if (i != null) {
                sum += i.getP();
            }
        }
        return sum;
    }

    public static String calcSum(Map<String, Integer> components) {
        return calcSum(components, true);
    }

    public static String calcSum(Map<String, Integer> components, boolean spaces) {
        StringBuilder formula = new StringBuilder();
        List<String> keys = new LinkedList<String>(components.keySet());
        Collections.sort(keys);
        for (String symbol : keys) {
            formula.append(String.format("%s%d", symbol, components.get(symbol)));
            if (spaces) {
                formula.append(' ');
            }
        }
        return formula.toString().trim();
    }
}
