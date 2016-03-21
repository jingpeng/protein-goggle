package edu.tongji.proteingoggle.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import edu.tongji.proteingoggle.bll.Analysis;
import edu.tongji.proteingoggle.bll.AnalysisParameter;
import edu.tongji.proteingoggle.external.IPC;

public class ConcurrentCal {

	/**
	 * @param args
	 */
	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	//
	// // aParameter.FastCalc = session.FastCalc;// ��� session.FastCalc
	// // // ֵ���ã�2014/10/25 soap �� recheck
	// // // note��
	// // aParameter.Sequence = sSeq;
	// // aParameter.Formula = sFormula;
	// // aParameter.ValenceState = 1;
	//
	// try {
	// excute();
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ExecutionException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	// private static int NUMBER = 1000;
	//
	// public static void excute() throws InterruptedException,
	// ExecutionException {
	// Analysis analysis = new Analysis();
	//
	// List<AnalysisParameter> listParameters = new
	// ArrayList<AnalysisParameter>();
	//
	// // init params
	//
	// for (int i = 0; i < NUMBER; i++) {
	// AnalysisParameter p = new AnalysisParameter();
	// listParameters.add(p);
	// }
	//
	// long startMili = System.currentTimeMillis();// ��ǰʱ���Ӧ�ĺ�����
	//
	// // IPC.Results results = analysis.AnalysisCalc(aParameter);
	// excuteForkJoinAnalysisCalc(analysis, listParameters);
	//
	// long endMili = System.currentTimeMillis();
	// System.out.println("�ܺ�ʱΪ��" + (endMili - startMili) + "����");
	//
	// }

	// ���߳����� listParameters.size() �õ�����ֵ������һ���߳������Դ�����ٸ�parameters
	public static int THRESHOLD = 100;
	// Ԥ���߳��� ��ÿ��Խ�ģ��õ���õ���ֵ����������ֱ��Ԥ��
	private static final int ThreadNumber = 1;

	// analysis ��Ҫ��ʼ��
	// listParameters Ϊ������list, ��Ҫ��ʼ��
	// excuteForkJoinAnalysisCalc(analysis, listParameters)
	// ����ConcurrentCal.excuteForkJoinAnalysisCalc(analysis, listParameters)
	// �滻ԭ�� for ѭ���� ˳��ִ��
	public static List<IPC.Results> excuteForkJoinAnalysisCalc(
			Analysis analysis, List<AnalysisParameter> listParameters)
			throws InterruptedException, ExecutionException {

		final int number = listParameters.size();

		// calculate THRESHOLD depend on listParameters.size()
		THRESHOLD = calculateThreshold(number);

		System.out.println(THRESHOLD);

		IPC.Results[] resultsList = new IPC.Results[number];

		// fork join
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		// Future<IPC.Results[]> result = forkJoinPool
		// .submit(new ForkJoinAnalysisCalc(resultsList, analysis,
		// listParameters, 0,
		// number));

		forkJoinPool.submit(new ForkJoinAnalysisCalc(resultsList, analysis,
				listParameters, 0, number));

		forkJoinPool.shutdown();
		forkJoinPool.awaitTermination(5000, TimeUnit.SECONDS);
		// end

		// ����תList
		// IPC.Results[] finalResults = result.get();
		// List<IPC.Results> list = Arrays.asList(finalResults);
		List<IPC.Results> list = Arrays.asList(resultsList);

		return list;
	}

	private static int calculateThreshold(int length) {
		if (length <= THRESHOLD)
			return length;

		return THRESHOLD;
	}
}
