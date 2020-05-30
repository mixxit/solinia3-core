package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Models.EQItem;

public class JsonImportItemsRepository implements IRepository<EQItem>  {
	private ConcurrentHashMap<Integer, EQItem> ImportItems = new ConcurrentHashMap<Integer, EQItem>();
	private String filePath;

	@Override
	public void reload() {
		List<EQItem> readObj = new ArrayList<EQItem>();
		Gson gson = new Gson();
		
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try
			{
			    if (file.isFile() && file.getName().endsWith(".json")) {
			        BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+file.getName()));
			        readObj.add(gson.fromJson(br, new TypeToken<EQItem>(){}.getType()));
					br.close();
			    }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		ImportItems.clear();
		for(EQItem i : readObj)
		{
			ImportItems.put((int)i.getId(), i);
		}
		
		System.out.println("Reloaded " + ImportItems.size() + " importitems");
	}
	

	@Override
	public void commit() {
		
	}

	@Override
	public void add(EQItem item) {
	}

	@Override
	public void add(Iterable<EQItem> items) {
	}

	@Override
	public void update(EQItem item) {
	}

	@Override
	public void remove(EQItem item) {
	}

	@Override
	public void remove(Predicate<EQItem> filter) {
	}

	@Override
	public List<EQItem> query(Predicate<EQItem> filter) {
		return ImportItems.values().stream().filter(filter).collect(Collectors.toList());
	}
	
	public void setJsonFile(String jsonFile)
	{
		this.filePath = jsonFile;
	}
	
	@Override
	public EQItem getByKey(Object key) {
		return this.ImportItems.get(key);
	}


	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
