package edu.tongji.proteingoggle.analysis;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.tongji.proteingoggle.bll.DataBaseBLL;
import edu.tongji.proteingoggle.bll.PeptideBLL;
import edu.tongji.proteingoggle.bll.PeptideDissociationBLL;
import edu.tongji.proteingoggle.bll.PeptideModResBLL;
import edu.tongji.proteingoggle.bll.Table;
import edu.tongji.proteingoggle.dal.ElementPeaksDAL;
import edu.tongji.proteingoggle.dal.ElementPeaksDAL.PeaksData;
import edu.tongji.proteingoggle.model.DataBaseParameter;
import edu.tongji.proteingoggle.model.ProteinModResConfig;
import edu.tongji.proteingoggle.model.ProteinSession;

public class CreateDatabase {
	public CreateDatabase() {
		// InitializeComponent();
	}

	// private Size textBoxMarked = new Size(100, 20);
	// private Size textBoxContent = new Size(268,20);
	// private Size checkBoxCheck = new Size(15,14);
	class FromFileFTStruct {
		public String ID;
		public String Marked;
		public int Seq1;
		public int Seq2;
		public String Content;

		public String getID() {
			return ID;
		}

		public void setID(String iD) {
			ID = iD;
		}

		public String getMarked() {
			return Marked;
		}

		public void setMarked(String marked) {
			Marked = marked;
		}

		public int getSeq1() {
			return Seq1;
		}

		public void setSeq1(int seq1) {
			Seq1 = seq1;
		}

		public int getSeq2() {
			return Seq2;
		}

		public void setSeq2(int seq2) {
			Seq2 = seq2;
		}

		public String getContent() {
			return Content;
		}

		public void setContent(String content) {
			Content = content;
		}

	}

	private FileProcess filePro = new FileProcess();

	private Map<String, Map<String, String>> dictFlatText = new HashMap<String, Map<String, String>>();
	private Map<String, List<String>> dictFTMark = new HashMap<String, List<String>>();
	private List<FromFileFTStruct> lstFromFileFTStruct = new ArrayList<FromFileFTStruct>();

	private void GetDataInfo(File proteinFile, boolean seqFlag) {
		try {
			// Clear and Initialized

			// Read the DataFile
			filePro.getTxtContent(proteinFile, dictFlatText, dictFTMark,
					seqFlag);
			for (String pair : dictFTMark.keySet()) {
				List<String> lst = dictFTMark.get(pair);
				String ID = pair;

				this.FindMarked(pair, "INIT_MET");
				this.FindMarked(pair, "MOD_RES");
				this.FindMarked(pair, "VARIANT");
				this.FindMarked(pair, "CONFLICT");
				this.FindMarked(pair, "VAR_SEQ");
				this.FindMarked(pair, "NON_SEQ");
				this.FindMarked(pair, "UNSURE");
			}

			/*
			 * CreateModResControl(); CreatePolymorphismControl();
			 * 
			 * this.txtDataFile.Text = sPath;
			 */
		} catch (Exception e) {
			// MessageBox.Show("The file format is not correct",
			// "ProteinGoggle", MessageBoxButtons.OK, MessageBoxIcon.Warning);
			// this.txtDataFile.Text = "";
		}

	}

	private void FindMarked(String pair, String sMarked) {
		String sPreMod_Res = "";
		String sContent = "";
		String sSeq = "";
		FromFileFTStruct fromFileFTStruct = null;

		List<String> lstFT = dictFTMark.get(pair);
		String ID = pair;

		for (int i = 0; i < lstFT.size(); i++) {
			if (lstFT.get(i).indexOf(sMarked) > -1) {
				sPreMod_Res = sMarked;
				sContent = lstFT.get(i);
				sSeq = lstFT.get(i);

				fromFileFTStruct = new FromFileFTStruct();
				fromFileFTStruct.ID = ID;
				fromFileFTStruct.Marked = sMarked;
				// //////////////////// zhoum modify Begin 2013 01 01
				/*
				 * fromFileFTStruct.Seq1 = int.Parse(sSeq.SubString(0,
				 * 15).Replace(sMarked, "").Trim()); fromFileFTStruct.Seq2 =
				 * int.Parse(sSeq.SubString(15).Trim());
				 */
				String pattern = " +";
				String replacement = "";
				System.out.println(sSeq + "******");
				String contents = sSeq.replaceAll(pattern, replacement);
				System.out.println(contents + "~~~~~~~");
				String[] contentSplit = sSeq.replaceAll(pattern, ";")
						.split(";");
				fromFileFTStruct.Seq1 = Integer.parseInt(contentSplit[1]);
				fromFileFTStruct.Seq2 = Integer.parseInt(contentSplit[2]);
				// //////////////////////// zhoum modify end 2013 01 01

				fromFileFTStruct.Content = sContent;
				lstFromFileFTStruct.add(fromFileFTStruct);
				continue;
			} else if (lstFT.get(i).indexOf(sMarked) == -1
					&& sPreMod_Res == sMarked
					&& lstFT.get(i).substring(0, sMarked.length()).trim()
							.equals("")) {
				sPreMod_Res = sMarked;
				// //////////////////////// zhoum modify begin 2013 01 01
				// sContent = lstFT.get(i).SubString(29);
				sContent = lstFT.get(i);
				String pattern = " +";
				String replacement = "";
				String contents = sContent.replaceAll(pattern, replacement);
				String[] contentSplit = sContent.replaceAll(pattern, ";")
						.split(";");
				sContent = contentSplit[contentSplit.length - 1];
				// //////////////////////// zhoum modify end 2013 01 01

				fromFileFTStruct = lstFromFileFTStruct.get(lstFromFileFTStruct
						.size() - 1);
				fromFileFTStruct.Content += sContent;

				continue;
			} else {
				sPreMod_Res = "";
			}
		}
	}

	private String DataBaseFilter(String sDatabaseName) {
		String sReturnDataBase;
		sReturnDataBase = sDatabaseName.replaceAll("\\s", "");
		sReturnDataBase = sReturnDataBase.replace("-", "");
		sReturnDataBase = sReturnDataBase.replace(",", "");
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sReturnDataBase += "_" + sdf.format(d);

		return sReturnDataBase;
	}

	private String ProcessRemoveSeq(String ID, List<Integer> remove) {
		String sSeq = this.dictFlatText.get(ID).get("SQ");

		for (int v : remove) {
			if (v == 1)
				sSeq = sSeq.substring(1);
			else if (v == sSeq.length())
				sSeq = sSeq.substring(0, v - 2);
			else if (v > 1 && v < sSeq.length())
				sSeq = sSeq.substring(0, v - 2) + sSeq.substring(v);

		}

		return sSeq;
	}

	private String FormatID(String ID) {
		String sTemp = ID.split(";")[0];
		sTemp = sTemp.split(" ")[0].trim();
		return sTemp;
	}

	@Test
	public void LoadProteinConfig() throws SAXException, IOException,
			ParserConfigurationException {
		Table.ModResConfig.clear();
		File f = new File("src/ModResColor.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(f);
		NodeList nl = doc.getElementsByTagName("ModRes");

		for (int i = 0; i < nl.getLength(); i++) {

			ProteinModResConfig pmrc = new ProteinModResConfig();
			pmrc.Name = doc.getElementsByTagName("Name").item(i)
					.getFirstChild().getNodeValue();
			pmrc.ShortName = doc.getElementsByTagName("ShortName").item(i)
					.getFirstChild().getNodeValue();
			pmrc.Formula = doc.getElementsByTagName("Formula").item(i)
					.getFirstChild().getNodeValue();
			pmrc.Color = doc.getElementsByTagName("Color").item(i)
					.getFirstChild().getNodeValue();
			Table.ModResConfig.add(pmrc);
			// System.out.println( pmrc.Name);
		}

		// ProteinGoggle.BLL.Table.ModResConfig.AddRange(query);
		Table.ModResSymbolTable = Table.makeModResSymbolTable();
		Table.ModResAminoAcidsTable = Table.makeModResAminoAcidsTable();
	}

	public void Excute(File proteinFile) throws Exception {

		ElementPeaksDAL elementPeaksDAL = new ElementPeaksDAL();
		Map<String, PeaksData> elementPeaks = elementPeaksDAL.getElementPeaks();

		Map<String, List<String>> dictModResWithSeq = null;
		List<String> lstSeq = null;
		List<String> lstModRes = null;
		List<String> lstModResSingle = null;
		String sDatabaseName = "";
		String ID = "";
		String sSeq = "";
		// this.LockControl(false);
		int iCnt = 0;
		String sStartTime = "";
		String sEndTime = "";
		LoadProteinConfig();
		GetDataInfo(proteinFile, true);
		try {
			// Read element dictionary to make
			// Dictionary<String, SortedSet<Peak>> elementDic = getElements();

			// if (elementDic == null)
			// {
			// Environment.Exit(0);
			// }
			// Database Name
			File file = proteinFile;

			sDatabaseName = DataBaseFilter(file.getName().substring(0,
					file.getName().lastIndexOf(".")));
			System.out.println(sDatabaseName);
			// StartTime
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			sStartTime = sdf.format(d);
			// CreateDatabase
			DataBaseBLL dbBLL = new DataBaseBLL(sDatabaseName);
			dbBLL.CreateDataBaseWithTable();
			// LoadProteinConfig();
			for (String pair : dictFlatText.keySet()) {

				// ID Primary key
				ID = pair;

				// Sequence
				sSeq = dictFlatText.get(pair).get("SQ");
				System.out.println(sSeq);
				List<Integer> remove = new ArrayList<Integer>();
				for (FromFileFTStruct f3ts : lstFromFileFTStruct) {
					if (f3ts.ID == ID && f3ts.Marked == "INIT_MET")
						remove.add(f3ts.Seq1);
				}
				remove.sort(Collections.reverseOrder());

				sSeq = ProcessRemoveSeq(ID, remove); // FT 标定删除氨基酸
				// ProcessRemoveVariation(ID, remove); 用户可选多态Polymorphism，功能暂存
				// ProcessRemoveModRes(ID, remove); 用户可选PTMS，功能暂存

				List<String> lstSeqKbn = new ArrayList<String>();
				List<FromFileFTStruct> lstSeqMark = new ArrayList<FromFileFTStruct>();

				lstSeq = new ArrayList<String>();
				// ProcessSeq(ID, sSeq, lstSeq, lstSeqKbn, lstSeqMark);功能
				// 暂不实现【。不做操作，等同与以下
				lstSeq.add(sSeq);
				lstSeqKbn.add("");
				lstSeqMark.add(null);
				// 】*/
				dictModResWithSeq = new HashMap<String, List<String>>();
				for (int iSeq = 0; iSeq < lstSeq.size(); iSeq++) {
					/*
					 * Map<Integer, String> dictModRes = ProcessModRes(ID,
					 * lstSeqMark[iSeq]);
					 * 
					 * if (iSeq == 0) lstModResSingle =
					 * ProcessModResSingle(lstSeq[iSeq], dictModRes);
					 */
					lstModRes = new ArrayList<String>();
					lstModRes.add("");
					/*
					 * for (int i = 0; i < dictModRes.Count; i++) {
					 * ModResCompose(i, lstSeq[iSeq], dictModRes, lstModRes); }
					 * 
					 * List<String> lsttemp = lstModRes.Distinct().ToList();
					 */
					dictModResWithSeq.put(lstSeq.get(iSeq), lstModRes);
				}

				// Session 设置
				ProteinSession session = new ProteinSession();

				session.ID = FormatID(pair);
				session.Sequence = lstSeq;
				session.SeqKbn = lstSeqKbn;
				session.ValenceState = 10;
				session.FastCalc = 100;
				session.Mod_Res = dictModResWithSeq;
				session.DataBase = sDatabaseName;

				// System.out.println(lstSeq.get(0));
				DataBaseParameter dbParameter = new DataBaseParameter();
				// dbParameter.StartRange = (int)this.numStart.Value;
				// dbParameter.EndRange = (int)this.numEnd.Value; 待数据流
				dbParameter.StartRange = 500;
				dbParameter.EndRange = 2000;

				session.DBParameter = dbParameter;

				// PeptideBLL

				// dictFlatText.get(pair).put("ModResSingle", String.join("@",
				// lstModResSingle));
				dictFlatText.get(pair).put("ID", FormatID(pair));

				PeptideBLL peptideBLL = new PeptideBLL(session);

				peptideBLL.Excute(dictFlatText.get(pair));

				// PeptideModResBLL

				// 一级质谱
				PeptideModResBLL peptideModeRes = new PeptideModResBLL(session);
				// peptideModeRes.ProgressBra = this.progressBar1;
				peptideModeRes.Excute();

				// iCnt++;
				// this.progressBar1.Value += 5 / this.dictFlatText.Count *
				// iCnt;
				//
				// //PeptideDissociationBLL

				// 二级质谱
				PeptideDissociationBLL peptideDissociation = new PeptideDissociationBLL(
						session);
				// peptideDissociation.ProgressBra = this.progressBar1;
				peptideDissociation.setProteinCount(this.dictFlatText.size());
				peptideDissociation.Excute(elementPeaks);

			}

			// EndTime
			System.out.println((new Date()).getTime() - d.getTime());
			d = new Date();
			sEndTime = sdf.format(d);

			PeptideBLL peptideTime = new PeptideBLL(sDatabaseName);
			peptideTime.UpdateCreateTime(sStartTime, sEndTime);
			//
			// if (this.progressBar1.Value < 100)
			// this.progressBar1.Value = 100;
			//
			// MessageBox.Show("Import Success", "ProteinGoggle",
			// MessageBoxButtons.OK, MessageBoxIcon.Information);
			// this.Close();
		} catch (Exception ex) {
			// DataBaseBLL bll = new DataBaseBLL();

			try {
				// bll.DeleteDataBase(sDatabaseName);
			} catch (Exception e) {
			}

			throw ex;
		} finally {
			// this.LockControl(true);
			// this.progressBar1.Value = 0;
		}
	}

	// private Thread gbExcuteThread = null;

	
}
