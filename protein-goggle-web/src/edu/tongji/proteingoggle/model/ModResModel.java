package edu.tongji.proteingoggle.model;

import java.util.List;

public class ModResModel {
	 public String ID ;
     public String Mod_Res ;
     public String SEQKBN ;
     public String Sequence ;
     public int Z ;
     public double M_Z ;
     public double M ;
     public List<Double> M_Z_ALL ;
     public List<Double> M_ALL ;
     public String Forumla ;
     public boolean IsGet ;
     public List<MassPoint> Mass_Point ;
     public List<MassPoint> Mass_Point_FromFile ;

//     public object Clone()
//     {
//         MemoryStream ms = new MemoryStream();
//         BinaryFormatter bf = new BinaryFormatter();
//         bf.Serialize(ms, this);
//         ms.Position = 0;
//         object obj = bf.Deserialize(ms);
//         ms.Close();
//
//         return obj;
//     }
}
