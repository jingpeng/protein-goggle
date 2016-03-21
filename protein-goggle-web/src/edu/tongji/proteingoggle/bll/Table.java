package edu.tongji.proteingoggle.bll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import edu.tongji.proteingoggle.model.ProteinModResConfig;
public class Table {
	 public static final Double H = 1.00782d;

     public static List<ProteinModResConfig> ModResConfig = new ArrayList<ProteinModResConfig>();
     public static Map<String, String> ModResSymbolTable;
     public static Map<String, Map<String, Integer>> ModResAminoAcidsTable;

     private static List<String> MolecularElements = 
    		 new ArrayList<String>(){/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			{add("C"); add("H"); add("O"); add("S"); add("P");add("N");}};
//    		 ("C", "H", "N", "O", "S", "P" ) ;

     public static Map<String, Map<String, Integer>> makeAminoAcidsTable()
     {
         Map<String, Map<String, Integer>> newAminoAcidTable = new HashMap<String, Map<String, Integer>>();

         Map<String, Integer> map = new HashMap<String, Integer>();
         newAminoAcidTable.put("A", map);
         map.put("C", 3);
         map.put("H", 7);
         map.put("N", 1);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("R", map);
         map.put("C", 6);
         map.put("H", 14);
         map.put("N", 4);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("N", map);
         map.put("C", 4);
         map.put("H", 8);
         map.put("N", 2);
         map.put("O", 3);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("D", map);
         map.put("C", 4);
         map.put("H", 7);
         map.put("N", 1);
         map.put("O", 4);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("c", map);
         map.put("C", 5);
         map.put("H", 10);
         map.put("N", 2);
         map.put("O", 3);
         map.put("S", 1);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("C", map);
         map.put("C", 3);
         map.put("H", 7);
         map.put("N", 1);
         map.put("O", 2);
         map.put("S", 1);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("E", map);
         map.put("C", 5);
         map.put("H", 9);
         map.put("N", 1);
         map.put("O", 4);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("Q", map);
         map.put("C", 5);
         map.put("H", 10);
         map.put("N", 2);
         map.put("O", 3);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("G", map);
         map.put("C", 2);
         map.put("H", 5);
         map.put("N", 1);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("H", map);
         map.put("C", 6);
         map.put("H", 9);
         map.put("N", 3);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("I", map);
         newAminoAcidTable.put("L", map);
         map.put("C", 6);
         map.put("H", 13);
         map.put("N", 1);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("K", map);
         map.put("C", 6);
         map.put("H", 14);
         map.put("N", 2);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("M", map);
         map.put("C", 5);
         map.put("H", 11);
         map.put("N", 1);
         map.put("O", 2);
         map.put("S", 1);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("F", map);
         map.put("C", 9);
         map.put("H", 11);
         map.put("N", 1);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("P", map);
         map.put("C", 5);
         map.put("H", 9);
         map.put("N", 1);
         map.put("O", 2);
         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("S", map);
         map.put("C", 3);
         map.put("H", 7);
         map.put("N", 1);
         map.put("O", 3);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("T", map);
         map.put("C", 4);
         map.put("H", 9);
         map.put("N", 1);
         map.put("O", 3);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("W", map);
         map.put("C", 11);
         map.put("H", 12);
         map.put("N", 2);
         map.put("O", 2);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("Y", map);
         map.put("C", 9);
         map.put("H", 11);
         map.put("N", 1);
         map.put("O", 3);

         map = new HashMap<String, Integer>();
         newAminoAcidTable.put("V", map);
         map.put("C", 5);
         map.put("H", 11);
         map.put("N", 1);
         map.put("O", 2);

         //map = new HashMap<String, Integer>();
         //newAminoAcidTable.put("U", map);
         //map.put("C", 4);
         //map.put("H", 7);
         //map.put("N", 1);
         //map.put("O", 2);

         //map = new HashMap<String, Integer>();
         //newAminoAcidTable.put("O", map);
         //map.put("C", 5);
         //map.put("H", 10);
         //map.put("N", 2);
         //map.put("O", 1);

         //map = new HashMap<String, Integer>();
         //newAminoAcidTable.put("Z", map);
         //map.put("C", 5);
         //map.put("H", 8);
         //map.put("N", 2);
         //map.put("O", 2);

         //map = new HashMap<String, Integer>();
         //newAminoAcidTable.put("B", map);
         //map.put("C", 4);
         //map.put("H", 6);
         //map.put("N", 2);
         //map.put("O", 2);

         //map = new HashMap<String, Integer>();
         //newAminoAcidTable.put("X", map);
         //map.put("C", 6);
         //map.put("H", 8);
         //map.put("N", 2);
         //map.put("O", 2);

         return newAminoAcidTable;
     }
     
     public static Map<String, String> makeModResSymbolTable()
     {
    	 Map<String, String> newAminoAcidTable = new HashMap<String, String>();

    	 for ( ProteinModResConfig pmrc : ModResConfig)
    	 {
    		 newAminoAcidTable.put(pmrc.Name, pmrc.ShortName);
    	 }
//         foreach (var ProteinModResConfig in ModResConfig)
//         {
//             newAminoAcidTable.Add(ProteinModResConfig.Name, ProteinModResConfig.ShortName);
//         }

         return newAminoAcidTable;
     }

     public static Map<String, Map<String, Integer>> makeModResAminoAcidsTable()
     {
         Map<String, Map<String, Integer>> newAminoAcidTable = new HashMap<String, Map<String, Integer>>();
         Map<String, String> map = null;
         String sFormula = "";

         try
         {
//             for (var ProteinModResConfig in ModResConfig)
//             {
        	 for ( ProteinModResConfig pmrc : ModResConfig)
        	 {
        		 map =new HashMap<String,String>();
        		 sFormula = pmrc.Formula;
//                 map = new Dictionary<String, String>();
//                 sFormula = ProteinModResConfig.Formula;
        		 
//                 Console.WriteLine(ProteinModResConfig.ShortName);
//                 String sContent = String.Empty;
        		 String sContent = "";
        		 String key = "";
        		 Boolean bFirst = false;
//                 String key = String.Empty;
//                 bool bFirst = false;
//        		 System.out.println(sFormula);
                 for (int i = 0; i < sFormula.length(); i++)
                 {
                     sContent = "";
//                     System.out.println(i);
//                     System.out.println(sFormula.substring(i, i+1));
                     if (MolecularElements.contains(sFormula.substring(i, i+1)))
                     {	
//                    	 System.out.println(true);
                         key = sFormula.substring(i, i+1);
                         map.put(key, "1");
                         bFirst = true;
                     }
                     else
                     {
                         if (bFirst == true)
                         {
//                             map[key] = String.Empty;
                             map.put(key,"");
                             bFirst = false;
                         }
                         map.put(key, map.get(key)+sFormula.substring(i, i+1));
//                         map[key] += sFormula.substring(i, 1);
                     }
//                     System.out.println(map.get(key));
                 }
//
//                 newAminoAcidTable.Add(ProteinModResConfig.ShortName, map.ToDictionary(ms => ms.Key, ms => int.Parse(ms.Value)));
                 Map<String, Integer> mapInt =new HashMap<String, Integer>();  
                 for(String k : map.keySet()){
                	// System.out.println(map.get(k));
                	 mapInt.put(k, Integer.parseInt(map.get(k)));
                 }
                 newAminoAcidTable.put(pmrc.ShortName, mapInt);
                 }
                 

        	
         }
         catch (Exception e)
         {
             
             throw e;
         }



         return newAminoAcidTable;
     }
     
     public static Map<String, Integer> makeETDCXTable()
     {
    	 Map<String, Integer> newETDTable = new HashMap<String, Integer>();
         newETDTable.put("N", 1);
         newETDTable.put("H", 1);
         newETDTable.put("O", -1);
         return newETDTable;
     }
     public static Map<String, Integer> makeETDYZTable()
     {
    	 Map<String, Integer> newETDTable = new HashMap<String, Integer>();
         newETDTable.put("N", -1);
         newETDTable.put("H", -2);
         return newETDTable;
     }

     public static Map<String, Integer> makeHCDBXTable()
     {
    	 Map<String, Integer> newHCDTable = new HashMap<String, Integer>();
         newHCDTable.put("O", -1);
         newHCDTable.put("H", -2);
         return newHCDTable;
     }

}
