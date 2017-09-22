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
import com.solinia.solinia.Factories.ISoliniaAARankTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaClass;
import com.solinia.solinia.Models.SoliniaAARank;
import com.solinia.solinia.Models.SoliniaClass;

public class JsonClassRepository implements IRepository<ISoliniaClass> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaClass> classes = new ConcurrentHashMap<Integer, ISoliniaClass>();

	@Override
	public void add(ISoliniaClass item) {
		this.classes.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaClass> items) {
		for(ISoliniaClass i : items)
		{
			this.classes.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaClass item) {
		this.classes.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaClass item) {
		this.classes.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaClass> filter) {
		for(ISoliniaClass i : classes.values().stream().filter(filter).collect(Collectors.toList()))
		{
			classes.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaClass> query(Predicate<ISoliniaClass> filter) {
		return classes.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaClass> file = new ArrayList<ISoliniaClass>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaClass>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		classes.clear();
		for(ISoliniaClass i : file)
		{
			classes.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + classes.size() + " classes");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(classes.values(), new TypeToken<List<SoliniaClass>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + classes.size() + " classes");
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

}
