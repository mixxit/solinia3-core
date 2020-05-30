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
import com.solinia.solinia.Models.EQMob;

public class JsonImportNPCsRepository implements IRepository<EQMob>  {
	private ConcurrentHashMap<Integer, EQMob> ImportNPCs = new ConcurrentHashMap<Integer, EQMob>();
	private String filePath;

	@Override
	public void reload() {
		List<EQMob> readObj = new ArrayList<EQMob>();
		Gson gson = new Gson();
		
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			try
			{
			    if (file.isFile() && file.getName().endsWith(".json")) {
			        BufferedReader br = new BufferedReader(new FileReader(filePath+"/"+file.getName()));
			        readObj.add(gson.fromJson(br, new TypeToken<EQMob>(){}.getType()));
					br.close();
			    }
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		ImportNPCs.clear();
		for(EQMob i : readObj)
		{
			ImportNPCs.put((int)i.getId(), i);
		}
		
		System.out.println("Reloaded " + ImportNPCs.size() + " ImportNPCs");
	}
	

	@Override
	public void commit() {
		
	}

	@Override
	public void add(EQMob item) {
	}

	@Override
	public void add(Iterable<EQMob> items) {
	}

	@Override
	public void update(EQMob item) {
	}

	@Override
	public void remove(EQMob item) {
	}

	@Override
	public void remove(Predicate<EQMob> filter) {
	}

	@Override
	public List<EQMob> query(Predicate<EQMob> filter) {
		return ImportNPCs.values().stream().filter(filter).collect(Collectors.toList());
	}
	
	public void setJsonFile(String jsonFile)
	{
		this.filePath = jsonFile;
	}
	
	@Override
	public EQMob getByKey(Object key) {
		return this.ImportNPCs.get(key);
	}


	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
