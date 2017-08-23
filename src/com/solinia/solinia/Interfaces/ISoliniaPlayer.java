package com.solinia.solinia.Interfaces;

import java.io.Serializable;
import java.util.UUID;

import com.solinia.solinia.Exceptions.CoreStateInitException;

public interface ISoliniaPlayer extends Serializable {
	public UUID getUUID();

	public void setUUID(UUID uuid);

	public String getForename();

	public void setForename(String forename);

	public String getLastname();

	public void setLastname(String lastname);

	public void updateDisplayName();

	public ISoliniaEntity getEntity() throws CoreStateInitException;

	String getFullName();
}
