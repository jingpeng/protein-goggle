/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tongji.proteingoggle.external;

import edu.tongji.proteingoggle.external.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author D3Y241
 */
public class Peak implements Comparable<Peak>, Serializable {
	// private Isotope next = null;
	// private Isotope previous = null;

	private static final long serialVersionUID = 4782227867704187328l;
	private double p = 0.0;
	private double mass = 0.0;
	private double relInt = Double.NaN;

	public Peak() {
		this.mass = 0.0;
		this.p = 1.0;
	}

	public Peak(Peak peak) {
		set(peak);
	}

	public Peak(double mass, double p) {
		// this();
		setMass(mass);
		setP(p);
	}

	public int compareTo(Peak o) {
		return Double.compare(mass, o.mass);
	}

	@Override
	public int hashCode() {
		return new Double(100 * p * mass).hashCode()
				^ new Double(mass).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Peak other = (Peak) obj;
		if (this.mass != other.mass) {
			return false;
		}
		return true;
	}

	public void addP(double additionalP) {
		setP(getP(), additionalP);
	}

	public double getP() {
		return p;
	}

	public void setP(double... ps) {

		double sumP = 0.0;
		for (double p : ps) {
			sumP += p;
		}
		try {
			setP(sumP);
		} catch (RuntimeException re) {
			System.err.println(Arrays.toString(ps));
			throw re;
		}
	}

	public void setP(double p) {
		if (p >= 1.5) {

			throw new RuntimeException("Trying to set P to: " + p);
		}
		this.p = p;
	}

	/**
	 * @return the mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass
	 *            the mass to set
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}

	public String toString(boolean showIntensity) {
		if (showIntensity) {
			return "mass=" + mass + ", p=" + p;
		} else {
			return String.valueOf(getMass());
		}
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public void set(Peak other) {
		setMass(other.getMass());
		setP(other.getP());
	}

	public void doubleP() {
		p = p + p;
	}

	public double getRelInt() {
		return relInt;
	}

	public void setRelInt(double relInt) {
		this.relInt = relInt;
	}

	public String getTooltip() {
		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html><b>M/Z: </b>");
		tooltip.append(IPC.roundToDigits(getMass(), 4));
		tooltip.append("<br><b>Relative Intensity: </b>");
		tooltip.append(IPC.roundToDigits(getRelInt() * 100, 2));
		tooltip.append("%<br><b>Percent of Total: </b>");
		tooltip.append(IPC.roundToDigits(getP() * 100, 2));
		tooltip.append("%</html>");
		return tooltip.toString();
	}

	public static void setRelIntensities(SortedSet<Peak> peaks) {
		double maxP = 0.0;
		for (Peak p : peaks) {
			if (p.getP() > maxP) {
				maxP = p.getP();
			}
		}
		for (Peak p : peaks) {
			p.setRelInt(p.getP() / maxP);
		}
	}

	public static TreeSet<Peak> copyPeakSet(TreeSet<Peak> oldSet) {
		TreeSet<Peak> newSet = new TreeSet<Peak>();
		for (Peak p : oldSet) {
			newSet.add(new Peak(p));
		}
		return newSet;
	}
}