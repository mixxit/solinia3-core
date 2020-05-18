package com.solinia.solinia.Interfaces;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

public interface IPersistable extends Serializable {
	public int getId();
	public void setId(int id);
	public UUID getPrimaryUUID();
	public void setPrimaryUUID(UUID uuid);
	public UUID getSecondaryUUID();
	public void setSecondaryUUID(UUID uuid);
}
