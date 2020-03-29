package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaGod;
import com.solinia.solinia.Models.SoliniaGod;

public class JsonGodRepository implements IRepository<ISoliniaGod> {
	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaGod> Gods = new ConcurrentHashMap<Integer, ISoliniaGod>();

	@Override
	public void add(ISoliniaGod item) {
		this.Gods.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaGod> items) {
		for(ISoliniaGod i : items)
		{
			this.Gods.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaGod item) {
		this.Gods.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaGod item) {
		this.Gods.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaGod> filter) {
		for(ISoliniaGod i : Gods.values().stream().filter(filter).collect(Collectors.toList()))
		{
			Gods.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaGod> query(Predicate<ISoliniaGod> filter) {
		return Gods.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaGod> file = new ArrayList<ISoliniaGod>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaGod>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Gods.clear();
		for(ISoliniaGod i : file)
		{
			Gods.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + Gods.size() + " Gods");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(Gods.values(), new TypeToken<List<SoliniaGod>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + Gods.size() + " Gods");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}
	
	@Override
	public ISoliniaGod getByKey(Object key) {
		return this.Gods.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
