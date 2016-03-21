package edu.tongji.proteingoggle.controller;

import com.jfinal.core.Controller;

public class HomeController extends Controller {
	public void index() {
		render("pages-home.html");
	}
}