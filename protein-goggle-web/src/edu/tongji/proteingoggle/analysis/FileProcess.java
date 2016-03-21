package edu.tongji.proteingoggle.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileProcess {

	public void getTxtContent(File proteinFile,
			Map<String, Map<String, String>> dictFlatText,
			Map<String, List<String>> dictFTMark, Boolean flag)
			throws FileNotFoundException {
		String sLine = "";
		String sHead = "";
		String sContent = "";
		String sPreHead = "";

		Map<String, String> dictItem = new HashMap<String, String>();
		List<String> lstFt = new ArrayList<String>();

		File sr = proteinFile;
		InputStream ms = new FileInputStream(sr);
		InputStreamReader read = new InputStreamReader(ms);
		BufferedReader bufferedReader = new BufferedReader(read);
		try {

			if (!dictFlatText.isEmpty())
				dictFlatText.clear();
			if (!dictFTMark.isEmpty())
				dictFTMark.clear();

			while ((sLine = bufferedReader.readLine()) != null) {

				if (sLine.trim().length() == 2 && sLine.equals("//")) {
					String[] arrSeq = dictItem.get("SQ").split(";");
					// System.out.println(arrSeq.length);
					dictItem.put("SQ",
							arrSeq[arrSeq.length - 1].replaceAll("\\s", ""));
					if (!flag) {
						dictItem.put("SQ", RecursionReverse(dictItem.get("SQ")));
						String pattern = " +";
						String replacement = "";
						String contents = sContent.replaceAll(pattern,
								replacement);
						int seqLength = contents.length();
						for (int i = 0; i < lstFt.size(); i++) {
							String[] contentSplit = lstFt.get(i)
									.replaceAll(pattern, ";").split(";");
							try {
								contentSplit[1] = String
										.valueOf(seqLength
												- Integer
														.parseInt(contentSplit[1])
												+ 1);
								contentSplit[2] = String
										.valueOf(seqLength
												- Integer
														.parseInt(contentSplit[2])
												+ 1);
								// lstFT[i] = lstFT[i].Replace(contentSplit[1],
								// point1);
								// lstFT[i] = lstFT[i].Replace(contentSplit[1],
								// point2);
								lstFt.set(i, "");
								for (int j = 0; j < contentSplit.length; j++) {
									lstFt.set(i, lstFt.get(i) + contentSplit[j]
											+ "  ");
								}
							} catch (Exception e) {
							}
						}

						// ////////////////////// soap modify end 2014 10 02
					}

					dictFTMark.put(dictItem.get("ID"), lstFt);
					dictFlatText.put(dictItem.get("ID"), dictItem);
					dictItem = new HashMap<String, String>();
					lstFt = new ArrayList<String>();
					continue;
				}
				if (sLine.length() < 5)
					continue;
				sHead = sLine.substring(0, 5).trim();
				sContent = sLine.substring(5);
				if (sHead.trim().equals(""))
					sHead = sPreHead;
				if (dictItem.containsKey(sHead) == false) {
					dictItem.put(sHead, sContent);
					if (!sHead.trim().isEmpty())
						sPreHead = sHead;
				} else {
					dictItem.put(sHead, dictItem.get(sHead) + sContent);
				}
				if (dictItem.containsKey("FT")) {
					lstFt.add(sContent);
				}
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				ms.close();
				read.close();
				bufferedReader.close();
				// System.out.println(dictFlatText.isEmpty());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static String RecursionReverse(String str) {
		if (str.length() == 1)// 当仅剩下最后一个字母时
			return str;
		else {

			String strFist = str.substring(0, 1);// 得到第一个字母
			String strCut = str.substring(1);// 去掉第一个字母的部分
			String strReverseNext;// 经过递归逆序后的字母
			strReverseNext = RecursionReverse(strCut);// 此处逆序递归调用
			return strReverseNext + strFist;// 逆序的原理是反过来拼接

		}
	}
}
