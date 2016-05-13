package edu.tongji.proteingoggle.test;

import java.io.File;

import edu.tongji.proteingoggle.analysis.CreateDatabase;

public class IPCTest {

	public static void main(String[] args) {
		CreateDatabase cdb = new CreateDatabase();
		try {
			cdb.Excute(new File(args[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
