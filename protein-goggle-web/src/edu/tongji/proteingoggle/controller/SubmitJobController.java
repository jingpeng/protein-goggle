package edu.tongji.proteingoggle.controller;

import java.io.File;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;

import edu.tongji.proteingoggle.analysis.CreateDatabase;
import edu.tongji.proteingoggle.jobcontrol.Job;
import edu.tongji.proteingoggle.jobcontrol.JobQueue;

public class SubmitJobController extends Controller {
	public void index() {
		render("pages-submit.html");
	}

	public void post() {
		
		String workEmail = getSessionAttr("user_email");
		String path = getSession().getServletContext().getRealPath("/");
		UploadFile proteinUploadFile = getFile("protein-file-input", path
				+ "\\upload\\" + workEmail, 50 * 1024 * 1024);
		File proteinFile = proteinUploadFile.getFile();
		Job job = new Job(workEmail,proteinFile);
		
		Boolean is_vip = getSessionAttr("is_vip");
		
		if(is_vip != null&&is_vip)
		{
			System.out.println("This user is vip");
			job.setIs_VIP_job(true);
			JobQueue.appendVIPJob(job);
			

		}
		else {
			JobQueue.appendNormalJob(job);
		}
		JobQueue.printQueue();
//		CreateDatabase cdb = new CreateDatabase();
//		try {
//			cdb.Excute(proteinFile);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		renderText("success");
	}
}