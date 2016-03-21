package edu.tongji.proteingoggle.controller;

import java.util.List;

import com.jfinal.core.Controller;

import edu.tongji.proteingoggle.datamodel.User;

public class RegisterController extends Controller {
	public void index() {
		render("pages-sign-up.html");
	}

	public void submit() {
		String workEmail = getPara("work_email");
		String password = getPara("password");
		String firstName = getPara("first_name");
		String lastName = getPara("last_name");
		String piFirstName = getPara("pi_first_name");
		String piLastName = getPara("pi_last_name");
		String institution = getPara("institution");

		List<User> users = User.dao
				.find("select * from user where work_email = '" + workEmail
						+ "'");
		if (users.size() > 0) {
			renderText("User is already existed!");
		} else {
			User.dao.set("work_email", workEmail).set("password", password)
					.set("first_name", firstName).set("last_name", lastName)
					.set("pi_first_name", piFirstName)
					.set("pi_last_name", piLastName)
					.set("institution", institution)
					.set("is_vip","F").save();
			renderText("success");
		}
	}
}
