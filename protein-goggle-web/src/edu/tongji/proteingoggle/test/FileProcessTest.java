package edu.tongji.proteingoggle.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import edu.tongji.proteingoggle.analysis.CreateDatabase;
import edu.tongji.proteingoggle.analysis.FileProcess;

public class FileProcessTest {

	@Test
	public void testFileInput() {
		FileProcess fp = new FileProcess();
		File proteinFile = new File("F:\\Sophomore B\\ÖÊÆ×\\P62258.txt");
		Map<String, Map<String, String>> dictFlatText = new HashMap<String, Map<String, String>>();
		Map<String, List<String>> dictFTMark = new HashMap<String, List<String>>();
		TreeMap<String, String> result = new TreeMap<String, String>();
		Boolean flag = true;
		try {
			fp.getTxtContent(proteinFile, dictFlatText, dictFTMark, flag);

			// System.out.println(result.isEmpty());
			for (String i : dictFlatText.keySet()) {
				System.out.println(i);
				result.putAll(dictFlatText.get(i));

				for (String j : result.keySet())
					System.out.println(j + " ---- " + result.get(j));
				System.out.println("===========");
			}
			for (String i : dictFTMark.keySet()) {
				System.out.println(i);

				for (String j : dictFTMark.get(i))
					System.out.println(j + " ---- @@");
				System.out.println("===========");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testFileName() {
		String sFileName = "F:\\Sophomore B\\ÖÊÆ×\\P62258.txt";
		File file = new File(sFileName);

		System.out.println(file.getName().substring(0,
				file.getName().lastIndexOf(".")));
		List<Integer> remove = new ArrayList<Integer>();
		remove.add(5);
		remove.add(3);
		remove.add(4);
		System.out.println(remove.toString());
		remove.sort(Collections.reverseOrder());

		System.out.println(remove.toString());
	}

	@Test
	public void test01() throws Exception {
		File proteinFile = new File("F:\\Sophomore B\\ÖÊÆ×\\P62258.txt");
		CreateDatabase cdb = new CreateDatabase();
		cdb.Excute(proteinFile);
	}
}
