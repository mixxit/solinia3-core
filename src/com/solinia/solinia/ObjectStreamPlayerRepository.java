package com.solinia.solinia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IPlayerRepository;
import com.solinia.solinia.Interfaces.ISoliniaPlayer;

public class ObjectStreamPlayerRepository implements IPlayerRepository {
	private String filePath;
	
	public ObjectStreamPlayerRepository(String filePath)
	{
		this.filePath = filePath;
	}

	@Override
	public void setPlayers(List<ISoliniaPlayer> players) {
		Gson gson = new Gson();
		String jsonOutput = gson.toJson(players, new TypeToken<List<SoliniaPlayer>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	@Override
	public List<ISoliniaPlayer> getPlayers() {
		List<ISoliniaPlayer> players = new ArrayList<ISoliniaPlayer>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			players = gson.fromJson(br, new TypeToken<List<SoliniaPlayer>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return players;
	}

}
