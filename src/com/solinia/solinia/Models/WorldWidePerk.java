package com.solinia.solinia.Models;

import java.sql.Timestamp;

public class WorldWidePerk {
	private int id;
	private String perkname;
	private String endtime;
	private String contributor;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPerkname() {
		return perkname;
	}
	public void setPerkname(String perkname) {
		this.perkname = perkname;
	}
	public String getContributor() {
		return contributor;
	}
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
}
