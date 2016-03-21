package edu.tongji.proteingoggle.controller;

import com.jfinal.core.Controller;

public class IndexController extends Controller {
	public void index() {
		render("index.html");
	}
	
	public void welcome() {
		render("welcome.html");
	}
	
	public void team(){
		render("the_team.html");
	}
	
	public void collaborators(){
		render("collaborators.html");
	}
	
	public void example(){
		render("example_data.html");
	}
	
	public void submit(){
		render("submit_a_job.html");
	}
	
	public void jobs(){
		render("my_jobs.html");
	}
	
	public void references(){
		render("references.html");
	}
	
	public void forum(){
		render("forum.html");
	}
	
	public void employment(){
		render("employment.html");
	}
	
	public void contact(){
		render("contact_us.html");
	}
}
