package com.solinia.solinia.Models;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	public Timestamp getEndtimeAsTimestamp()
	{
		Timestamp timestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date parsedDate;
			parsedDate = dateFormat.parse(getEndtime());
		    timestamp = new java.sql.Timestamp(parsedDate.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return timestamp;
	}
}
