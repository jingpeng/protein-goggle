package edu.tongji.proteingoggle.bll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common {
	 public static String[] getModResForArray(String sModRes)
     {
         if (sModRes.trim().equals("")||sModRes==null)
             return new String[]{};

         if (sModRes.substring(sModRes.length() - 1).equals(";"))
             sModRes = sModRes.substring(0, sModRes.length() - 1);

         String strComma ="";
         String[] arrComma = sModRes.split(";");
         List<String> list = new ArrayList<String>();

         for(int i = 0;i < arrComma.length;i++)
         {
             strComma = arrComma[i];
             list.add(strComma.split(",")[1]);
         }

         return (String[]) list.toArray();
     }
	 
	 public static Map<Integer, String> getModResForDictionay(String sModRes)
     {
         if (sModRes.trim().equals(""))
             return new HashMap<Integer, String>();

         if (sModRes.substring(sModRes.length() - 1).equals(";"))
             sModRes = sModRes.substring(0, sModRes.length() - 1);

         String[] arrComma = sModRes.split(";");
         String strComma ="";
         String sLocationContent = "";
         int iLocation = 0;
         Map<Integer, String> dict = new HashMap<Integer, String>();

         for (int i = 0; i < arrComma.length; i++)
         {
             strComma = arrComma[i];
             sLocationContent = strComma.split(",")[0];
             iLocation = Integer.parseInt(sLocationContent.substring(1));
//             dict[iLocation] = strComma.Split(',')[1];
             dict.put(iLocation, strComma.split(",")[1]);
         }

         return dict;
     }
}
