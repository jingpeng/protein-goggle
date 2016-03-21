package edu.tongji.proteingoggle.concurrent;



import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import edu.tongji.proteingoggle.bll.Analysis;
import edu.tongji.proteingoggle.bll.AnalysisParameter;
import edu.tongji.proteingoggle.external.IPC;

public class ForkJoinAnalysisCalc extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int THRESHOLD = 100;

	private int start;							//
	private int end;
	private IPC.Results[] resultsList;			//result list
	final private Analysis analysis ;
	final List<AnalysisParameter> listParameters;  // parameters list

	public ForkJoinAnalysisCalc(IPC.Results[] resultsList, Analysis analysis, List<AnalysisParameter> listParameters,
			int start, int end) {
		this.resultsList = resultsList;
		this.start = start;
		this.end = end;
		this.analysis = new Analysis();
		this.listParameters = listParameters;
	}

	@Override
	protected void compute() {
		// TODO Auto-generated method stub

		//List<AnalysisParameter> list = new ArrayList<AnalysisParameter>();
		
		if ((end - start) < THRESHOLD) {
//			System.out.println("start:" + start + "  end" + end);
			for (int i = start; i < end; i++) {
				System.out.println("for !=:"+i+" start:" + start + "  end" + end);
				AnalysisParameter parameter = listParameters.get(i);
				IPC.Results results = analysis.AnalysisCalc(parameter);
				resultsList[i] = results;
			}
		} else {
			
			int middle = (start + end) / 2;
			System.out.println("start:" + start + "  end" + end + " middle"+middle);
			ForkJoinAnalysisCalc left = new ForkJoinAnalysisCalc(resultsList,
					analysis, listParameters,start, middle);

			ForkJoinAnalysisCalc right = new ForkJoinAnalysisCalc(resultsList,
					analysis,listParameters, middle, end);

			left.fork();
			right.fork();

		}

		//return resultsList;
	}

	
}
