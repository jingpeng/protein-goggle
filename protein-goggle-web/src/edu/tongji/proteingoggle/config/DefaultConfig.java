package edu.tongji.proteingoggle.config;

import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

import edu.tongji.proteingoggle.controller.AdminController;
import edu.tongji.proteingoggle.controller.HomeController;
import edu.tongji.proteingoggle.controller.IndexController;
import edu.tongji.proteingoggle.controller.LoginController;
import edu.tongji.proteingoggle.controller.RegisterController;
import edu.tongji.proteingoggle.controller.SubmitJobController;
import edu.tongji.proteingoggle.datamodel.Admin;
import edu.tongji.proteingoggle.datamodel.JobFinished;
import edu.tongji.proteingoggle.datamodel.User;

public class DefaultConfig extends JFinalConfig {
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}

	public void configRoute(Routes me) {
		me.add("/flatdream/login", LoginController.class);
		me.add("/flatdream/register", RegisterController.class);
		me.add("/flatdream/homepage", HomeController.class);
		me.add("/flatdream/submit",SubmitJobController.class);
		me.add("/flatdream/index",IndexController.class);
		me.add("/flatdream/admin",AdminController.class);

	}

	public void configPlugin(Plugins me) {
		C3p0Plugin cp = new C3p0Plugin(
				"jdbc:mysql://localhost:3306/protein_goggle", "root", "root");
		me.add(cp);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(cp);
		me.add(arp);
		arp.addMapping("user", User.class);
		arp.addMapping("admin", Admin.class);
		arp.addMapping("job_finished", JobFinished.class);
	}

	public void configInterceptor(Interceptors me) {
	}

	public void configHandler(Handlers me) {
	}
}
