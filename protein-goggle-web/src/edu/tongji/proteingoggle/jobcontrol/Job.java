package edu.tongji.proteingoggle.jobcontrol;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Job {
	
	private String submitUserEmail;
	private File file;
	private JobState state;
	private String uploadTime;
	private Boolean is_VIP_job;
	public Job(String submitUserEmail, File file)
	{
		this.submitUserEmail = submitUserEmail;
		this.file = file;
		this.state = JobState.pending;
		this.uploadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		this.is_VIP_job = false;
	}

	public String getSubmitUserEmail() {
		return submitUserEmail;
	}

	public void setSubmitUserEmail(String submitUserEmail) {
		this.submitUserEmail = submitUserEmail;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public JobState getState() {
		return state;
	}

	public void setState(JobState state) {
		this.state = state;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Boolean getIs_VIP_job() {
		return is_VIP_job;
	}

	public void setIs_VIP_job(Boolean is_VIP_job) {
		this.is_VIP_job = is_VIP_job;
	}
	
	
	
}

