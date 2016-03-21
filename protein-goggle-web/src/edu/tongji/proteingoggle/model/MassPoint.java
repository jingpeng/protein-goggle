package edu.tongji.proteingoggle.model;

public class MassPoint {
	 private int index;
     private double intensityPercentage;
     private double intensity;
 
     private double mass;
     private boolean isSearch = true;
     private int matchIndex = 0;
     private int comPared = 0;
  
     private double deviation;
     private double ipmd_r;
     private double ipad_r;

     private double ipadom_r;
     private double ipmdom_r;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getIntensityPercentage() {
		return intensityPercentage;
	}
	public void setIntensityPercentage(double intensityPercentage) {
		this.intensityPercentage = intensityPercentage;
	}
	public double getIntensity() {
		return intensity;
	}
	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	public boolean isSearch() {
		return isSearch;
	}
	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}
	public int getMatchIndex() {
		return matchIndex;
	}
	public void setMatchIndex(int matchIndex) {
		this.matchIndex = matchIndex;
	}
	public int getComPared() {
		return comPared;
	}
	public void setComPared(int comPared) {
		this.comPared = comPared;
	}
	public double getDeviation() {
		return deviation;
	}
	public void setDeviation(double deviation) {
		this.deviation = deviation;
	}
	public double getIpmd_r() {
		return ipmd_r;
	}
	public void setIpmd_r(double ipmd_r) {
		this.ipmd_r = ipmd_r;
	}
	public double getIpad_r() {
		return ipad_r;
	}
	public void setIpad_r(double ipad_r) {
		this.ipad_r = ipad_r;
	}
	public double getIpadom_r() {
		return ipadom_r;
	}
	public void setIpadom_r(double ipadom_r) {
		this.ipadom_r = ipadom_r;
	}
	public double getIpmdom_r() {
		return ipmdom_r;
	}
	public void setIpmdom_r(double ipmdom_r) {
		this.ipmdom_r = ipmdom_r;
	}
  
}
