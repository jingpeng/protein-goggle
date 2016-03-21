/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tongji.proteingoggle.external;

import java.util.Comparator;

/**
 * 
 * @author mpcusack
 */
public class PeakPComparator implements Comparator<Peak> {

	public int compare(Peak o1, Peak o2) {
		return Double.compare(o2.getP(), o1.getP());
	}
}