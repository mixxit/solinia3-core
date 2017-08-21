package com.solinia.solinia;

import java.util.UUID;


import com.solina.solinia.Interfaces.ISoliniaPlayer;

public class SoliniaPlayer implements ISoliniaPlayer {

	private static final long serialVersionUID = 9075039437399478391L;
	private UUID _uuid;
	private String _forename;
	private String _lastname;

	@Override
	public UUID getUUID() {
		return _uuid;
	}
	
	@Override
	public void setUUID(UUID uuid) {
		_uuid = uuid;
	}

	@Override
	public String getForename() {
		return _forename;
	}

	@Override
	public void setForename(String _forename) {
		this._forename = _forename;
	}

	@Override
	public String getLastname() {
		return _lastname;
	}

	@Override
	public void setLastname(String _lastname) {
		this._lastname = _lastname;
	}

}
