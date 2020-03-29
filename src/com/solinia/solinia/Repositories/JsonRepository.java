package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Factories.ISoliniaLootTableEntryTypeAdapterFactory;
import com.solinia.solinia.Interfaces.IPersistable;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaLootTable;
import com.solinia.solinia.Models.SoliniaLootTable;
import com.solinia.solinia.Models.SoliniaLootTableEntry;

public class JsonRepository<T extends IPersistable> implements IRepository<T> {
	private String filePath;
	private ConcurrentHashMap<Integer, T> loottables = new ConcurrentHashMap<Integer, T>();

	@Override
	public void add(T item) {
		this.loottables.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<T> items) {
		for(T i : items)
		{
			this.loottables.put(i.getId(), i);
		}
	}

	@Override
	public void update(T item) {
		this.loottables.put(item.getId(), item);
	}

	@Override
	public void remove(T item) {
		this.loottables.remove(item.getId());
	}

	@Override
	public void remove(Predicate<T> filter) {
		for(T i : loottables.values().stream().filter(filter).collect(Collectors.toList()))
		{
			loottables.remove(i.getId());
		}
	}

	@Override
	public List<T> query(Predicate<T> filter) {
		return loottables.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<T> file = new ArrayList<T>();
		
		try {
			GsonBuilder gsonbuilder = new GsonBuilder();
			gsonbuilder.registerTypeAdapterFactory(new ISoliniaLootTableEntryTypeAdapterFactory(SoliniaLootTableEntry.class));
			Gson gson = gsonbuilder.create();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaLootTable>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		loottables.clear();
		for(T i : file)
		{
			loottables.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + loottables.size() + " loottables");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
				GsonBuilder gsonbuilder = new GsonBuilder();
				// gsonbuilder.setPrettyPrinting();
				gsonbuilder
						.registerTypeAdapterFactory(new ISoliniaLootTableEntryTypeAdapterFactory(SoliniaLootTableEntry.class));
				gsonbuilder.setPrettyPrinting();
				Gson gson = gsonbuilder.create();
				String jsonOutput = gson.toJson(loottables.values(), new TypeToken<List<SoliniaLootTable>>() {
				}.getType());
				try {

					File file = new File(filePath);
					if (!file.exists())
						file.createNewFile();

					FileOutputStream fileOut = new FileOutputStream(file);
					OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
					outWriter.append(jsonOutput);
					outWriter.close();
					fileOut.close();
					
					System.out.println("Commited " + loottables.size() + " items of type " + this.getClass().getCanonicalName());
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
	public T getByKey(Object key) {
		return this.loottables.get(key);
	}

	@Override
	public void writeCsv(String filePath) {
		// TODO Auto-generated method stub
		
	}
}
