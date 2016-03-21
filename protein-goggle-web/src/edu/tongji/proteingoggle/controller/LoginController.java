package edu.tongji.proteingoggle.controller;

import java.util.List;

import com.jfinal.core.Controller;

import edu.tongji.proteingoggle.datamodel.User;

public class LoginController extends Controller {
	public void index() {
		render("pages-login.html");
	}
	
	public void submit() {
		String workEmail = getPara("work_email");
		String password = getPara("password");
		List<User> users = User.dao
				.find("select * from user where work_email = '" + workEmail
						+ "'");
		if(users.size() > 0){
			User currentUser = users.get(0);
			if(password.equals(currentUser.getStr("password"))){
				setSessionAttr("user_email", workEmail);
				if(currentUser.getStr("is_vip").equals("T"))
				{
					setSessionAttr("is_vip", true);
				}
				else
				{
					setSessionAttr("is_vip", false);
				}
				renderText("success");
			}else{
				renderText("Password is incorrect!");
			}
		}else{
			renderText("User is not existed!");
		}
	}
}