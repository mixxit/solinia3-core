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
import com.solinia.solinia.Interfaces.ISoliniaAlignment;
import com.solinia.solinia.Models.SoliniaAlignment;

public class JsonAlignmentRepository implements IRepository<ISoliniaAlignment> {
	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaAlignment> alignments = new ConcurrentHashMap<Integer, ISoliniaAlignment>();

	@Override
	public void add(ISoliniaAlignment item) {
		this.alignments.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaAlignment> items) {
		for(ISoliniaAlignment i : items)
		{
			this.alignments.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaAlignment item) {
		this.alignments.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaAlignment item) {
		this.alignments.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaAlignment> filter) {
		for(ISoliniaAlignment i : alignments.values().stream().filter(filter).collect(Collectors.toList()))
		{
			alignments.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaAlignment> query(Predicate<ISoliniaAlignment> filter) {
		return alignments.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaAlignment> file = new ArrayList<ISoliniaAlignment>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaAlignment>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		alignments.clear();
		for(ISoliniaAlignment i : file)
		{
			alignments.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + alignments.size() + " Alignments");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		//gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(alignments.values(), new TypeToken<List<SoliniaAlignment>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + alignments.size() + " Alignments");
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
	public ISoliniaAlignment getByKey(Object key) {
		return this.alignments.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
