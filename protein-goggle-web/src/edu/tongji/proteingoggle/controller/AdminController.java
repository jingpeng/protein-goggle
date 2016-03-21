package edu.tongji.proteingoggle.controller;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;

import edu.tongji.proteingoggle.analysis.CreateDatabase;
import edu.tongji.proteingoggle.datamodel.Admin;
import edu.tongji.proteingoggle.datamodel.JobFinished;
import edu.tongji.proteingoggle.datamodel.User;
import edu.tongji.proteingoggle.jobcontrol.Job;
import edu.tongji.proteingoggle.jobcontrol.JobQueue;
import edu.tongji.proteingoggle.jobcontrol.JobState;

public class AdminController extends Controller {
	private String is_admin;

	public void index() {
		is_admin = getSessionAttr("is_admin");
		if(is_admin==null || is_admin.equals("false"))
		{
			render("admin-login.html");
		}
		else
		{
			render("admin-index.html");
		}
	}

	public void login() {
		String name = getPara("name");
		String password = getPara("password");
		List<Admin> admins = Admin.dao
				.find("select * from admin where name = '" + name
						+ "'");
		if(admins.size() > 0){
			Admin currentAdmin = admins.get(0);
			if(password.equals(currentAdmin.getStr("password"))){
				setSessionAttr("admin_name", name);
				setSessionAttr("is_admin", "true");
				renderText("success");
			}else{
				renderText("Password is incorrect!");
			}
		}else{
			renderText("admin does not exist!");
		}
	}
	
	public void logout() {
		setSessionAttr("admin_name", null);
		setSessionAttr("is_admin", null);
		removeSessionAttr("admin_name");
		removeSessionAttr("is_admin");
		renderText("success");
	}
	
	public void getQueue(){
		List<Job> jobQueue = JobQueue.getQueue();

		
		renderJson("jobQueue", jobQueue);

	}
	public void getFinishedQueue(){
		List<JobFinished> jobs = JobFinished.dao
				.find("select * from job_finished");
		renderJson("jobFinished", jobs);
	}
	
	public void startExecution(){
		List<Job> jobQueue = JobQueue.getQueue();
		redirect("/flatdream/admin");
		
		Iterator<Job> i = jobQueue.iterator();
		while (i.hasNext()) {
		   Job j = i.next();
		   try
		   {
			   execute(j);
		   } catch (Exception e) {
				e.printStackTrace();
			}
		   i.remove();
		}
	}
	
	private void execute(Job job){
		job.setState(JobState.running);
		CreateDatabase cdb = new CreateDatabase();

		File proteinFile = job.getFile();
		try {
			cdb.Excute(proteinFile);
			job.setState(JobState.finished);
			saveJob(job);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	 
	private void saveJob(Job job)
	{
		new JobFinished().set("email", job.getSubmitUserEmail())
		.set("file_path", job.getFile().getAbsolutePath())
		.set("state",job.getState().toString())
		.set("time",job.getUploadTime())
		.save();
		String contextPath = getSession().getServletContext().getRealPath("/");
		
		
	}
	
	public void moveUp()
	{
		int index = getParaToInt(0);
		List<Job> jobQueue = JobQueue.getQueue();
		Job job = jobQueue.remove(index);
		jobQueue.add(0, job);
		redirect("/flatdream/admin");
		System.out.println("job"+index+" move up");
	}
	
}
