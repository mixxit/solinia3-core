package com.solina.solinia.Interfaces;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectStreamPlayerRepository implements IPlayerRepository {

	@Override
	public void setPlayers(ConcurrentHashMap<UUID, ISoliniaPlayer> players) {
		try {
		      FileOutputStream fileOutStream = new FileOutputStream("players.obj");
		      ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutStream);
		      objOutStream.writeObject(players.values());
		      objOutStream.flush();
		      objOutStream.close();
		    } catch (Exception exception) {
		      System.out.println("Could not serialize: " + exception);
		    }
	}

}
