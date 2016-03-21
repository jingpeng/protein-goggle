package edu.tongji.proteingoggle.gpu;

import static jcuda.driver.JCudaDriver.cuCtxCreate;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuInit;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemFree;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoHAsync;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoDAsync;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoad;
import static jcuda.driver.JCudaDriver.cuStreamCreate;
import static jcuda.runtime.JCuda.cudaHostRegister;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.CUstream;
import jcuda.driver.JCudaDriver;
import jcuda.runtime.JCuda;
import edu.tongji.proteingoggle.bll.Analysis;
import edu.tongji.proteingoggle.bll.AnalysisParameter;
import edu.tongji.proteingoggle.dal.ElementPeaksDAL.PeaksData;
import edu.tongji.proteingoggle.external.IPC;
import edu.tongji.proteingoggle.external.Peak;
import edu.tongji.proteingoggle.external.IPC.Options;
import edu.tongji.proteingoggle.external.IPC.Results;

public class GPUSolution {

	private static int GPU_ID = 0;
	private static int STREAM_NUMBER = 2;
	private static int MASS_SPECTRAL_NUMBER;
	private static int MASS_SPECTRAL_CHUNKSIZE;
	private static int ELEMENT_NUMBER = 5;

	public List<IPC.Results> excuteGPUAnalysisCalc(Analysis analysis,
			List<AnalysisParameter> listParameters,
			Map<String, PeaksData> elementPeaks) {

		MASS_SPECTRAL_NUMBER = listParameters.size();
		MASS_SPECTRAL_CHUNKSIZE = listParameters.size() / 4;
		int[] instances = new int[ELEMENT_NUMBER * MASS_SPECTRAL_NUMBER];

		int CMax = 0;
		int SMax = 0;
		int HMax = 0;
		int NMax = 0;
		int OMax = 0;

		for (int i = 0; i != listParameters.size(); i++) {
			AnalysisParameter parameter = listParameters.get(i);
			String formula = parameter.Formula;
			Map<String, Integer> components = IPC.Options
					.parseChemFormula(formula);

			int CNumber = 0, HNumber = 0, NNumber = 0, ONumber = 0, SNumber = 0, PNumber = 0, FeNumber = 0;
			if (components.containsKey("C")) {
				CNumber = components.get("C");
				if (CNumber > CMax) {
					CMax = CNumber;
				}
			}
			if (components.containsKey("H")) {
				HNumber = components.get("H");
				if (HNumber > HMax) {
					HMax = HNumber;
				}
			}
			if (components.containsKey("N")) {
				NNumber = components.get("N");
				if (NNumber > NMax) {
					NMax = NNumber;
				}
			}
			if (components.containsKey("O")) {
				ONumber = components.get("O");
				if (ONumber > OMax) {
					OMax = ONumber;
				}
			}
			if (components.containsKey("S")) {
				SNumber = components.get("S");
				if (SNumber > SMax) {
					SMax = SNumber;
				}
			}
			if (components.containsKey("P")) {
				PNumber = components.get("P");
			}
			if (components.containsKey("Fe")) {
				FeNumber = components.get("Fe");
			}

			// order C,S,H,N,O, ===== Fe,P

			instances[i * ELEMENT_NUMBER] = CNumber;
			instances[i * ELEMENT_NUMBER + 1] = SNumber;
			instances[i * ELEMENT_NUMBER + 2] = HNumber;
			instances[i * ELEMENT_NUMBER + 3] = NNumber;
			instances[i * ELEMENT_NUMBER + 4] = ONumber;
		}

		PeaksData4GPU peaksData4GPU = processElementPeaks(elementPeaks, CMax,
				SMax, HMax, NMax, OMax);

		List<Results> resultsList = null;
		try {
			long start = System.currentTimeMillis();
			resultsList = GPUCalculation(peaksData4GPU, instances,
					listParameters);
			long end = System.currentTimeMillis();
			System.out.println("GPU time cost is: " + (end - start) + "ms");
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultsList;
	}

	private static List<Results> GPUCalculation(PeaksData4GPU peaksData4GPU,
			int[] instances, List<AnalysisParameter> listParameters)
			throws Exception {

		List<IPC.Results> resultsList = new ArrayList<IPC.Results>();
		System.out.println("=============JCudaIPC begin=============");
		JCudaDriver.setExceptionsEnabled(true);

		String ptxFileName = preparePtxFile("JCudaIPCShared.cu");

		cuInit(0);
		CUdevice device = new CUdevice();
		cuDeviceGet(device, GPU_ID);
		CUcontext context = new CUcontext();
		cuCtxCreate(context, 0, device);

		CUmodule module = new CUmodule();
		cuModuleLoad(module, ptxFileName);

		CUfunction function = new CUfunction();
		cuModuleGetFunction(function, module, "ipc");

		double[] CMass = peaksData4GPU.getCMass();
		double[] SMass = peaksData4GPU.getSMass();
		double[] HMass = peaksData4GPU.getHMass();
		double[] NMass = peaksData4GPU.getNMass();
		double[] OMass = peaksData4GPU.getOMass();

		// not use now !!
		double[] FeMass = peaksData4GPU.getFeMass();
		double[] PMass = peaksData4GPU.getPMass();

		double[] CP = peaksData4GPU.getCP();
		double[] SP = peaksData4GPU.getSP();
		double[] HP = peaksData4GPU.getHP();
		double[] NP = peaksData4GPU.getNP();
		double[] OP = peaksData4GPU.getOP();

		// not use now !!
		double[] FeP = peaksData4GPU.getFeP();
		double[] PP = peaksData4GPU.getPP();

		IntBuffer indexCombination = ByteBuffer
				.allocateDirect(
						ELEMENT_NUMBER * MASS_SPECTRAL_NUMBER * Sizeof.INT)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		for (int i = 0; i != ELEMENT_NUMBER * MASS_SPECTRAL_NUMBER; i++) {
			indexCombination.put(i, instances[i]);
		}

		List<Integer> totalCombinations = peaksData4GPU.getTotalCombinations();
		int bufferSize = 1;

		int[] arrayCombination = new int[totalCombinations.size()];
		for (int i = 0; i != totalCombinations.size(); i++) {
			bufferSize = bufferSize * totalCombinations.get(i);
			arrayCombination[i] = totalCombinations.get(i);
		}

		DoubleBuffer hostMassOutput = ByteBuffer
				.allocateDirect(
						MASS_SPECTRAL_NUMBER * bufferSize * Sizeof.DOUBLE)
				.order(ByteOrder.nativeOrder()).asDoubleBuffer();
		DoubleBuffer hostPOutput = ByteBuffer
				.allocateDirect(
						MASS_SPECTRAL_NUMBER * bufferSize * Sizeof.DOUBLE)
				.order(ByteOrder.nativeOrder()).asDoubleBuffer();

		CUdeviceptr deviceCMass = new CUdeviceptr();
		cuMemAlloc(deviceCMass, CMass.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceCMass, Pointer.to(CMass), CMass.length
				* Sizeof.DOUBLE);
		CUdeviceptr deviceSMass = new CUdeviceptr();
		cuMemAlloc(deviceSMass, SMass.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceSMass, Pointer.to(SMass), SMass.length
				* Sizeof.DOUBLE);
		CUdeviceptr deviceHMass = new CUdeviceptr();
		cuMemAlloc(deviceHMass, HMass.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceHMass, Pointer.to(HMass), HMass.length
				* Sizeof.DOUBLE);
		CUdeviceptr deviceNMass = new CUdeviceptr();
		cuMemAlloc(deviceNMass, NMass.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceNMass, Pointer.to(NMass), NMass.length
				* Sizeof.DOUBLE);
		CUdeviceptr deviceOMass = new CUdeviceptr();
		cuMemAlloc(deviceOMass, OMass.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceOMass, Pointer.to(OMass), OMass.length
				* Sizeof.DOUBLE);

		CUdeviceptr deviceCP = new CUdeviceptr();
		cuMemAlloc(deviceCP, CP.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceCP, Pointer.to(CP), CP.length * Sizeof.DOUBLE);
		CUdeviceptr deviceSP = new CUdeviceptr();
		cuMemAlloc(deviceSP, SP.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceSP, Pointer.to(SP), SP.length * Sizeof.DOUBLE);
		CUdeviceptr deviceHP = new CUdeviceptr();
		cuMemAlloc(deviceHP, HP.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceHP, Pointer.to(HP), HP.length * Sizeof.DOUBLE);
		CUdeviceptr deviceNP = new CUdeviceptr();
		cuMemAlloc(deviceNP, NP.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceNP, Pointer.to(NP), NP.length * Sizeof.DOUBLE);
		CUdeviceptr deviceOP = new CUdeviceptr();
		cuMemAlloc(deviceOP, OP.length * Sizeof.DOUBLE);
		cuMemcpyHtoD(deviceOP, Pointer.to(OP), OP.length * Sizeof.DOUBLE);

		CUdeviceptr deviceArrayCombination = new CUdeviceptr();
		cuMemAlloc(deviceArrayCombination, arrayCombination.length * Sizeof.INT);
		cuMemcpyHtoD(deviceArrayCombination, Pointer.to(arrayCombination),
				arrayCombination.length * Sizeof.INT);

		cudaHostRegister(Pointer.to(indexCombination), ELEMENT_NUMBER
				* MASS_SPECTRAL_NUMBER * Sizeof.INT,
				JCuda.cudaHostRegisterPortable);

		cudaHostRegister(Pointer.to(hostMassOutput), MASS_SPECTRAL_NUMBER
				* bufferSize * Sizeof.DOUBLE, JCuda.cudaHostRegisterDefault);
		cudaHostRegister(Pointer.to(hostPOutput), MASS_SPECTRAL_NUMBER
				* bufferSize * Sizeof.DOUBLE, JCuda.cudaHostRegisterDefault);

		CUdeviceptr[] deviceIndexCombinations = new CUdeviceptr[STREAM_NUMBER];
		CUdeviceptr[] deviceMassOutputs = new CUdeviceptr[STREAM_NUMBER];
		CUdeviceptr[] devicePOutputs = new CUdeviceptr[STREAM_NUMBER];

		CUstream[] streams = new CUstream[STREAM_NUMBER];

		for (int i = 0; i != STREAM_NUMBER; i++) {
			CUstream stream = new CUstream();
			cuStreamCreate(stream, 0);
			streams[i] = stream;

			deviceIndexCombinations[i] = new CUdeviceptr();
			cuMemAlloc(deviceIndexCombinations[i], ELEMENT_NUMBER
					* MASS_SPECTRAL_CHUNKSIZE * Sizeof.INT);

			deviceMassOutputs[i] = new CUdeviceptr();
			cuMemAlloc(deviceMassOutputs[i], MASS_SPECTRAL_CHUNKSIZE
					* bufferSize * Sizeof.DOUBLE);

			devicePOutputs[i] = new CUdeviceptr();
			cuMemAlloc(devicePOutputs[i], MASS_SPECTRAL_CHUNKSIZE * bufferSize
					* Sizeof.DOUBLE);
		}

		for (int i = 0; i != (MASS_SPECTRAL_NUMBER + MASS_SPECTRAL_CHUNKSIZE - 1)
				/ MASS_SPECTRAL_CHUNKSIZE; i++) {

			// copy input data
			cuMemcpyHtoDAsync(
					deviceIndexCombinations[i % STREAM_NUMBER],
					Pointer.to(indexCombination).withByteOffset(
							i * MASS_SPECTRAL_CHUNKSIZE * ELEMENT_NUMBER
									* Sizeof.INT), ELEMENT_NUMBER
							* MASS_SPECTRAL_CHUNKSIZE * Sizeof.INT, streams[i
							% STREAM_NUMBER]);

			// Set up the kernel parameters: A pointer to an array
			// of pointers which point to the actual values.
			Pointer kernelParameters = Pointer.to(Pointer.to(deviceCMass),
					Pointer.to(deviceSMass), Pointer.to(deviceHMass),
					Pointer.to(deviceNMass), Pointer.to(deviceOMass),
					Pointer.to(deviceCP), Pointer.to(deviceSP),
					Pointer.to(deviceHP), Pointer.to(deviceNP),
					Pointer.to(deviceOP), Pointer.to(deviceArrayCombination),
					Pointer.to(deviceIndexCombinations[i % STREAM_NUMBER]),
					Pointer.to(deviceMassOutputs[i % STREAM_NUMBER]),
					Pointer.to(devicePOutputs[i % STREAM_NUMBER]));

			// Call the kernel function.
			int blockSizeX = 1;
			int gridSizeX = MASS_SPECTRAL_CHUNKSIZE;
			cuLaunchKernel(function, gridSizeX, 1, 1, // Grid dimension
					blockSizeX, 1, 1, // Block dimension
					0, streams[i % STREAM_NUMBER], // Shared memory size and
													// stream
					kernelParameters, null // Kernel- and extra parameters
			);
			cuCtxSynchronize();

			cuMemcpyDtoHAsync(
					Pointer.to(hostMassOutput).withByteOffset(
							i * MASS_SPECTRAL_CHUNKSIZE * bufferSize
									* Sizeof.DOUBLE), deviceMassOutputs[i
							% STREAM_NUMBER], MASS_SPECTRAL_CHUNKSIZE
							* bufferSize * Sizeof.DOUBLE, streams[i
							% STREAM_NUMBER]);
			cuMemcpyDtoHAsync(
					Pointer.to(hostPOutput).withByteOffset(
							i * MASS_SPECTRAL_CHUNKSIZE * bufferSize
									* Sizeof.DOUBLE), devicePOutputs[i
							% STREAM_NUMBER], MASS_SPECTRAL_CHUNKSIZE
							* bufferSize * Sizeof.DOUBLE, streams[i
							% STREAM_NUMBER]);
		}

		JCuda.cudaHostUnregister(Pointer.to(hostMassOutput));
		JCuda.cudaHostUnregister(Pointer.to(hostPOutput));
		JCuda.cudaHostUnregister(Pointer.to(indexCombination));

		cuMemFree(deviceCMass);
		cuMemFree(deviceSMass);
		cuMemFree(deviceHMass);
		cuMemFree(deviceNMass);
		cuMemFree(deviceOMass);
		cuMemFree(deviceCP);
		cuMemFree(deviceSP);
		cuMemFree(deviceHP);
		cuMemFree(deviceNP);
		cuMemFree(deviceOP);
		for (int i = 0; i != STREAM_NUMBER; i++) {
			cuMemFree(deviceIndexCombinations[i]);
			cuMemFree(deviceMassOutputs[i]);
			cuMemFree(devicePOutputs[i]);
		}

		JCuda.cudaDeviceReset();

		for (int i = 0; i != MASS_SPECTRAL_NUMBER; i++) {
			String formula = listParameters.get(i).Formula;
			Options option = new Options();

			option.parseChemFormulaAndAdd(formula);
			option.setFastCalc(10000);
			option.setPrintOutput(true);
			option.setBinPeaks(IPC.binningType.FIXED_BINNING);
			option.setCharge(1);
			Results results = new Results(option);

			TreeSet<Peak> gpuResultPeaks = new TreeSet<Peak>();
			int offSet = i * bufferSize;
			for (int j = 0; j != bufferSize; j++) {
				double mass = hostMassOutput.get(offSet + j);
				double p = hostPOutput.get(offSet + j);

				if (p != 0d) {
					Peak peak = new Peak(mass, p);
					if (!gpuResultPeaks.contains(peak)) {
						gpuResultPeaks.add(peak);
					}
				}
			}

			results.setPeaks(gpuResultPeaks);
			IPC.fillResults(results);
			resultsList.add(results);
		}

		return resultsList;
	}

	private static String preparePtxFile(String cuFileName) throws Exception {
		int endIndex = cuFileName.lastIndexOf('.');
		if (endIndex == -1) {
			endIndex = cuFileName.length() - 1;
		}
		String ptxFileName = cuFileName.substring(0, endIndex + 1) + "ptx";
		File ptxFile = new File(ptxFileName);
		if (ptxFile.exists()) {
			return ptxFileName;
		}

		URL url = GPUSolution.class.getResource("");
		File cuFile = new File(new URI(url.toString() + "/" + cuFileName));
		if (!cuFile.exists()) {
			throw new IOException("Input file not found: " + cuFileName);
		}
		String modelString = "-m" + System.getProperty("sun.arch.data.model");
		String command = "nvcc " + modelString + " -ptx " + cuFile.getPath()
				+ " -o " + ptxFileName;

		System.out.println("Executing\n" + command);
		Process process = Runtime.getRuntime().exec(command);

		String errorMessage = new String(toByteArray(process.getErrorStream()));
		String outputMessage = new String(toByteArray(process.getInputStream()));
		int exitValue = 0;
		try {
			exitValue = process.waitFor();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while waiting for nvcc output",
					e);
		}

		if (exitValue != 0) {
			System.out.println("nvcc process exitValue " + exitValue);
			System.out.println("errorMessage:\n" + errorMessage);
			System.out.println("outputMessage:\n" + outputMessage);
			throw new IOException("Could not create .ptx file: " + errorMessage);
		}

		System.out.println("Finished creating PTX file");
		return ptxFileName;
	}

	private static byte[] toByteArray(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[8192];
		while (true) {
			int read = inputStream.read(buffer);
			if (read == -1) {
				break;
			}
			baos.write(buffer, 0, read);
		}
		return baos.toByteArray();
	}

	private PeaksData4GPU processElementPeaks(
			Map<String, PeaksData> elementPeaks, int cMax, int sMax, int hMax,
			int nMax, int oMax) {

		int CMaxLength = 0;

		for (int i = 0; i != cMax; i++) {
			PeaksData data = elementPeaks.get("C" + String.valueOf(i + 1));
			int localLength = data.getMass().length;
			if (localLength > CMaxLength) {
				CMaxLength = localLength;
			}
		}

		int HMaxLength = 0;

		for (int i = 0; i != hMax; i++) {
			PeaksData data = elementPeaks.get("H" + String.valueOf(i + 1));
			int localLength = data.getMass().length;
			if (localLength > HMaxLength) {
				HMaxLength = localLength;
			}
		}

		int NMaxLength = 0;

		for (int i = 0; i != nMax; i++) {
			PeaksData data = elementPeaks.get("N" + String.valueOf(i + 1));
			int localLength = data.getMass().length;
			if (localLength > NMaxLength) {
				NMaxLength = localLength;
			}
		}

		int OMaxLength = 0;

		for (int i = 0; i != oMax; i++) {
			PeaksData data = elementPeaks.get("O" + String.valueOf(i + 1));
			int localLength = data.getMass().length;
			if (localLength > OMaxLength) {
				OMaxLength = localLength;
			}
		}

		int SMaxLength = 0;

		for (int i = 0; i != sMax; i++) {
			PeaksData data = elementPeaks.get("S" + String.valueOf(i + 1));
			int localLength = data.getMass().length;
			if (localLength > SMaxLength) {
				SMaxLength = localLength;
			}
		}

		double[] CMass = new double[CMaxLength * cMax];
		double[] CP = new double[CMaxLength * cMax];

		double[] HMass = new double[HMaxLength * hMax];
		double[] HP = new double[HMaxLength * hMax];

		double[] NMass = new double[NMaxLength * nMax];
		double[] NP = new double[NMaxLength * nMax];

		double[] OMass = new double[OMaxLength * oMax];
		double[] OP = new double[OMaxLength * oMax];

		double[] SMass = new double[SMaxLength * sMax];
		double[] SP = new double[SMaxLength * sMax];

		// not use now !!
		double[] PMass = new double[1 * 100];
		double[] PP = new double[1 * 100];

		double[] FeMass = new double[44 * 100];
		double[] FeP = new double[44 * 100];

		for (int i = 0; i != cMax; i++) {
			PeaksData data = elementPeaks.get("C" + String.valueOf(i + 1));
			double[] mass = data.getMass();
			double[] p = data.getP();
			for (int j = 0; j != mass.length; j++) {
				CMass[i * CMaxLength + j] = mass[j];
				CP[i * CMaxLength + j] = p[j];
			}
		}

		for (int i = 0; i != hMax; i++) {
			PeaksData data = elementPeaks.get("H" + String.valueOf(i + 1));
			double[] mass = data.getMass();
			double[] p = data.getP();
			for (int j = 0; j != mass.length; j++) {
				HMass[i * HMaxLength + j] = mass[j];
				HP[i * HMaxLength + j] = p[j];
			}
		}

		for (int i = 0; i != nMax; i++) {
			PeaksData data = elementPeaks.get("N" + String.valueOf(i + 1));
			double[] mass = data.getMass();
			double[] p = data.getP();
			for (int j = 0; j != mass.length; j++) {
				NMass[i * NMaxLength + j] = mass[j];
				NP[i * NMaxLength + j] = p[j];
			}
		}

		for (int i = 0; i != oMax; i++) {
			PeaksData data = elementPeaks.get("O" + String.valueOf(i + 1));
			double[] mass = data.getMass();
			double[] p = data.getP();
			for (int j = 0; j != mass.length; j++) {
				OMass[i * OMaxLength + j] = mass[j];
				OP[i * OMaxLength + j] = p[j];
			}
		}

		for (int i = 0; i != sMax; i++) {
			PeaksData data = elementPeaks.get("S" + String.valueOf(i + 1));
			double[] mass = data.getMass();
			double[] p = data.getP();
			for (int j = 0; j != mass.length; j++) {
				SMass[i * SMaxLength + j] = mass[j];
				SP[i * SMaxLength + j] = p[j];
			}
		}

		List<Integer> maxCombination = new ArrayList<Integer>();
		maxCombination.add(CMaxLength);
		maxCombination.add(SMaxLength);
		maxCombination.add(HMaxLength);
		maxCombination.add(NMaxLength);
		maxCombination.add(OMaxLength);

		return new PeaksData4GPU(CMass, CP, HMass, HP, NMass, NP, OMass, OP,
				SMass, SP, PMass, PP, FeMass, FeP, maxCombination);
	}

	public class PeaksData4GPU {

		private List<Integer> maxCombination;

		public List<Integer> getTotalCombinations() {
			return maxCombination;
		}

		private double[] CMass;
		private double[] CP;
		private double[] HMass;
		private double[] HP;
		private double[] NMass;
		private double[] NP;
		private double[] OMass;
		private double[] OP;
		private double[] SMass;
		private double[] SP;
		private double[] PMass;
		private double[] PP;
		private double[] FeMass;
		private double[] FeP;

		public PeaksData4GPU(double[] CMass, double[] CP, double[] HMass,
				double[] HP, double[] NMass, double[] NP, double[] OMass,
				double[] OP, double[] SMass, double[] SP, double[] PMass,
				double[] PP, double[] FeMass, double[] FeP,
				List<Integer> maxCombination) {
			this.CMass = CMass;
			this.HMass = HMass;
			this.NMass = NMass;
			this.OMass = OMass;
			this.SMass = SMass;
			this.PMass = PMass;
			this.FeMass = FeMass;
			this.CP = CP;
			this.HP = HP;
			this.NP = NP;
			this.OP = OP;
			this.SP = SP;
			this.PP = PP;
			this.FeP = FeP;
			this.maxCombination = maxCombination;
		}

		public double[] getCMass() {
			return CMass;
		}

		public double[] getCP() {
			return CP;
		}

		public double[] getHMass() {
			return HMass;
		}

		public double[] getHP() {
			return HP;
		}

		public double[] getNMass() {
			return NMass;
		}

		public double[] getNP() {
			return NP;
		}

		public double[] getOMass() {
			return OMass;
		}

		public double[] getOP() {
			return OP;
		}

		public double[] getSMass() {
			return SMass;
		}

		public double[] getSP() {
			return SP;
		}

		public double[] getPMass() {
			return PMass;
		}

		public double[] getPP() {
			return PP;
		}

		public double[] getFeMass() {
			return FeMass;
		}

		public double[] getFeP() {
			return FeP;
		}
	}
}
