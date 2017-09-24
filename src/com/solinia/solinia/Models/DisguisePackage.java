package com.solinia.solinia.Models;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;

public class DisguisePackage {
	private DisguiseType disguisetype;
	private String description;
	private String disguisedata;

	public DisguisePackage(DisguiseType disguiseType, String disguiseDescription) {
		setDisguisetype(disguiseType);
		setDescription(disguiseDescription);
	}

	public DisguisePackage(DisguiseType disguiseType, String disguiseDescription, String disguiseData) {
		setDisguisetype(disguiseType);
		setDescription(disguiseDescription);
		setDisguisedata(disguiseData);
	}

	public DisguiseType getDisguisetype() {
		return disguisetype;
	}

	public void setDisguisetype(DisguiseType disguisetype) {
		this.disguisetype = disguisetype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisguisedata() {
		return disguisedata;
	}

	public void setDisguisedata(String disguisedata) {
		this.disguisedata = disguisedata;
	}

}
