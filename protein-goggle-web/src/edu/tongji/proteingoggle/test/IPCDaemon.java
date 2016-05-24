package edu.tongji.proteingoggle.test;

import java.io.File;
import java.util.Calendar;

import edu.tongji.proteingoggle.analysis.CreateDatabase;

public class IPCDaemon {

	public static void main(String[] args) {
		final String path = "/Users/i330739/gambition-recorder";

		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {
					Calendar now = Calendar.getInstance();
					int hour = now.get(Calendar.HOUR_OF_DAY);
					if (hour == 11) {
						System.out.println("==========DB setup started=========");
						traverseAllFiles(new File(path));
						break;
					}

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	private static void traverseAllFiles(File dir) {
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++) {
			// System.out.println(fs[i].getAbsolutePath());
			System.out.println(fs[i].getName());
			if (fs[i].getName().endsWith(".txt") && fs[i].getName().startsWith("A")) {
				CreateDatabase cdb = new CreateDatabase();
				try {
					cdb.Excute(fs[i]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				File newfile = new File(dir.getAbsolutePath() + "/" + "B" + fs[i].getName().substring(1));
				fs[i].renameTo(newfile);
			}
			if (fs[i].isDirectory()) {
				try {
					traverseAllFiles(fs[i]);
				} catch (Exception e) {
				}
			}
		}
	}
}
